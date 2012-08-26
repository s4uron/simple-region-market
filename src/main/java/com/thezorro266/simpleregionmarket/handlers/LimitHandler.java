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

	public ArrayList<Limit> limitList = new ArrayList<Limit>();

	public HashMap<String, Integer> limitEntries = new HashMap<String, Integer>();

	private final LanguageHandler langHandler;

	public LimitHandler(SimpleRegionMarket plugin, TokenManager tokenManager, LanguageHandler langHandler) {
		this.langHandler = langHandler;

		registerLimits();
		load();
	}

	public void load() {
		if (LIMITS_FILE.exists()) {
			final YamlConfiguration configHandle = YamlConfiguration.loadConfiguration(LIMITS_FILE);

			for (String key : configHandle.getKeys(true)) {
				if (!configHandle.isConfigurationSection(key)) {
					final String oldKey = key;
					if (key.startsWith("player.")) {
						key = key.toLowerCase();
					}
					limitEntries.put(key, Limit.string2Limit(configHandle.getString(oldKey)));
				}
			}
		} else {
			try {
				LIMITS_FILE.createNewFile();
			} catch (final IOException e) {
				langHandler.consoleOut("LIMITS.ERROR.CREATE_LIMITSFILE");
			}
		}
	}

	public void save() throws IOException {
		final YamlConfiguration configHandle = new YamlConfiguration();

		for (final String key : limitEntries.keySet()) {
			configHandle.set(key, Limit.limit2String(limitEntries.get(key)));
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

	public Limit getLimitClassByTag(String tag) {
		final Iterator<Limit> i = limitList.iterator();
		while (i.hasNext()) {
			final Limit limit = i.next();
			if (limit.getTag().equals(tag)) {
				return limit;
			}
		}
		return null;
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
				if (((PlayerTokenLimit) curLimit).getLimit(player.getName(), token) != Limit.DISABLED) {
					if (((PlayerTokenLimit) curLimit).checkLimit(player.getName(), token)) {
						return true;
					} else {
						langHandler.playerErrorOut(player, "PLAYER.LIMITS.TOKEN_PLAYER", null);
						return false;
					}
				}
			}
			curLimit = getLimitClassByName("player");
			if (((PlayerLimit) curLimit).getLimit(player.getName()) != Limit.DISABLED) {
				if (((PlayerLimit) curLimit).checkLimit(player.getName())) {
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
						if (((ParentregionTokenLimit) curLimit).checkLimit(player.getName(), protectedRegion, token)) {
							return true;
						} else {
							langHandler.playerErrorOut(player, "PLAYER.LIMITS.TOKEN_PARENTREGION", null);
							return false;
						}
					}
				}
				curLimit = getLimitClassByName("parentregion");
				if (((ParentregionLimit) curLimit).getLimit(protectedRegion) != Limit.DISABLED) {
					if (((ParentregionLimit) curLimit).checkLimit(player.getName(), protectedRegion)) {
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
						if (((WorldTokenLimit) curLimit).checkLimit(player.getName(), world, token)) {
							return true;
						} else {
							langHandler.playerErrorOut(player, "PLAYER.LIMITS.TOKEN_WORLD", null);
							return false;
						}
					}
				}
				curLimit = getLimitClassByName("world");
				if (((WorldLimit) curLimit).getLimit(world) != Limit.DISABLED) {
					if (((WorldLimit) curLimit).checkLimit(player.getName(), world)) {
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
					if (((TokenLimit) curLimit).checkLimit(player.getName(), token)) {
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
				if (((GlobalLimit) curLimit).checkLimit(player.getName())) {
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
