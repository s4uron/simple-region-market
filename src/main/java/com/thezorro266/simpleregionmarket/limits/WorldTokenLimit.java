package com.thezorro266.simpleregionmarket.limits;

import org.bukkit.Bukkit;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.simpleregionmarket.SimpleRegionMarket;
import com.thezorro266.simpleregionmarket.signs.TemplateMain;

public class WorldTokenLimit extends Limit {

	public WorldTokenLimit(String name, String tag) {
		super(name, tag);
	}

	public int getLimit(String world, TemplateMain token) {
		return getLimitEntry("world." + token.id + "." + world);
	}

	public void setLimit(int newLimit, String world, TemplateMain token) {
		final String key = "world." + token.id + "." + world;
		if (newLimit == DISABLED) {
			SimpleRegionMarket.limitHandler.limitEntries.remove(key);
		} else {
			SimpleRegionMarket.limitHandler.limitEntries.put(key, newLimit);
		}
	}

	public boolean checkLimit(String player, String world, TemplateMain token) {
		final int limitEntry = getLimit(world, token);
		if (limitEntry != INFINITE && limitEntry != DISABLED) {
			return countPlayerRegions(player, world, token) < limitEntry;
		}
		return true;
	}

	public int countPlayerRegions(String player, String world, TemplateMain token) {
		int count = 0;
		if(token.entries.containsKey(world)) {
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
