package com.thezorro266.simpleregionmarket;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MorePageDisplay {
	static final int ROWS = 10;
	static final int CHARS = 50;

	private ArrayList<String> header = new ArrayList<String>();
	private final ArrayList<StringConnected> content = new ArrayList<StringConnected>();

	private int currentpage = 0;
	private int currentpagecount = 0;
	private boolean terminated = false;

	public MorePageDisplay(ArrayList<String> header, String name) {
		if (header.size() <= (ROWS - 2)) {
			this.header = header;
		}
		ConfirmListener.register(this, name);
	}

	public MorePageDisplay(String[] header, String name) {
		if (header.length <= (ROWS - 2)) {
			this.header = new ArrayList<String>();
			for (final String element : header) {
				this.header.add(element);
			}
		}
		ConfirmListener.register(this, name);
	}

	public int getRows() {
		return ROWS;
	}

	public int getChars() {
		return CHARS;
	}

	public void append(String input) {
		append(input, false);
	}

	public void append(String input, boolean flag) {
		content.add(new StringConnected(input, flag));
	}

	public boolean isTerminated() {
		return terminated;
	}

	public void display(CommandSender sender) {
		display(sender, 1);
	}

	public void display(Player player) {
		display(player, 1);
	}

	public void display(Player player, int page) {
		display((CommandSender) player, page);
	}

	private String ReplaceMeta(String input, int page, int count) {
		String output = "";
		int pagecount = (int) Math.ceil(((double) content.size()) / ((double) count));
		if (count == -1) {
			pagecount = 0;
		}
		if (count == -2) {
			pagecount = 1;
		}
		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
			switch (c) {
				case '%':
					output += String.valueOf(page);
					break;
				case '$':
					output += String.valueOf(pagecount);
					break;
				default:
					output += c;
					break;
			}
		}
		boolean LeftDone = false;
		boolean RightDone = false;
		String output2 = "";
		// Add = for <>
		for (int i = 0; i < output.length(); i++) {
			final char c = output.charAt(i);
			switch (c) {
				case '<':
					if (LeftDone) {
						break;
					}
					LeftDone = true;
					output2 += ChatColor.AQUA;
					for (int j = 0; j < ((CHARS - output.length()) / 2); j++) {
						output2 += "=";
					}
					output2 += ChatColor.WHITE;
					break;
				case '>':
					if (RightDone) {
						break;
					}
					RightDone = true;
					output2 += ChatColor.AQUA;
					for (int j = 0; j < ((CHARS - output.length()) / 2); j++) {
						output2 += "=";
					}
					output2 += ChatColor.WHITE;
					break;
				case '(':
					if (LeftDone) {
						break;
					}
					LeftDone = true;
					for (int j = 0; j < ((CHARS - output.length()) / 2); j++) {
						output2 += " ";
					}
					break;
				case ')':
					if (RightDone) {
						break;
					}
					RightDone = true;
					for (int j = 0; j < ((CHARS - output.length()) / 2); j++) {
						output2 += " ";
					}
					break;
				default:
					output2 += c;
					break;
			}
		}
		return output2;
	}

	//

	public boolean HandleChat(String input, CommandSender sender) {
		if (terminated) {
			return false;
		}
		if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
			terminated = true;
			for (final Object zeilenobject : header.toArray()) {
				if (!(zeilenobject instanceof String)) {
					continue;
				}
				sender.sendMessage(ReplaceMeta((String) zeilenobject, 0, -1));
			}
			clearscreen(sender, 19 - header.size());
			sender.sendMessage(ChatColor.AQUA + "Pageview: " + ChatColor.RED + "Exit.");
		} else if (input.equalsIgnoreCase("next") || input.equalsIgnoreCase("nex") || input.equalsIgnoreCase("n")) {
			if (currentpage > (currentpagecount - 1)) {
				display(sender, 0);
			} else {
				currentpage++;
				display(sender, currentpage);
			}
		} else if (input.equalsIgnoreCase("previous") || input.equalsIgnoreCase("pre") || input.equalsIgnoreCase("p")) {
			if (currentpage < 2) {
				display(sender, currentpagecount);
			} else {
				currentpage--;
				display(sender, currentpage);
			}
		} else if (isNumber(input)) {
			if (toNumber(input) <= currentpagecount && toNumber(input) > 0) {
				display(sender, toNumber(input));
			} else {
				display(sender, currentpage, true);
				sender.sendMessage(ChatColor.AQUA + "Pageview:" + ChatColor.RED + " Not a valid number.");
			}
		} else if (input.equalsIgnoreCase("reprint")) {
			display(sender, currentpage);
		} else if (input.equalsIgnoreCase("all")) {
			display(sender, currentpage, false, true);
		} else {
			display(sender, currentpage, true);
		}
		return true;
	}

	public static boolean isNumber(String input) {
		try {
			final int number = Integer.parseInt(input);
			final String numstring = String.valueOf(number);
			if (numstring.equalsIgnoreCase(input)) {
				return true;
			}
		} catch (final Exception e) {
		}
		return false;
	}

	public static int toNumber(String input) {
		try {
			return Integer.parseInt(input);
		} catch (final Exception e) {
		}
		return -1;
	}

	private void clearscreen(CommandSender sender, int count) {
		for (int i = 0; i < count; i++) {
			sender.sendMessage("|");
		}
	}

	private int countnextcontent(int start) {
		int i;
		for (i = start; i < (content.size() - 1) && content.get(i + 1).connected; i++) {
			;
		}
		return i - start;
	}

	public void display(CommandSender sender, int page) {
		display(sender, page, false, false);
	}

	public void display(CommandSender sender, int page, boolean flag) {
		display(sender, page, flag, false);
	}

	public void printLastLine(CommandSender sender) {
		printLastLine(sender, false);
	}

	public void printLastLine(CommandSender sender, boolean flag) {
		sender.sendMessage(ChatColor.AQUA + "Pageview:" + ChatColor.WHITE + " Enter " + ChatColor.RED + "Pre" + ChatColor.WHITE + "/" + ChatColor.GREEN
				+ "Next" + ChatColor.WHITE + ", a " + ChatColor.AQUA + "number" + ChatColor.WHITE + ", " + ChatColor.AQUA + "all" + ChatColor.WHITE + ", "
				+ ChatColor.AQUA + "reprint" + ChatColor.WHITE + " or " + ChatColor.RED + "exit" + ChatColor.WHITE + (flag ? "!" : "."));
	}

	public void display(CommandSender sender, int page, boolean flag, boolean all) {
		if (terminated) {
			return;
		}
		int count = ROWS - header.size();
		page = (page > 0 && !all ? page : 1);
		currentpage = page;
		final int pagecount = (int) Math.ceil(((double) content.size()) / ((double) count));
		currentpagecount = pagecount;
		if (all) {
			count = -2;
		}
		for (final Object zeilenobject : header.toArray()) {
			if (!(zeilenobject instanceof String)) {
				continue;
			}
			sender.sendMessage(ReplaceMeta((String) zeilenobject, page, count));
		}
		int i;
		for (i = count * (page - 1); i < content.size() && (i < (count * page) || all); i++) {
			if (!all) {
				final int nexttake = countnextcontent(i);
				if ((nexttake + i) >= (count * page)) {
					break;
				}
			}
			sender.sendMessage(content.get(i).content);
		}
		if (!all) {
			clearscreen(sender, (count * page) - i);
		}
		if (all && count > content.size()) {
			clearscreen(sender, count - content.size());
		}
	}

	private class StringConnected {
		StringConnected(String s, boolean b) {
			content = s;
			connected = b;
		}

		public String content;
		public boolean connected;
	}
}
