package com.thezorro266.simpleregionmarket.limits;

import org.bukkit.Bukkit;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.simpleregionmarket.SimpleRegionMarket;
import com.thezorro266.simpleregionmarket.signs.TemplateMain;

public class TokenLimit extends Limit {

	public TokenLimit(String name, String tag) {
		super(name, tag);
	}

	public int getLimit(TemplateMain token) {
		return getLimitEntry("template." + token.id);
	}

	public void setLimit(int newLimit, TemplateMain token) {
		final String key = "template." + token.id;
		if (newLimit == DISABLED) {
			SimpleRegionMarket.limitHandler.limitEntries.remove(key);
		} else {
			SimpleRegionMarket.limitHandler.limitEntries.put(key, newLimit);
		}
	}

	public boolean checkLimit(String player, TemplateMain token) {
		final int limitEntry = getLimit(token);
		if (limitEntry != INFINITE && limitEntry != DISABLED) {
			return countPlayerRegions(player, token) < limitEntry;
		}
		return true;
	}

	public int countPlayerRegions(String player, TemplateMain token) {
		int count = 0;
		for (final String world : token.entries.keySet()) {
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
		}
		return count;
	}
}
