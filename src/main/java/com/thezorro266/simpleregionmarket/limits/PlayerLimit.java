package com.thezorro266.simpleregionmarket.limits;

import com.thezorro266.simpleregionmarket.SimpleRegionMarket;
import com.thezorro266.simpleregionmarket.TokenManager;
import com.thezorro266.simpleregionmarket.signs.TemplateMain;

public class PlayerLimit extends Limit {

	public PlayerLimit(String name, String tag) {
		super(name, tag);
	}

	public int getLimit(String player) {
		return getLimitEntry("player.all." + player);
	}

	public void setLimit(int newLimit, String player) {
		final String key = "player.all." + player;
		if (newLimit == DISABLED) {
			SimpleRegionMarket.limitHandler.limitEntries.remove(key);
		} else {
			SimpleRegionMarket.limitHandler.limitEntries.put(key, newLimit);
		}
	}

	public boolean checkLimit(String player) {
		final int limitEntry = getLimit(player);
		if (limitEntry != INFINITE && limitEntry != DISABLED) {
			return countPlayerRegions(player) < limitEntry;
		}
		return true;
	}

	public int countPlayerRegions(String player) {
		int count = 0;
		for (final TemplateMain token : TokenManager.tokenList) {
			count += ((PlayerTokenLimit) SimpleRegionMarket.limitHandler.getLimitClassByName("playertoken")).countPlayerRegions(player, token);
		}
		return count;
	}
}
