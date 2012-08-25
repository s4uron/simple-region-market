package com.thezorro266.simpleregionmarket.limits;

import org.bukkit.entity.Player;

import com.thezorro266.simpleregionmarket.SimpleRegionMarket;
import com.thezorro266.simpleregionmarket.TokenManager;
import com.thezorro266.simpleregionmarket.signs.TemplateMain;

public class GlobalLimit extends Limit {

	public GlobalLimit(String name, String tag) {
		super(name, tag);
	}

	public int getLimit() {
		return getLimitEntry("global.global");
	}

	public boolean checkLimit(Player player) {
		final int limitEntry = getLimit();
		if (limitEntry != INFINITE && limitEntry != DISABLED) {
			return countPlayerRegions(player) < limitEntry;
		}
		return true;
	}

	public int countPlayerRegions(Player player) {
		int count = 0;
		for (final TemplateMain token : TokenManager.tokenList) {
			count += ((TokenLimit) SimpleRegionMarket.limitHandler.getLimitClassByName("token")).countPlayerRegions(player, token);
		}
		return count;
	}
}
