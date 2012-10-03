package dead;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.thezorro266.simpleregionmarket.SimpleRegionMarket;
import com.thezorro266.simpleregionmarket.handlers.LanguageHandler;

public class NewCommandHandler implements CommandExecutor {

	private final SimpleRegionMarket plugin;
	private final LanguageHandler langHandler;

	public NewCommandHandler(SimpleRegionMarket plugin, LanguageHandler langHandler) {
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
		List<String> userInput = new ArrayList<String>();
		userInput.add(command.getName());
		for(int i=0; i < args.length; i++) {
			userInput.add(args[i]);
		}
		
		runCommand(isConsole, player, userInput);
		return false;
	}

	private void runCommand(Boolean isConsole, Player player, List<String> userInput) {
		
	}

}
