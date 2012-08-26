package com.thezorro266.simpleregionmarket.limits;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.simpleregionmarket.SimpleRegionMarket;
import com.thezorro266.simpleregionmarket.TokenManager;
import com.thezorro266.simpleregionmarket.signs.TemplateMain;

public class ParentregionLimit extends Limit {

	public ParentregionLimit(String name, String tag) {
		super(name, tag);
	}

	public int getLimit(ProtectedRegion parentRegion) {
		return getLimitEntry("parentregion.all." + parentRegion.getId());
	}

	public void setLimit(int newLimit, ProtectedRegion parentRegion) {
		final String key = "parentregion.all." + parentRegion.getId();
		if (newLimit == DISABLED) {
			SimpleRegionMarket.limitHandler.limitEntries.remove(key);
		} else {
			SimpleRegionMarket.limitHandler.limitEntries.put(key, newLimit);
		}
	}

	public boolean checkLimit(String player, ProtectedRegion parentRegion) {
		final int limitEntry = getLimit(parentRegion);
		if (limitEntry != INFINITE && limitEntry != DISABLED) {
			return countPlayerRegions(player, parentRegion) < limitEntry;
		}
		return true;
	}

	public int countPlayerRegions(String player, ProtectedRegion parentRegion) {
		int count = 0;
		for (final TemplateMain token : TokenManager.tokenList) {
			count += ((ParentregionTokenLimit) SimpleRegionMarket.limitHandler.getLimitClassByName("parentregiontoken")).countPlayerRegions(player,
					parentRegion, token);
		}
		return count;
	}
}
