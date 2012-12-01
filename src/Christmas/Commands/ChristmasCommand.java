package Christmas.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import Christmas.Christmas;
import Christmas.Util.ChristmasUtil;

public class ChristmasCommand implements CommandExecutor {

	public Christmas main;
	public ChristmasUtil util;

	public ChristmasCommand(Christmas main) {
		this.main = main;
		this.util = main.util;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("christmas")) {

			if (args.length == 0) {
				sender.sendMessage("§f=== §b[§aChristmas§b] §f===");
				sender.sendMessage("§aDevelopers: §bxapfeltortexp§f, §bDarkBlade12");
				return true;
			}
			if (args[0].equalsIgnoreCase("reload") && args.length == 1) {

				if (!(sender.hasPermission("christmas.reload"))) {
					sender.sendMessage(main.prefix + "You dont have Permissions");
					return true;
				}
				main.reloadConfig();
				main.ccl.load();
				main.ccl.save();
				sender.sendMessage(main.prefix + "Config reloaded!");
			}

		}

		return false;
	}

}
