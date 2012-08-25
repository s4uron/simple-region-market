package com.thezorro266.simpleregionmarket;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ConfirmListener implements Listener {
	private static HashMap<String, MorePageDisplay> displays = new HashMap<String, MorePageDisplay>();

	public ConfirmListener(SimpleRegionMarket plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (displays.containsKey(event.getPlayer().getName())) {
			if (!displays.get(event.getPlayer().getName()).isTerminated()) {
				if (!displays.get(event.getPlayer().getName()).HandleChat("exit", event.getPlayer())) {
					event.getPlayer().sendMessage(ChatColor.RED + "Exit " + ChatColor.AQUA + "PageView" + ChatColor.RED + " first!");
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (displays.containsKey(event.getPlayer().getName())) {
			if (!displays.get(event.getPlayer().getName()).isTerminated()) {
				if (displays.get(event.getPlayer().getName()).HandleChat(event.getMessage(), event.getPlayer())) {
					event.setCancelled(true);
				}
			}
		}
	}

	public static void register(MorePageDisplay morePageDisplay, String name) {
		if (displays.containsKey(name) && !displays.get(name).isTerminated()) {
			return;
		}
		displays.put(name, morePageDisplay);
	}
}
