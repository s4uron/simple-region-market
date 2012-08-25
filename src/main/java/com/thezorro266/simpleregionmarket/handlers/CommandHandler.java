package com.thezorro266.simpleregionmarket.handlers;

import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.thezorro266.simpleregionmarket.MorePageDisplay;
import com.thezorro266.simpleregionmarket.SimpleRegionMarket;
import com.thezorro266.simpleregionmarket.TokenManager;
import com.thezorro266.simpleregionmarket.Utils;
import com.thezorro266.simpleregionmarket.signs.TemplateMain;

public class CommandHandler implements CommandExecutor {
	private final LanguageHandler langHandler;
	private final SimpleRegionMarket plugin;

	/**
	 * Instantiates a new command handler.
	 * 
	 * @param plugin
	 *            the plugin
	 * @param langHandler
	 *            the lang handler
	 */
	public CommandHandler(SimpleRegionMarket plugin, LanguageHandler langHandler) {
		this.plugin = plugin;
		this.langHandler = langHandler;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = null;
		Boolean isConsole = true;
		if (sender instanceof Player) {
			player = (Player) sender;
			isConsole = false;
		}

		if (args.length < 1) {
			return false;
		}

		if (args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("v")) {
			if (player != null) {
				langHandler.playerDirectOut(player, ChatColor.YELLOW, "loaded version " + plugin.getDescription().getVersion() + ",  " + Utils.getCopyright());
			} else {
				langHandler.consoleDirectOut(Level.INFO, "loaded version " + plugin.getDescription().getVersion() + ",  " + Utils.getCopyright());
			}
		} else if (args[0].equalsIgnoreCase("release")) {
			if (isConsole || SimpleRegionMarket.permManager.isAdmin(player)) {
				if (args.length < 2) {
					if (isConsole) {
						langHandler.consoleOut("CMD.RELEASE.NO_ARG", Level.INFO, null);
					} else {
						langHandler.playerListOut(player, "CMD.RELEASE.NO_ARG", null);
					}
					return true;
				} else {
					final String region = args[1];
					String world;
					if (args.length > 2) {
						world = args[2];
					} else {
						if (isConsole) {
							langHandler.consoleOut("COMMON.CONSOLE_NOWORLD", Level.SEVERE, null);
							return true;
						} else {
							world = player.getWorld().getName();
						}
					}
					Boolean found = false;
					for (final TemplateMain token : TokenManager.tokenList) {
						if (Utils.getEntry(token, world, region, "taken") != null) {
							if (Utils.getEntryBoolean(token, world, region, "taken")) {
								token.releaseRegion(world, region);
								found = true;
								break;
							}
						}
					}
					final ArrayList<String> list = new ArrayList<String>();
					list.add(region);
					list.add(world);
					if (found) {
						if (isConsole) {
							langHandler.consoleOut("CMD.RELEASE.SUCCESS", Level.INFO, list);
						} else {
							langHandler.playerNormalOut(player, "CMD.RELEASE.SUCCESS", list);
						}
					} else {
						if (isConsole) {
							langHandler.consoleOut("COMMON.NO_REGION", Level.WARNING, list);
						} else {
							langHandler.playerErrorOut(player, "COMMON.NO_REGION", list);
						}
					}
				}
			} else {
				langHandler.playerErrorOut(player, "PLAYER.NO_PERMISSIONS.NORM", null);
			}
		} else if (args[0].equalsIgnoreCase("remove")) {
			if (isConsole || SimpleRegionMarket.permManager.isAdmin(player)) {
				if (args.length < 2) {
					if (isConsole) {
						langHandler.consoleOut("CMD.REMOVE.NO_ARG", Level.INFO, null);
					} else {
						langHandler.playerListOut(player, "CMD.REMOVE.NO_ARG", null);
					}
					return true;
				} else {
					final String region = args[1];
					String world;
					if (args.length > 2) {
						world = args[2];
					} else {
						if (isConsole) {
							langHandler.consoleOut("COMMON.CONSOLE_NOWORLD", Level.SEVERE, null);
							return true;
						} else {
							world = player.getWorld().getName();
						}
					}
					Boolean found = false;
					for (final TemplateMain token : TokenManager.tokenList) {
						if (Utils.getEntry(token, world, region, "taken") != null) {
							if (Utils.getEntryBoolean(token, world, region, "taken")) {
								token.releaseRegion(world, region);
								Utils.removeRegion(token, world, region);
								found = true;
								break;
							}
						}
					}
					final ArrayList<String> list = new ArrayList<String>();
					list.add(region);
					list.add(world);
					if (found) {
						if (isConsole) {
							langHandler.consoleOut("CMD.REMOVE.SUCCESS", Level.INFO, list);
						} else {
							langHandler.playerNormalOut(player, "CMD.REMOVE.SUCCESS", list);
						}
					} else {
						if (isConsole) {
							langHandler.consoleOut("COMMON.NO_REGION", Level.WARNING, list);
						} else {
							langHandler.playerErrorOut(player, "COMMON.NO_REGION", list);
						}
					}
				}
			} else {
				langHandler.playerErrorOut(player, "PLAYER.NO_PERMISSIONS.NORM", null);
			}
		} else if (args[0].equalsIgnoreCase("list")) { // TODO Can list own and rented regions
			if (player == null) {
				langHandler.consoleDirectOut(Level.INFO, "Not yet implemented");
			} else {
				langHandler.playerDirectOut(player, ChatColor.BLUE, "Not yet implemented");
			}
		} else if (args[0].equalsIgnoreCase("limits") || args[0].equalsIgnoreCase("limit")) { // TODO set/get limits command
			MorePageDisplay chatDisplay = new MorePageDisplay(new String[]{"<Limits>"}, "Limits for all kind of stuff");
			chatDisplay.display(sender);
		} else if (args[0].equalsIgnoreCase("addmember")) {
			if (args.length < 3) {
				if (isConsole) {
					langHandler.consoleOut("CMD.ADDMEMBER.NO_ARG", Level.INFO, null);
				} else {
					langHandler.playerListOut(player, "CMD.ADDMEMBER.NO_ARG", null);
				}
			} else {
				final Player givenPlayer = Bukkit.getPlayer(args[1]);
				if (givenPlayer == null) {
					if (isConsole) {
						langHandler.consoleOut("COMMON.NO_PLAYER", Level.INFO, null);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_PLAYER", null);
					}
					return true;
				}
				final String region = args[2];
				String world;
				if (args.length > 3) {
					world = args[3];
				} else {
					if (isConsole) {
						langHandler.consoleOut("COMMON.CONSOLE_NOWORLD", Level.SEVERE, null);
						return true;
					} else {
						world = player.getWorld().getName();
					}
				}
				final World worldWorld = Bukkit.getWorld(world);
				if (worldWorld == null) {
					if (isConsole) {
						langHandler.consoleOut("COMMON.NO_WORLD", Level.SEVERE, null);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_WORLD", null);
					}
					return true;
				}
				final ProtectedRegion protectedRegion = SimpleRegionMarket.wgManager.getProtectedRegion(worldWorld, region);
				if (protectedRegion == null) {
					final ArrayList<String> list = new ArrayList<String>();
					list.add(region);
					list.add(world);
					if (isConsole) {
						langHandler.consoleOut("COMMON.NO_REGION", Level.SEVERE, list);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_REGION", list);
					}
					return true;
				}

				Boolean found = false;
				for (final TemplateMain token : TokenManager.tokenList) {
					if (Utils.getEntry(token, world, region, "taken") != null) {
						if (Utils.getEntryBoolean(token, world, region, "taken")) {
							if (token.canAddMember()) {
								if (isConsole || SimpleRegionMarket.permManager.canPlayerAddMember(player, token)
										|| SimpleRegionMarket.permManager.isAdmin(player)) {
									if (isConsole || Utils.getEntryString(token, world, region, "owner").equalsIgnoreCase(player.getName())
											|| SimpleRegionMarket.permManager.isAdmin(player)) {
										SimpleRegionMarket.wgManager.addMember(protectedRegion, givenPlayer);
										found = true;
										break;
									} else {
										langHandler.playerErrorOut(player, "PLAYER.ERROR.NOT_OWNER", null);
									}
								} else {
									langHandler.playerErrorOut(player, "PLAYER.NO_PERMISSIONS.ADDMEMBER", null);
								}
							} else {
								if (isConsole) {
									langHandler.consoleOut("CMD.ADDMEMBER.NO_ADDMEMBER", Level.SEVERE, null);
								} else {
									langHandler.playerErrorOut(player, "CMD.ADDMEMEBR.NO_ADDMEMBER", null);
								}
							}
						}
					}
				}

				final ArrayList<String> list = new ArrayList<String>();
				if (found) {
					list.add(givenPlayer.getName());
					list.add(region);
					list.add(world);
					if (isConsole) {
						langHandler.consoleOut("CMD.ADDMEMBER.SUCCESS", Level.INFO, list);
					} else {
						langHandler.playerNormalOut(player, "CMD.ADDMEMBER.SUCCESS", list);
					}
				} else {
					list.add(region);
					list.add(world);
					if (isConsole) {
						langHandler.consoleOut("COMMON.NO_REGION", Level.WARNING, list);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_REGION", list);
					}
				}
			}
		} else if (args[0].equalsIgnoreCase("remmember") || args[0].equalsIgnoreCase("removemember")) {
			if (args.length < 3) {
				if (isConsole) {
					langHandler.consoleOut("CMD.REMOVEMEMBER.NO_ARG", Level.INFO, null);
				} else {
					langHandler.playerListOut(player, "CMD.REMOVEMEMBER.NO_ARG", null);
				}
			} else {
				final Player givenPlayer = Bukkit.getPlayer(args[1]);
				if (givenPlayer == null) {
					if (isConsole) {
						langHandler.consoleOut("COMMON.NO_PLAYER", Level.INFO, null);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_PLAYER", null);
					}
					return true;
				}
				final String region = args[2];
				String world;
				if (args.length > 3) {
					world = args[3];
				} else {
					if (isConsole) {
						langHandler.consoleOut("COMMON.CONSOLE_NOWORLD", Level.SEVERE, null);
						return true;
					} else {
						world = player.getWorld().getName();
					}
				}
				final World worldWorld = Bukkit.getWorld(world);
				if (worldWorld == null) {
					if (isConsole) {
						langHandler.consoleOut("COMMON.NO_WORLD", Level.SEVERE, null);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_WORLD", null);
					}
					return true;
				}
				final ProtectedRegion protectedRegion = SimpleRegionMarket.wgManager.getProtectedRegion(worldWorld, region);
				if (protectedRegion == null) {
					final ArrayList<String> list = new ArrayList<String>();
					list.add(region);
					list.add(world);
					if (isConsole) {
						langHandler.consoleOut("COMMON.NO_REGION", Level.SEVERE, list);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_REGION", list);
					}
					return true;
				}

				Boolean found = false;
				for (final TemplateMain token : TokenManager.tokenList) {
					if (Utils.getEntry(token, world, region, "taken") != null) {
						if (Utils.getEntryBoolean(token, world, region, "taken")) {
							if (token.canAddMember()) {
								if (isConsole || SimpleRegionMarket.permManager.canPlayerAddMember(player, token)
										|| SimpleRegionMarket.permManager.isAdmin(player)) {
									if (isConsole || Utils.getEntryString(token, world, region, "owner").equalsIgnoreCase(player.getName())
											|| SimpleRegionMarket.permManager.isAdmin(player)) {
										SimpleRegionMarket.wgManager.removeMember(protectedRegion, givenPlayer);
										found = true;
										break;
									} else {
										langHandler.playerErrorOut(player, "PLAYER.ERROR.NOT_OWNER", null);
									}
								} else {
									langHandler.playerErrorOut(player, "PLAYER.NO_PERMISSIONS.ADDMEMBER", null);
								}
							} else {
								if (isConsole) {
									langHandler.consoleOut("CMD.ADDMEMBER.NO_ADDMEMBER", Level.SEVERE, null);
								} else {
									langHandler.playerErrorOut(player, "CMD.ADDMEMEBR.NO_ADDMEMBER", null);
								}
							}
						}
					}
				}

				final ArrayList<String> list = new ArrayList<String>();
				if (found) {
					list.add(givenPlayer.getName());
					list.add(region);
					list.add(world);
					if (isConsole) {
						langHandler.consoleOut("CMD.REMOVEMEMBER.SUCCESS", Level.INFO, list);
					} else {
						langHandler.playerNormalOut(player, "CMD.REMOVEMEMBER.SUCCESS", list);
					}
				} else {
					list.add(region);
					list.add(world);
					if (isConsole) {
						langHandler.consoleOut("COMMON.NO_REGION", Level.WARNING, list);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_REGION", list);
					}
				}
			}
		} else if (args[0].equalsIgnoreCase("addowner")) {
			if (args.length < 3) {
				if (isConsole) {
					langHandler.consoleOut("CMD.ADDOWNER.NO_ARG", Level.INFO, null);
				} else {
					langHandler.playerListOut(player, "CMD.ADDOWNER.NO_ARG", null);
				}
			} else {
				final Player givenPlayer = Bukkit.getPlayer(args[1]);
				if (givenPlayer == null) {
					if (isConsole) {
						langHandler.consoleOut("COMMON.NO_PLAYER", Level.INFO, null);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_PLAYER", null);
					}
					return true;
				}
				final String region = args[2];
				String world;
				if (args.length > 3) {
					world = args[3];
				} else {
					if (isConsole) {
						langHandler.consoleOut("COMMON.CONSOLE_NOWORLD", Level.SEVERE, null);
						return true;
					} else {
						world = player.getWorld().getName();
					}
				}
				final World worldWorld = Bukkit.getWorld(world);
				if (worldWorld == null) {
					if (isConsole) {
						langHandler.consoleOut("COMMON.NO_WORLD", Level.SEVERE, null);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_WORLD", null);
					}
					return true;
				}
				final ProtectedRegion protectedRegion = SimpleRegionMarket.wgManager.getProtectedRegion(worldWorld, region);
				if (protectedRegion == null) {
					final ArrayList<String> list = new ArrayList<String>();
					list.add(region);
					list.add(world);
					if (isConsole) {
						langHandler.consoleOut("COMMON.NO_REGION", Level.SEVERE, list);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_REGION", list);
					}
					return true;
				}

				Boolean found = false;
				for (final TemplateMain token : TokenManager.tokenList) {
					if (Utils.getEntry(token, world, region, "taken") != null) {
						if (Utils.getEntryBoolean(token, world, region, "taken")) {
							if (token.canAddOwner()) {
								if (isConsole || SimpleRegionMarket.permManager.canPlayerAddOwner(player, token)
										|| SimpleRegionMarket.permManager.isAdmin(player)) {
									if (isConsole || Utils.getEntryString(token, world, region, "owner").equalsIgnoreCase(player.getName())
											|| SimpleRegionMarket.permManager.isAdmin(player)) {
										SimpleRegionMarket.wgManager.addOwner(protectedRegion, givenPlayer);
										found = true;
										break;
									} else {
										langHandler.playerErrorOut(player, "PLAYER.ERROR.NOT_OWNER", null);
									}
								} else {
									langHandler.playerErrorOut(player, "PLAYER.NO_PERMISSIONS.ADDOWNER", null);
								}
							} else {
								if (isConsole) {
									langHandler.consoleOut("CMD.ADDOWNER.NO_ADDOWNER", Level.SEVERE, null);
								} else {
									langHandler.playerErrorOut(player, "CMD.ADDOWNER.NO_ADDOWNER", null);
								}
							}
						}
					}
				}

				final ArrayList<String> list = new ArrayList<String>();
				if (found) {
					list.add(givenPlayer.getName());
					list.add(region);
					list.add(world);
					if (isConsole) {
						langHandler.consoleOut("CMD.ADDOWNER.SUCCESS", Level.INFO, list);
					} else {
						langHandler.playerNormalOut(player, "CMD.ADDOWNER.SUCCESS", list);
					}
				} else {
					list.add(region);
					list.add(world);
					if (isConsole) {
						langHandler.consoleOut("COMMON.NO_REGION", Level.WARNING, list);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_REGION", list);
					}
				}
			}
		} else if (args[0].equalsIgnoreCase("remowner") || args[0].equalsIgnoreCase("removeowner")) {
			if (args.length < 3) {
				if (isConsole) {
					langHandler.consoleOut("CMD.REMOVEOWNER.NO_ARG", Level.INFO, null);
				} else {
					langHandler.playerListOut(player, "CMD.REMOVEOWNER.NO_ARG", null);
				}
			} else {
				final Player givenPlayer = Bukkit.getPlayer(args[1]);
				if (givenPlayer == null) {
					if (isConsole) {
						langHandler.consoleOut("COMMON.NO_PLAYER", Level.INFO, null);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_PLAYER", null);
					}
					return true;
				}
				final String region = args[2];
				String world;
				if (args.length > 3) {
					world = args[3];
				} else {
					if (isConsole) {
						langHandler.consoleOut("COMMON.CONSOLE_NOWORLD", Level.SEVERE, null);
						return true;
					} else {
						world = player.getWorld().getName();
					}
				}
				final World worldWorld = Bukkit.getWorld(world);
				if (worldWorld == null) {
					if (isConsole) {
						langHandler.consoleOut("COMMON.NO_WORLD", Level.SEVERE, null);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_WORLD", null);
					}
					return true;
				}
				final ProtectedRegion protectedRegion = SimpleRegionMarket.wgManager.getProtectedRegion(worldWorld, region);
				if (protectedRegion == null) {
					final ArrayList<String> list = new ArrayList<String>();
					list.add(region);
					list.add(world);
					if (isConsole) {
						langHandler.consoleOut("COMMON.NO_REGION", Level.SEVERE, list);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_REGION", list);
					}
					return true;
				}

				Boolean found = false;
				for (final TemplateMain token : TokenManager.tokenList) {
					if (Utils.getEntry(token, world, region, "taken") != null) {
						if (Utils.getEntryBoolean(token, world, region, "taken")) {
							if (token.canAddOwner()) {
								if (isConsole || SimpleRegionMarket.permManager.canPlayerAddOwner(player, token)
										|| SimpleRegionMarket.permManager.isAdmin(player)) {
									if (isConsole || Utils.getEntryString(token, world, region, "owner").equalsIgnoreCase(player.getName())
											|| SimpleRegionMarket.permManager.isAdmin(player)) {
										SimpleRegionMarket.wgManager.removeOwner(protectedRegion, givenPlayer);
										found = true;
										break;
									} else {
										langHandler.playerErrorOut(player, "PLAYER.ERROR.NOT_OWNER", null);
									}
								} else {
									langHandler.playerErrorOut(player, "PLAYER.NO_PERMISSIONS.ADDOWNER", null);
								}
							} else {
								if (isConsole) {
									langHandler.consoleOut("CMD.ADDOWNER.NO_ADDOWNER", Level.SEVERE, null);
								} else {
									langHandler.playerErrorOut(player, "CMD.ADDOWNER.NO_ADDOWNER", null);
								}
							}
						}
					}
				}

				final ArrayList<String> list = new ArrayList<String>();
				if (found) {
					list.add(givenPlayer.getName());
					list.add(region);
					list.add(world);
					if (isConsole) {
						langHandler.consoleOut("CMD.REMOVEOWNER.SUCCESS", Level.INFO, list);
					} else {
						langHandler.playerNormalOut(player, "CMD.REMOVEOWNER.SUCCESS", list);
					}
				} else {
					list.add(region);
					list.add(world);
					if (isConsole) {
						langHandler.consoleOut("COMMON.NO_REGION", Level.WARNING, list);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_REGION", list);
					}
				}
			}
		} else {
			return false;
		}
		return true;
	}
}
