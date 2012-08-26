package com.thezorro266.simpleregionmarket.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
import com.thezorro266.simpleregionmarket.limits.GlobalLimit;
import com.thezorro266.simpleregionmarket.limits.Limit;
import com.thezorro266.simpleregionmarket.limits.ParentregionLimit;
import com.thezorro266.simpleregionmarket.limits.ParentregionTokenLimit;
import com.thezorro266.simpleregionmarket.limits.PlayerLimit;
import com.thezorro266.simpleregionmarket.limits.PlayerTokenLimit;
import com.thezorro266.simpleregionmarket.limits.TokenLimit;
import com.thezorro266.simpleregionmarket.limits.WorldLimit;
import com.thezorro266.simpleregionmarket.limits.WorldTokenLimit;
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
							langHandler.consoleOut("COMMON.NO_REGION_FOUND", Level.WARNING, list);
						} else {
							langHandler.playerErrorOut(player, "COMMON.NO_REGION_FOUND", list);
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
							langHandler.consoleOut("COMMON.NO_REGION_FOUND", Level.WARNING, list);
						} else {
							langHandler.playerErrorOut(player, "COMMON.NO_REGION_FOUND", list);
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
		} else if (args[0].equalsIgnoreCase("limits") || args[0].equalsIgnoreCase("limit")) {
			if (isConsole || SimpleRegionMarket.permManager.isAdmin(player)) {
				final LimitHandler limitHandler = SimpleRegionMarket.limitHandler;

				final MorePageDisplay chatDisplay = new MorePageDisplay(new String[] {
					"< " + "Limits" + " >"
				}, "limits");
				chatDisplay.append("( " + "Set or get Limits" + " )");

				final Iterator<Limit> i = limitHandler.limitList.iterator();
				while (i.hasNext()) {
					final Limit limitEntry = i.next();
					chatDisplay.append("| " + ChatColor.YELLOW + "/rm limits " + limitEntry.getName() + " (" + limitEntry.getTag() + ")");
				}

				if (args.length < 2) {
					chatDisplay.display(sender);
				} else {
					final String limitsName = args[1].toLowerCase();
					Limit limitEntry = limitHandler.getLimitClassByTag(limitsName);

					if (limitEntry == null) {
						limitEntry = limitHandler.getLimitClassByName(limitsName);
					}

					if (limitEntry != null) {
						final ArrayList<String> list = new ArrayList<String>();
						if (limitsName.equals("global") || limitsName.equals("g")) {
							list.add("global");
							if (args.length < 3) {
								list.add(Limit.limit2String(((GlobalLimit) limitEntry).getLimit()));
								if (isConsole) {
									langHandler.consoleOut("LIMITS.NORM.LIMIT_GET", Level.INFO, list);
								} else {
									langHandler.playerNormalOut(player, "LIMITS.NORM.LIMIT_GET", list);
								}
							} else {
								final int newLimit = Limit.string2Limit(args[2]);
								((GlobalLimit) limitEntry).setLimit(newLimit);
								try {
									limitHandler.save();
									list.add(Limit.limit2String(newLimit));
									if (isConsole) {
										langHandler.consoleOut("LIMITS.NORM.LIMIT_SET", Level.INFO, list);
									} else {
										langHandler.playerNormalOut(player, "LIMITS.NORM.LIMIT_SET", list);
									}
								} catch (final IOException e) {
									if (isConsole) {
										langHandler.consoleOut("LIMITS.ERROR.SAVE_LIMITSFILE");
										e.printStackTrace();
									} else {
										langHandler.playerErrorOut(player, "LIMITS.ERROR.SAVE_LIMITSFILE", null);
									}
								}
							}
						} else if (limitsName.equals("token") || limitsName.equals("t")) {
							if (args.length < 3) {
								if (isConsole) {
									langHandler.consoleOut("COMMON.NO_TEMPLATE", Level.WARNING, null);
								} else {
									langHandler.playerErrorOut(player, "COMMON.NO_TEMPLATE", null);
								}
							} else {
								TemplateMain token = null;
								for (final TemplateMain sToken : TokenManager.tokenList) {
									if (sToken.id.equalsIgnoreCase(args[2])) {
										token = sToken;
										break;
									}
								}
								if (token == null) {
									if (isConsole) {
										langHandler.consoleOut("COMMON.NO_TEMPLATE", Level.WARNING, null);
									} else {
										langHandler.playerErrorOut(player, "COMMON.NO_TEMPLATE", null);
									}
								} else {
									list.add("template " + token.id);
									if (args.length < 4) {
										list.add(Limit.limit2String(((TokenLimit) limitEntry).getLimit(token)));
										if (isConsole) {
											langHandler.consoleOut("LIMITS.NORM.LIMIT_GET", Level.INFO, list);
										} else {
											langHandler.playerNormalOut(player, "LIMITS.NORM.LIMIT_GET", list);
										}
									} else {
										final int newLimit = Limit.string2Limit(args[3]);
										((TokenLimit) limitEntry).setLimit(newLimit, token);
										try {
											limitHandler.save();
											list.add(Limit.limit2String(newLimit));
											if (isConsole) {
												langHandler.consoleOut("LIMITS.NORM.LIMIT_SET", Level.INFO, list);
											} else {
												langHandler.playerNormalOut(player, "LIMITS.NORM.LIMIT_SET", list);
											}
										} catch (final IOException e) {
											if (isConsole) {
												langHandler.consoleOut("LIMITS.ERROR.SAVE_LIMITSFILE");
												e.printStackTrace();
											} else {
												langHandler.playerErrorOut(player, "LIMITS.ERROR.SAVE_LIMITSFILE", null);
											}
										}
									}
								}
							}
						} else if (limitsName.equals("world") || limitsName.equals("w")) {
							if (args.length < 3) {
								if (isConsole) {
									langHandler.consoleOut("COMMON.NO_WORLD", Level.WARNING, null);
								} else {
									langHandler.playerErrorOut(player, "COMMON.NO_WORLD", null);
								}
							} else {
								final World world = Bukkit.getWorld(args[2]);
								if (world == null) {
									if (isConsole) {
										langHandler.consoleOut("COMMON.NO_WORLD", Level.WARNING, null);
									} else {
										langHandler.playerErrorOut(player, "COMMON.NO_WORLD", null);
									}
								} else {
									list.add("world " + world.getName());
									if (args.length < 4) {
										list.add(Limit.limit2String(((WorldLimit) limitEntry).getLimit(world.getName())));
										if (isConsole) {
											langHandler.consoleOut("LIMITS.NORM.LIMIT_GET", Level.INFO, list);
										} else {
											langHandler.playerNormalOut(player, "LIMITS.NORM.LIMIT_GET", list);
										}
									} else {
										final int newLimit = Limit.string2Limit(args[3]);
										((WorldLimit) limitEntry).setLimit(newLimit, world.getName());
										try {
											limitHandler.save();
											list.add(Limit.limit2String(newLimit));
											if (isConsole) {
												langHandler.consoleOut("LIMITS.NORM.LIMIT_SET", Level.INFO, list);
											} else {
												langHandler.playerNormalOut(player, "LIMITS.NORM.LIMIT_SET", list);
											}
										} catch (final IOException e) {
											if (isConsole) {
												langHandler.consoleOut("LIMITS.ERROR.SAVE_LIMITSFILE");
												e.printStackTrace();
											} else {
												langHandler.playerErrorOut(player, "LIMITS.ERROR.SAVE_LIMITSFILE", null);
											}
										}
									}
								}
							}
						} else if (limitsName.equals("worldtoken") || limitsName.equals("wt")) {
							if (args.length < 3) {
								if (isConsole) {
									langHandler.consoleOut("COMMON.NO_WORLD", Level.WARNING, null);
								} else {
									langHandler.playerErrorOut(player, "COMMON.NO_WORLD", null);
								}
							} else {
								final World world = Bukkit.getWorld(args[2]);
								if (world == null) {
									if (isConsole) {
										langHandler.consoleOut("COMMON.NO_WORLD", Level.WARNING, null);
									} else {
										langHandler.playerErrorOut(player, "COMMON.NO_WORLD", null);
									}
								} else {
									if (args.length < 4) {
										if (isConsole) {
											langHandler.consoleOut("COMMON.NO_TEMPLATE", Level.WARNING, null);
										} else {
											langHandler.playerErrorOut(player, "COMMON.NO_TEMPLATE", null);
										}
									} else {
										TemplateMain token = null;
										for (final TemplateMain sToken : TokenManager.tokenList) {
											if (sToken.id.equalsIgnoreCase(args[3])) {
												token = sToken;
												break;
											}
										}
										if (token == null) {
											if (isConsole) {
												langHandler.consoleOut("COMMON.NO_TEMPLATE", Level.WARNING, null);
											} else {
												langHandler.playerErrorOut(player, "COMMON.NO_TEMPLATE", null);
											}
										} else {
											list.add("world + " + world.getName() + ", template " + token.id);
											if (args.length < 5) {
												list.add(Limit.limit2String(((WorldTokenLimit) limitEntry).getLimit(world.getName(), token)));
												if (isConsole) {
													langHandler.consoleOut("LIMITS.NORM.LIMIT_GET", Level.INFO, list);
												} else {
													langHandler.playerNormalOut(player, "LIMITS.NORM.LIMIT_GET", list);
												}
											} else {
												final int newLimit = Limit.string2Limit(args[4]);
												((WorldTokenLimit) limitEntry).setLimit(newLimit, world.getName(), token);
												try {
													limitHandler.save();
													list.add(Limit.limit2String(newLimit));
													if (isConsole) {
														langHandler.consoleOut("LIMITS.NORM.LIMIT_SET", Level.INFO, list);
													} else {
														langHandler.playerNormalOut(player, "LIMITS.NORM.LIMIT_SET", list);
													}
												} catch (final IOException e) {
													if (isConsole) {
														langHandler.consoleOut("LIMITS.ERROR.SAVE_LIMITSFILE");
														e.printStackTrace();
													} else {
														langHandler.playerErrorOut(player, "LIMITS.ERROR.SAVE_LIMITSFILE", null);
													}
												}
											}
										}
									}
								}
							}
						} else if (limitsName.equals("parentregion") || limitsName.equals("pr")) {
							if (args.length < 3) {
								if (isConsole) {
									langHandler.consoleOut("COMMON.NO_REGION", Level.WARNING, null);
								} else {
									langHandler.playerErrorOut(player, "COMMON.NO_REGION", null);
								}
							} else {
								final String region = args[2];
								if (args.length < 4) {
									if (isConsole) {
										langHandler.consoleOut("COMMON.NO_WORLD", Level.WARNING, null);
									} else {
										langHandler.playerErrorOut(player, "COMMON.NO_WORLD", null);
									}
								} else {
									final World world = Bukkit.getWorld(args[3]);
									if (world == null) {
										if (isConsole) {
											langHandler.consoleOut("COMMON.NO_WORLD", Level.WARNING, null);
										} else {
											langHandler.playerErrorOut(player, "COMMON.NO_WORLD", null);
										}
									} else {
										final ProtectedRegion protectedRegion = SimpleRegionMarket.wgManager.getProtectedRegion(world, region);
										if (protectedRegion == null) {
											final ArrayList<String> list1 = new ArrayList<String>();
											list1.add(region);
											list1.add(world.getName());
											if (isConsole) {
												langHandler.consoleOut("COMMON.NO_REGION_FOUND", Level.WARNING, list1);
											} else {
												langHandler.playerErrorOut(player, "COMMON.NO_REGION_FOUND", list1);
											}
										} else {
											list.add("parentregion " + protectedRegion.getId() + ", world " + world.getName());
											if (args.length < 5) {
												list.add(Limit.limit2String(((ParentregionLimit) limitEntry).getLimit(protectedRegion)));
												if (isConsole) {
													langHandler.consoleOut("LIMITS.NORM.LIMIT_GET", Level.INFO, list);
												} else {
													langHandler.playerNormalOut(player, "LIMITS.NORM.LIMIT_GET", list);
												}
											} else {
												final int newLimit = Limit.string2Limit(args[4]);
												((ParentregionLimit) limitEntry).setLimit(newLimit, protectedRegion);
												try {
													limitHandler.save();
													list.add(Limit.limit2String(newLimit));
													if (isConsole) {
														langHandler.consoleOut("LIMITS.NORM.LIMIT_SET", Level.INFO, list);
													} else {
														langHandler.playerNormalOut(player, "LIMITS.NORM.LIMIT_SET", list);
													}
												} catch (final IOException e) {
													if (isConsole) {
														langHandler.consoleOut("LIMITS.ERROR.SAVE_LIMITSFILE");
														e.printStackTrace();
													} else {
														langHandler.playerErrorOut(player, "LIMITS.ERROR.SAVE_LIMITSFILE", null);
													}
												}
											}
										}
									}
								}
							}
						} else if (limitsName.equals("parentregiontoken") || limitsName.equals("prt")) {
							if (args.length < 3) {
								if (isConsole) {
									langHandler.consoleOut("COMMON.NO_REGION", Level.WARNING, null);
								} else {
									langHandler.playerErrorOut(player, "COMMON.NO_REGION", null);
								}
							} else {
								final String region = args[2];
								if (args.length < 4) {
									if (isConsole) {
										langHandler.consoleOut("COMMON.NO_WORLD", Level.WARNING, null);
									} else {
										langHandler.playerErrorOut(player, "COMMON.NO_WORLD", null);
									}
								} else {
									final World world = Bukkit.getWorld(args[3]);
									if (world == null) {
										if (isConsole) {
											langHandler.consoleOut("COMMON.NO_WORLD", Level.WARNING, null);
										} else {
											langHandler.playerErrorOut(player, "COMMON.NO_WORLD", null);
										}
									} else {
										final ProtectedRegion protectedRegion = SimpleRegionMarket.wgManager.getProtectedRegion(world, region);
										if (protectedRegion == null) {
											final ArrayList<String> list1 = new ArrayList<String>();
											list1.add(region);
											list1.add(world.getName());
											if (isConsole) {
												langHandler.consoleOut("COMMON.NO_REGION_FOUND", Level.WARNING, list1);
											} else {
												langHandler.playerErrorOut(player, "COMMON.NO_REGION_FOUND", list1);
											}
										} else {
											if (args.length < 5) {
												if (isConsole) {
													langHandler.consoleOut("COMMON.NO_TEMPLATE", Level.WARNING, null);
												} else {
													langHandler.playerErrorOut(player, "COMMON.NO_TEMPLATE", null);
												}
											} else {
												TemplateMain token = null;
												for (final TemplateMain sToken : TokenManager.tokenList) {
													if (sToken.id.equalsIgnoreCase(args[4])) {
														token = sToken;
														break;
													}
												}
												if (token == null) {
													if (isConsole) {
														langHandler.consoleOut("COMMON.NO_TEMPLATE", Level.WARNING, null);
													} else {
														langHandler.playerErrorOut(player, "COMMON.NO_TEMPLATE", null);
													}
												} else {
													list.add("parentregion " + protectedRegion.getId() + ", world " + world.getName() + ", template "
															+ token.id);
													if (args.length < 6) {
														list.add(Limit.limit2String(((ParentregionTokenLimit) limitEntry).getLimit(protectedRegion, token)));
														if (isConsole) {
															langHandler.consoleOut("LIMITS.NORM.LIMIT_GET", Level.INFO, list);
														} else {
															langHandler.playerNormalOut(player, "LIMITS.NORM.LIMIT_GET", list);
														}
													} else {
														final int newLimit = Limit.string2Limit(args[5]);
														((ParentregionTokenLimit) limitEntry).setLimit(newLimit, protectedRegion, token);
														try {
															limitHandler.save();
															list.add(Limit.limit2String(newLimit));
															if (isConsole) {
																langHandler.consoleOut("LIMITS.NORM.LIMIT_SET", Level.INFO, list);
															} else {
																langHandler.playerNormalOut(player, "LIMITS.NORM.LIMIT_SET", list);
															}
														} catch (final IOException e) {
															if (isConsole) {
																langHandler.consoleOut("LIMITS.ERROR.SAVE_LIMITSFILE");
																e.printStackTrace();
															} else {
																langHandler.playerErrorOut(player, "LIMITS.ERROR.SAVE_LIMITSFILE", null);
															}
														}
													}
												}
											}
										}
									}
								}
							}
						} else if (limitsName.equals("player") || limitsName.equals("p")) {
							if (args.length < 3) {
								if (isConsole) {
									langHandler.consoleOut("COMMON.NO_PLAYER", Level.WARNING, null);
								} else {
									langHandler.playerErrorOut(player, "COMMON.NO_PLAYER", null);
								}
							} else {
								final String playerX = args[2].toLowerCase();
								list.add("player " + playerX);
								if (args.length < 4) {
									list.add(Limit.limit2String(((PlayerLimit) limitEntry).getLimit(playerX)));
									if (isConsole) {
										langHandler.consoleOut("LIMITS.NORM.LIMIT_GET", Level.INFO, list);
									} else {
										langHandler.playerNormalOut(player, "LIMITS.NORM.LIMIT_GET", list);
									}
								} else {
									final int newLimit = Limit.string2Limit(args[3]);
									((PlayerLimit) limitEntry).setLimit(newLimit, playerX);
									try {
										limitHandler.save();
										list.add(Limit.limit2String(newLimit));
										if (isConsole) {
											langHandler.consoleOut("LIMITS.NORM.LIMIT_SET", Level.INFO, list);
										} else {
											langHandler.playerNormalOut(player, "LIMITS.NORM.LIMIT_SET", list);
										}
									} catch (final IOException e) {
										if (isConsole) {
											langHandler.consoleOut("LIMITS.ERROR.SAVE_LIMITSFILE");
											e.printStackTrace();
										} else {
											langHandler.playerErrorOut(player, "LIMITS.ERROR.SAVE_LIMITSFILE", null);
										}
									}
								}
							}
						} else if (limitsName.equals("playertoken") || limitsName.equals("pt")) {
							if (args.length < 3) {
								if (isConsole) {
									langHandler.consoleOut("COMMON.NO_PLAYER", Level.WARNING, null);
								} else {
									langHandler.playerErrorOut(player, "COMMON.NO_PLAYER", null);
								}
							} else {
								final String playerX = args[2];
								if (args.length < 4) {
									if (isConsole) {
										langHandler.consoleOut("COMMON.NO_TEMPLATE", Level.WARNING, null);
									} else {
										langHandler.playerErrorOut(player, "COMMON.NO_TEMPLATE", null);
									}
								} else {
									TemplateMain token = null;
									for (final TemplateMain sToken : TokenManager.tokenList) {
										if (sToken.id.equalsIgnoreCase(args[3])) {
											token = sToken;
											break;
										}
									}
									if (token == null) {
										if (isConsole) {
											langHandler.consoleOut("COMMON.NO_TEMPLATE", Level.WARNING, null);
										} else {
											langHandler.playerErrorOut(player, "COMMON.NO_TEMPLATE", null);
										}
									} else {
										list.add("player " + playerX + ", template " + token.id);
										if (args.length < 5) {
											list.add(Limit.limit2String(((PlayerTokenLimit) limitEntry).getLimit(playerX, token)));
											if (isConsole) {
												langHandler.consoleOut("LIMITS.NORM.LIMIT_GET", Level.INFO, list);
											} else {
												langHandler.playerNormalOut(player, "LIMITS.NORM.LIMIT_GET", list);
											}
										} else {
											final int newLimit = Limit.string2Limit(args[4]);
											((PlayerTokenLimit) limitEntry).setLimit(newLimit, playerX, token);
											try {
												limitHandler.save();
												list.add(Limit.limit2String(newLimit));
												if (isConsole) {
													langHandler.consoleOut("LIMITS.NORM.LIMIT_SET", Level.INFO, list);
												} else {
													langHandler.playerNormalOut(player, "LIMITS.NORM.LIMIT_SET", list);
												}
											} catch (final IOException e) {
												if (isConsole) {
													langHandler.consoleOut("LIMITS.ERROR.SAVE_LIMITSFILE");
													e.printStackTrace();
												} else {
													langHandler.playerErrorOut(player, "LIMITS.ERROR.SAVE_LIMITSFILE", null);
												}
											}
										}
									}
								}
							}
						} else {
							chatDisplay.display(sender);
						}
					}
				}
			}
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
						langHandler.consoleOut("COMMON.NO_REGION_FOUND", Level.WARNING, list);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_REGION_FOUND", list);
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
						langHandler.consoleOut("COMMON.NO_REGION_FOUND", Level.WARNING, list);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_REGION_FOUND", list);
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
						langHandler.consoleOut("COMMON.NO_REGION_FOUND", Level.WARNING, list);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_REGION_FOUND", list);
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
						langHandler.consoleOut("COMMON.NO_REGION_FOUND", Level.WARNING, list);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_REGION_FOUND", list);
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
						langHandler.consoleOut("COMMON.NO_REGION_FOUND", Level.WARNING, list);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_REGION_FOUND", list);
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
						langHandler.consoleOut("COMMON.NO_REGION_FOUND", Level.WARNING, list);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_REGION_FOUND", list);
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
						langHandler.consoleOut("COMMON.NO_REGION_FOUND", Level.WARNING, list);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_REGION_FOUND", list);
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
						langHandler.consoleOut("COMMON.NO_REGION_FOUND", Level.WARNING, list);
					} else {
						langHandler.playerErrorOut(player, "COMMON.NO_REGION_FOUND", list);
					}
				}
			}
		} else {
			return false;
		}
		return true;
	}
}
