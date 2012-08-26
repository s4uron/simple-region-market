package com.thezorro266.simpleregionmarket.limits;

import com.thezorro266.simpleregionmarket.SimpleRegionMarket;
import com.thezorro266.simpleregionmarket.TokenManager;
import com.thezorro266.simpleregionmarket.signs.TemplateMain;

public class GlobalLimit extends Limit {

	public GlobalLimit(String name, String tag) {
		super(name, tag);
	}

	public int getLimit() {
		return getLimitEntry("template.all");
	}

	public void setLimit(int newLimit) {
		final String key = "template.all";
		if (newLimit == DISABLED) {
			SimpleRegionMarket.limitHandler.limitEntries.remove(key);
		} else {
			SimpleRegionMarket.limitHandler.limitEntries.put(key, newLimit);
		}
	}

	public boolean checkLimit(String player) {
		final int limitEntry = getLimit();
		if (limitEntry != INFINITE && limitEntry != DISABLED) {
			return countPlayerRegions(player) < limitEntry;
		}
		return true;
	}

	public int countPlayerRegions(String player) {
		int count = 0;
		for (final TemplateMain token : TokenManager.tokenList) {
			count += ((TokenLimit) SimpleRegionMarket.limitHandler.getLimitClassByName("token")).countPlayerRegions(player, token);
		}
		return count;
	}
}
