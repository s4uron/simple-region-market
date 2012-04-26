package com.thezorro266.simpleregionmarket.handlers;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.simpleregionmarket.SimpleRegionMarket;
import com.thezorro266.simpleregionmarket.TokenManager;
import com.thezorro266.simpleregionmarket.signs.TemplateMain;

public class LimitHandler {
	private final LanguageHandler LANG_HANDLER;

	/**
	 * Serves limiting for: - Global - Worlds - Parent regions - Players
	 */
	public LimitHandler(SimpleRegionMarket plugin, LanguageHandler langHandler, TokenManager tokenManager) {
		LANG_HANDLER = langHandler;
	}

	public int countPlayerTokenRegions(TemplateMain token, Player player) {
		int count = 0;
		for (final String world : token.entries.keySet()) {
			for (final String region : token.entries.get(world).keySet()) {
				final ProtectedRegion protectedRegion = SimpleRegionMarket.wgManager.getProtectedRegion(Bukkit.getWorld(world), region);
				if (protectedRegion != null) {
					if (token.isRegionOwner(player, world, region)) {
						count++;
					}
				}
			}
		}
		LANG_HANDLER.outputConsole(Level.INFO, "Counting " + count + " regions for player " + player.getName() + " on template " + token.id + ".");
		return count;
	}

	public int countPlayerRegions(Player player) {
		int count = 0;
		for (final TemplateMain token : TokenManager.tokenList) {
			count += countPlayerTokenRegions(token, player);
		}
		LANG_HANDLER.outputConsole(Level.INFO, "Counting " + count + " regions for player " + player.getName() + ".");
		return count;
	}
}
