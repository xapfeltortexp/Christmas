package Christmas.Listeners;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import Christmas.Christmas;
import Christmas.Util.ChristmasUtil;

public class ChristmasListener implements Listener {

	public Christmas main;
	public ChristmasUtil util;

	public ChristmasListener(Christmas main) {
		this.main = main;
		this.util = main.util;
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	String date = (new SimpleDateFormat("dd").format(new Date()));

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		Player player = event.getPlayer();
		String name = player.getName();

		main.ccl.load();

		if (!(main.ccl.getConfig().getStringList("PresentGet").contains(name))) {
			player.sendMessage(main.prefix + "You can open the Door " + ChatColor.GREEN + date + ChatColor.WHITE + "!");
		}

		return;
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {

		Player player = event.getPlayer();

		if (event.getLine(0).equalsIgnoreCase("[Christmas]")) {

			if (!(player.hasPermission("christmas.sign.create"))) {
				player.sendMessage(main.prefix + "You dont have Permissions!");
				event.getBlock().breakNaturally();
				return;
			}

			String date = (new SimpleDateFormat("dd.MM.yyyy").format(new Date()));

			event.setLine(0, "[" + ChatColor.GREEN + "Christmas" + ChatColor.BLACK + "]");
			event.setLine(1, ChatColor.AQUA + date);

			main.ccl.load();

			if (main.ccl.getConfig().getString("ChristmasSign.X") == null) {

				int x = event.getBlock().getX();
				int y = event.getBlock().getY();
				int z = event.getBlock().getZ();

				main.ccl.getConfig().set("ChristmasSign.X", x);
				main.ccl.getConfig().set("ChristmasSign.Y", y);
				main.ccl.getConfig().set("ChristmasSign.Z", z);
				player.sendMessage(main.prefix + "New Sign successful created");

				util.setRunning(true);
				util.startScheduler(60 * 60, x, y, z);

				main.ccl.save();

			} else {

				int x = event.getBlock().getX();
				int y = event.getBlock().getY();
				int z = event.getBlock().getZ();

				int mainx = main.ccl.getConfig().getInt("ChristmasSign.X");
				int mainy = main.ccl.getConfig().getInt("ChristmasSign.Y");
				int mainz = main.ccl.getConfig().getInt("ChristmasSign.Z");

				Block block = event.getPlayer().getWorld().getBlockAt(mainx, mainy, mainz);
				block.breakNaturally();
				player.sendMessage(main.prefix + "The Old Christmas Sign got destroyed!");

				main.ccl.getConfig().set("ChristmasSign.X", x);
				main.ccl.getConfig().set("ChristmasSign.Y", y);
				main.ccl.getConfig().set("ChristmasSign.Z", z);
				player.sendMessage(main.prefix + "SignLocation successful changed!");

				main.ccl.save();
			}

			main.ccl.save();
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {

		Player player = event.getPlayer();

		if (!(event.getBlock().getState() instanceof Sign)) {
			return;
		}

		Sign sign = (Sign) event.getBlock().getState();

		if (sign.getLine(0).equalsIgnoreCase("[" + ChatColor.GREEN + "Christmas" + ChatColor.BLACK + "]")) {

			util.setRunning(false);

			player.sendMessage(main.prefix + "You destroy the Christmas Sign!");

			main.ccl.load();

			main.ccl.getConfig().set("ChristmasSign.X", null);
			main.ccl.getConfig().set("ChristmasSign.Y", null);
			main.ccl.getConfig().set("ChristmasSign.Z", null);
			main.ccl.getConfig().set("ChristmasSign", null);

			main.ccl.save();
		}
	}

	@EventHandler
	public void onSignIntract(PlayerInteractEvent event) {

		Player player = event.getPlayer();

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		if (!(event.getClickedBlock().getState() instanceof Sign)) {
			return;
		}

		Sign sign = (Sign) event.getClickedBlock().getState();

		if (sign.getLine(0).equalsIgnoreCase("[" + ChatColor.GREEN + "Christmas" + ChatColor.BLACK + "]")) {

			main.ccl.load();

			Calendar cal = Calendar.getInstance();
			int day = cal.get(Calendar.DAY_OF_MONTH);

			if (day != Integer.valueOf(sign.getLine(0))) {

				double x = main.ccl.getConfig().getDouble("ChristmasSign.X");
				double y = main.ccl.getConfig().getDouble("ChristmasSign.Y");
				double z = main.ccl.getConfig().getDouble("ChristmasSign.Z");

				util.aktualisieren(x, y, z);
			}

			if (main.ccl.getConfig().getStringList("PresentGet.Day_" + day).contains(player.getName())) {
				player.sendMessage(main.prefix + "You already got your Present for today.");
				return;
			}

			if (Integer.valueOf(date) > 24) {
				player.sendMessage(main.prefix + "The Advents Calender just have 24 Doors :) Sorry");
				return;
			}

			for (int i = 1; i < 25; i++) {

				if (i == day) {

					player.sendMessage("" + i);

					String dateInConfig = "Day_" + day;

					List<String> allItems = main.getConfig().getStringList(dateInConfig + ".Items");
					List<String> allCommands = main.getConfig().getStringList(dateInConfig + ".Command");

					if (main.getConfig().getBoolean(dateInConfig + ".GiveItems", false)) {
						for (String id : allItems) {

							String[] item = id.split(",");
							int itemID = Integer.valueOf(item[0]);

							int amount = Integer.valueOf(item[1]);

							player.getInventory().addItem(new ItemStack(itemID, amount));

							util.updateInventory(player);
						}
						player.sendMessage(main.prefix + "Uhhh :) Do you like the Present for the Day " + ChatColor.GREEN + day + ChatColor.WHITE + "?");
					}

					if (main.getConfig().getBoolean(dateInConfig + ".DoCommand", false)) {
						for (String cmd : allCommands) {

							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
						}
					}
				}
			}
			
			if (main.getConfig().getBoolean("sendMessage", false)) {
				util.sendMessage(player);
			}

			main.ccl.load();

			if (main.ccl.getConfig().getString("PresentGet.Day_" + day) == null) {

				List<String> newp = new ArrayList<String>();
				newp.add(player.getName());

				main.ccl.getConfig().addDefault("PresentGet.Day_" + day, newp);
				main.ccl.save();
				newp.clear();
			}

			List<String> players = main.ccl.getConfig().getStringList("PresentGet.Day_" + day);

			players.add(player.getName());
			main.ccl.getConfig().set("PresentGet.Day_" + day, players);
			main.ccl.save();
		}
	}
}