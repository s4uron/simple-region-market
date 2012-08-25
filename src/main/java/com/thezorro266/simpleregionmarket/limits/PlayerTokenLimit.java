package com.thezorro266.simpleregionmarket.limits;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.simpleregionmarket.SimpleRegionMarket;
import com.thezorro266.simpleregionmarket.signs.TemplateMain;

public class PlayerTokenLimit extends Limit {

	public PlayerTokenLimit(String name, String tag) {
		super(name, tag);
	}

	public int getLimit(Player player, TemplateMain token) {
		return getLimitEntry(token.id + ".player." + player.getName());
	}

	public boolean checkLimit(Player player, TemplateMain token) {
		final int limitEntry = getLimit(player, token);
		if (limitEntry != INFINITE && limitEntry != DISABLED) {
			return countPlayerRegions(player, token) < limitEntry;
		}
		return true;
	}

	public int countPlayerRegions(Player player, TemplateMain token) {
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
		return count;
	}
}
