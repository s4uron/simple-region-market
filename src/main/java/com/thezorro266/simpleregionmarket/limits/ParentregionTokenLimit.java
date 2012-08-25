package com.thezorro266.simpleregionmarket.limits;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.simpleregionmarket.SimpleRegionMarket;
import com.thezorro266.simpleregionmarket.signs.TemplateMain;

public class ParentregionTokenLimit extends Limit {

	public ParentregionTokenLimit(String name, String tag) {
		super(name, tag);
	}

	public int getLimit(ProtectedRegion parentRegion, TemplateMain token) {
		return getLimitEntry(token.id + ".parentregion." + parentRegion.getId());
	}

	public boolean checkLimit(Player player, ProtectedRegion parentRegion, TemplateMain token) {
		final int limitEntry = getLimit(parentRegion, token);
		if (limitEntry != INFINITE && limitEntry != DISABLED) {
			return countPlayerRegions(player, parentRegion, token) < limitEntry;
		}
		return true;
	}

	public int countPlayerRegions(Player player, ProtectedRegion parentRegion, TemplateMain token) {
		int count = 0;
		for (final String world : token.entries.keySet()) {
			for (final String region : token.entries.get(world).keySet()) {
				final ProtectedRegion childRegion = SimpleRegionMarket.wgManager.getProtectedRegion(Bukkit.getWorld(world), region);
				if (childRegion != null && childRegion.getParent().equals(parentRegion)) {
					if (token.isRegionOwner(player, world, region)) {
						count++;
					}
				}
			}
		}
		return count;
	}
}
