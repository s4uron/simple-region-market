package com.thezorro266.simpleregionmarket.limits;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.simpleregionmarket.SimpleRegionMarket;
import com.thezorro266.simpleregionmarket.TokenManager;
import com.thezorro266.simpleregionmarket.signs.TemplateMain;

public class ParentregionLimit extends Limit {

	public ParentregionLimit(String name, String tag) {
		super(name, tag);
	}

	public int getLimit(ProtectedRegion parentRegion) {
		return getLimitEntry("global.parentregion." + parentRegion.getId());
	}

	public boolean checkLimit(Player player, ProtectedRegion parentRegion) {
		final int limitEntry = getLimit(parentRegion);
		if (limitEntry != INFINITE && limitEntry != DISABLED) {
			return countPlayerRegions(player, parentRegion) < limitEntry;
		}
		return true;
	}

	public int countPlayerRegions(Player player, ProtectedRegion parentRegion) {
		int count = 0;
		for (final TemplateMain token : TokenManager.tokenList) {
			count += ((ParentregionTokenLimit) SimpleRegionMarket.limitHandler.getLimitClassByName("parentregiontoken")).countPlayerRegions(player,
					parentRegion, token);
		}
		return count;
	}
}
