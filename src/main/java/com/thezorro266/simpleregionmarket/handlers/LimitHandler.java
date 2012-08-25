package com.thezorro266.simpleregionmarket.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.simpleregionmarket.SimpleRegionMarket;
import com.thezorro266.simpleregionmarket.TokenManager;
import com.thezorro266.simpleregionmarket.limits.GlobalLimit;
import com.thezorro266.simpleregionmarket.limits.Limit;
import com.thezorro266.simpleregionmarket.limits.ParentregionLimit;
import com.thezorro266.simpleregionmarket.limits.ParentregionTokenLimit;
import com.thezorro266.simpleregionmarket.limits.PlayerLimit;
import com.thezorro266.simpleregionmarket.limits.PlayerTokenLimit;
import com.thezorro266.simpleregionmarket.limits.TokenLimit;
import com.thezorro266.simpleregionmarket.limits.WorldLimit;
import com.thezorro266.simpleregionmarket.limits.WorldTokenLimit;
import com.thezorro266.simpleregionmarket.signs.TemplateMain;

public class LimitHandler {
	private static final String LIMITS_NAME = "limits.yml";
	private static final File LIMITS_FILE = new File(SimpleRegionMarket.getPluginDir() + LIMITS_NAME);

	private final ArrayList<Limit> limitList = new ArrayList<Limit>();

	public final HashMap<String, Integer> limitEntries = new HashMap<String, Integer>();

	private final LanguageHandler langHandler;

	public LimitHandler(SimpleRegionMarket plugin, TokenManager tokenManager, LanguageHandler langHandler) {
		this.langHandler = langHandler;

		registerLimits();
		load();
	}

	public void load() {
		final YamlConfiguration configHandle = YamlConfiguration.loadConfiguration(LIMITS_FILE);

		for (final String key : configHandle.getKeys(true)) {
			int value;
			final String tempVal = configHandle.getString(key);
			if (tempVal.equalsIgnoreCase("INFINITE")) {
				value = Limit.INFINITE;
			} else if (tempVal.equalsIgnoreCase("DISABLED")) {
				value = Limit.DISABLED;
			} else {
				value = Integer.parseInt(tempVal);
			}

			if (value < -2) {
				value = -2;
			}

			limitEntries.put(key, value);
		}
	}

	public void save() throws IOException {
		final YamlConfiguration configHandle = new YamlConfiguration();

		for (final String key : limitEntries.keySet()) {
			String value;
			final int tempVal = limitEntries.get(key);
			if (tempVal == Limit.DISABLED) {
				value = "DISABLED";
			} else if (tempVal == Limit.INFINITE) {
				value = "INFINITE";
			} else {
				value = Integer.toString(tempVal);
			}

			configHandle.set(key, value);
		}

		configHandle.save(LIMITS_FILE);
	}

	private void registerLimits() {
		limitList.add(new GlobalLimit("global", "g"));
		limitList.add(new TokenLimit("token", "t"));
		limitList.add(new WorldLimit("world", "w"));
		limitList.add(new WorldTokenLimit("worldtoken", "wt"));
		limitList.add(new ParentregionLimit("parentregion", "pr"));
		limitList.add(new ParentregionTokenLimit("parentregiontoken", "prt"));
		// limitList.add(new PermissionLimit("permission", "perm"));
		limitList.add(new PlayerLimit("player", "p"));
		limitList.add(new PlayerTokenLimit("playertoken", "pt"));
	}

	public Limit getLimitClassByName(String name) {
		final Iterator<Limit> i = limitList.iterator();
		while (i.hasNext()) {
			final Limit limit = i.next();
			if (limit.getName().equals(name)) {
				return limit;
			}
		}
		return null;
	}

	public boolean checkLimits(Player player, String world, TemplateMain token, ProtectedRegion protectedRegion) {
		if (player != null) {
			Limit curLimit = null;

			// Player limits
			if (token != null) {
				curLimit = getLimitClassByName("playertoken");
				if (((PlayerTokenLimit) curLimit).getLimit(player, token) != Limit.DISABLED) {
					if (((PlayerTokenLimit) curLimit).checkLimit(player, token)) {
						return true;
					} else {
						langHandler.playerErrorOut(player, "PLAYER.LIMITS.TOKEN_PLAYER", null);
						return false;
					}
				}
			}
			curLimit = getLimitClassByName("player");
			if (((PlayerLimit) curLimit).getLimit(player) != Limit.DISABLED) {
				if (((PlayerLimit) curLimit).checkLimit(player)) {
					return true;
				} else {
					langHandler.playerErrorOut(player, "PLAYER.LIMITS.GLOBAL_PLAYER", null);
					return false;
				}
			}

			// TODO: Permission limits (highest counts)

			// Parentregion limits
			if (protectedRegion != null) {
				if (token != null) {
					curLimit = getLimitClassByName("parentregiontoken");
					if (((ParentregionTokenLimit) curLimit).getLimit(protectedRegion, token) != Limit.DISABLED) {
						if (((ParentregionTokenLimit) curLimit).checkLimit(player, protectedRegion, token)) {
							return true;
						} else {
							langHandler.playerErrorOut(player, "PLAYER.LIMITS.TOKEN_PARENTREGION", null);
							return false;
						}
					}
				}
				curLimit = getLimitClassByName("parentregion");
				if (((ParentregionLimit) curLimit).getLimit(protectedRegion) != Limit.DISABLED) {
					if (((ParentregionLimit) curLimit).checkLimit(player, protectedRegion)) {
						return true;
					} else {
						langHandler.playerErrorOut(player, "PLAYER.LIMITS.GLOBAL_PARENTREGION", null);
						return false;
					}
				}
			}

			// World limits
			if (world != null) {
				if (token != null) {
					curLimit = getLimitClassByName("worldtoken");
					if (((WorldTokenLimit) curLimit).getLimit(world, token) != Limit.DISABLED) {
						if (((WorldTokenLimit) curLimit).checkLimit(player, world, token)) {
							return true;
						} else {
							langHandler.playerErrorOut(player, "PLAYER.LIMITS.TOKEN_WORLD", null);
							return false;
						}
					}
				}
				curLimit = getLimitClassByName("world");
				if (((WorldLimit) curLimit).getLimit(world) != Limit.DISABLED) {
					if (((WorldLimit) curLimit).checkLimit(player, world)) {
						return true;
					} else {
						langHandler.playerErrorOut(player, "PLAYER.LIMITS.GLOBAL_WORLD", null);
						return false;
					}
				}
			}

			// Token limit
			if (token != null) {
				curLimit = getLimitClassByName("token");
				if (((TokenLimit) curLimit).getLimit(token) != Limit.DISABLED) {
					if (((TokenLimit) curLimit).checkLimit(player, token)) {
						return true;
					} else {
						langHandler.playerErrorOut(player, "PLAYER.LIMITS.TOKEN", null);
						return false;
					}
				}
			}

			// Global limit
			curLimit = getLimitClassByName("global");
			if (((GlobalLimit) curLimit).getLimit() != Limit.DISABLED) {
				if (((GlobalLimit) curLimit).checkLimit(player)) {
					return true;
				} else {
					langHandler.playerErrorOut(player, "PLAYER.LIMITS.GLOBAL", null);
					return false;
				}
			}
		}
		return true; // If none of the limits is set
	}
}
