package com.thezorro266.simpleregionmarket.limits;

import com.thezorro266.simpleregionmarket.SimpleRegionMarket;

public abstract class Limit {
	public static final int INFINITE = -1;
	public static final int DISABLED = -2;

	private final String name;
	private final String tag;

	public Limit(String name, String tag) {
		this.name = name;
		this.tag = tag;
	}

	public String getName() {
		return name;
	}

	public String getTag() {
		return tag;
	}

	protected int getLimitEntry(String key) {
		if (SimpleRegionMarket.limitHandler.limitEntries.containsKey(key)) {
			try {
				return Integer.parseInt(SimpleRegionMarket.limitHandler.limitEntries.get(key).toString());
			} catch (final Exception e) {
			}
		}
		return Limit.DISABLED;
	}
}
