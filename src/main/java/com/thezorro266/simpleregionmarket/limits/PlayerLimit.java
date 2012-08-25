package com.thezorro266.simpleregionmarket.limits;

import org.bukkit.entity.Player;

import com.thezorro266.simpleregionmarket.SimpleRegionMarket;
import com.thezorro266.simpleregionmarket.TokenManager;
import com.thezorro266.simpleregionmarket.signs.TemplateMain;

public class PlayerLimit extends Limit {

	public PlayerLimit(String name, String tag) {
		super(name, tag);
	}

	public int getLimit(Player player) {
		return getLimitEntry("global.player." + player.getName());
	}

	public boolean checkLimit(Player player) {
		final int limitEntry = getLimit(player);
		if (limitEntry != INFINITE && limitEntry != DISABLED) {
			return countPlayerRegions(player) < limitEntry;
		}
		return true;
	}

	public int countPlayerRegions(Player player) {
		int count = 0;
		for (final TemplateMain token : TokenManager.tokenList) {
			count += ((PlayerTokenLimit) SimpleRegionMarket.limitHandler.getLimitClassByName("playertoken")).countPlayerRegions(player, token);
		}
		return count;
	}
}
