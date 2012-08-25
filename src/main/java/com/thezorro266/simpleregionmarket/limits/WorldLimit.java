package com.thezorro266.simpleregionmarket.limits;

import org.bukkit.entity.Player;

import com.thezorro266.simpleregionmarket.SimpleRegionMarket;
import com.thezorro266.simpleregionmarket.TokenManager;
import com.thezorro266.simpleregionmarket.signs.TemplateMain;

public class WorldLimit extends Limit {

	public WorldLimit(String name, String tag) {
		super(name, tag);
	}

	public int getLimit(String world) {
		return getLimitEntry("global.world." + world);
	}

	public boolean checkLimit(Player player, String world) {
		final int limitEntry = getLimit(world);
		if (limitEntry != INFINITE && limitEntry != DISABLED) {
			return countPlayerRegions(player, world) < limitEntry;
		}
		return true;
	}

	public int countPlayerRegions(Player player, String world) {
		int count = 0;
		for (final TemplateMain token : TokenManager.tokenList) {
			count += ((WorldTokenLimit) SimpleRegionMarket.limitHandler.getLimitClassByName("worldtoken")).countPlayerRegions(player, world, token);
		}
		return count;
	}
}
