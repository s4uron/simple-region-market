package com.thezorro266.simpleregionmarket.limits;

import org.bukkit.Bukkit;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.simpleregionmarket.SimpleRegionMarket;
import com.thezorro266.simpleregionmarket.signs.TemplateMain;

public class ParentregionTokenLimit extends Limit {

	public ParentregionTokenLimit(String name, String tag) {
		super(name, tag);
	}

	public int getLimit(ProtectedRegion parentRegion, TemplateMain token) {
		return getLimitEntry("parentregion." + token.id + "." + parentRegion.getId());
	}

	public void setLimit(int newLimit, ProtectedRegion parentRegion, TemplateMain token) {
		final String key = "parentregion." + token.id + "." + parentRegion.getId();
		if (newLimit == DISABLED) {
			SimpleRegionMarket.limitHandler.limitEntries.remove(key);
		} else {
			SimpleRegionMarket.limitHandler.limitEntries.put(key, newLimit);
		}
	}

	public boolean checkLimit(String player, ProtectedRegion parentRegion, TemplateMain token) {
		final int limitEntry = getLimit(parentRegion, token);
		if (limitEntry != INFINITE && limitEntry != DISABLED) {
			return countPlayerRegions(player, parentRegion, token) < limitEntry;
		}
		return true;
	}

	public int countPlayerRegions(String player, ProtectedRegion parentRegion, TemplateMain token) {
		int count = 0;
		for (final String world : token.entries.keySet()) {
			if(token.entries.containsKey(world)) {
				for (final String region : token.entries.get(world).keySet()) {
					final ProtectedRegion childRegion = SimpleRegionMarket.wgManager.getProtectedRegion(Bukkit.getWorld(world), region);
					if (childRegion != null && childRegion.getParent().equals(parentRegion)) {
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
