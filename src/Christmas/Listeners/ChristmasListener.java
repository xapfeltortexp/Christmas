package Christmas.Listeners;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
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

		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_MONTH);

		if (day > 24) {
			return;
		}

		if (!(main.ccl.getConfig().getStringList("PresentGet.Day_" + day).contains(name))) {
			player.sendMessage(main.prefix + util.replaceColorCodes(main.getConfig().getString("Messages.CanOpen").replace("%number%", date)));
		}

		return;
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {

		Player player = event.getPlayer();

		if (event.getLine(0).equalsIgnoreCase("[Christmas]") || event.getLine(0).equalsIgnoreCase("[Advent]")) {

			if (!(player.hasPermission("christmas.sign.create"))) {
				player.sendMessage(main.prefix + "You dont have Permissions!");
				event.getBlock().breakNaturally();
				return;
			}

			String date = (new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
			if (event.getLine(0).equalsIgnoreCase("[Christmas]")) {
				event.setLine(0, "[" + ChatColor.GREEN + "Christmas" + ChatColor.BLACK + "]");
			} else if (event.getLine(0).equalsIgnoreCase("[Advent]")) {
				event.setLine(0, "[" + ChatColor.GREEN + "Advent" + ChatColor.BLACK + "]");
			}

			event.setLine(1, ChatColor.AQUA + date);
			main.ccl.load();

			int x = event.getBlock().getX();
			int y = event.getBlock().getY();
			int z = event.getBlock().getZ();
			String world = event.getBlock().getWorld().getName();

			if (main.ccl.getConfig().getConfigurationSection("ChristmasSign") == null) {
				player.sendMessage(main.prefix + "You created a new advent calendar sign!");
				util.startScheduler(this.main);
			} else {
				int mainx = main.ccl.getConfig().getInt("ChristmasSign.X");
				int mainy = main.ccl.getConfig().getInt("ChristmasSign.Y");
				int mainz = main.ccl.getConfig().getInt("ChristmasSign.Z");
				String mainworld = main.ccl.getConfig().getString("ChristmasSign.World");
				World w = Bukkit.getWorld(mainworld);
				Block block = w.getBlockAt(mainx, mainy, mainz);
				block.breakNaturally();
				player.sendMessage(main.prefix + "The old Christmas sign got destroyed, because a new one has been created!");
				player.sendMessage(main.prefix + "The location of the sign was successfully changed!");
			}
			main.ccl.getConfig().set("ChristmasSign.X", x);
			main.ccl.getConfig().set("ChristmasSign.Y", y);
			main.ccl.getConfig().set("ChristmasSign.Z", z);
			main.ccl.getConfig().set("ChristmasSign.World", world);
			main.ccl.save();
			util.startScheduler(main);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Player player = event.getPlayer();

		if (!(event.getBlock().getState() instanceof Sign)) {
			return;
		}

		Sign sign = (Sign) event.getBlock().getState();

		if (sign.getLine(0).equalsIgnoreCase("[" + ChatColor.GREEN + "Christmas" + ChatColor.BLACK + "]") || sign.getLine(0).equalsIgnoreCase("[" + ChatColor.GREEN + "Advent" + ChatColor.BLACK + "]")) {
			if (!(player.hasPermission("christmas.sign.create"))) {
				player.sendMessage(main.prefix + "You dont have Permissions!");
				event.getBlock().breakNaturally();
				return;
			}
			util.stopScheduler(main);
			player.sendMessage(main.prefix + "You destroyed the Christmas sign!");
			main.ccl.load();
			main.ccl.getConfig().set("ChristmasSign", null);
			main.ccl.save();
		}
	}

	@EventHandler
	public void onSignIntract(PlayerInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Player player = event.getPlayer();

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		if (!(event.getClickedBlock().getState() instanceof Sign)) {
			return;
		}

		Sign sign = (Sign) event.getClickedBlock().getState();

		if (sign.getLine(0).equalsIgnoreCase("[" + ChatColor.GREEN + "Christmas" + ChatColor.BLACK + "]") || sign.getLine(0).equalsIgnoreCase("[" + ChatColor.GREEN + "Advent" + ChatColor.BLACK + "]")) {

			main.ccl.load();

			Calendar cal = Calendar.getInstance();
			int day = cal.get(Calendar.DAY_OF_MONTH);
			Date signDate = null;
			Calendar signCal = Calendar.getInstance();
			String signLine = sign.getLine(1).replace("§b", "");

			try {
				signDate = new SimpleDateFormat("dd.MM.yyyy").parse(signLine);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			signCal.setTime(signDate);
			int signDay = signCal.get(Calendar.DAY_OF_MONTH);
			
			
			if (day != signDay) {
				sign.setLine(1, ChatColor.AQUA + new SimpleDateFormat("dd.MM.yyyy").format(cal.getTime()));
				sign.update();
				main.getLogger().log(Level.INFO, "Sign has been updated!");
			}

			if (main.ccl.getConfig().getStringList("PresentGet.Day_" + day).contains(player.getName())) {
				player.sendMessage(main.prefix + util.replaceColorCodes(main.getConfig().getString("Messages.AlreadyGot")));
				return;
			}

			if (Integer.valueOf(date) > 24) {
				player.sendMessage(main.prefix + util.replaceColorCodes(main.getConfig().getString("Messages.Only24Doors")));
				return;
			}

			for (int i = 1; i < 25; i++) {
				
				if (i == day) {
					
					String dateInConfig = "Day_" + day;
					List<String> allItems = main.getConfig().getStringList(dateInConfig + ".Items");
					List<String> allCommands = main.getConfig().getStringList(dateInConfig + ".Command");
					
					if (main.getConfig().getBoolean(dateInConfig + ".GiveItems")) {
						for (String id : allItems) {

							String[] item = id.split(",");

							if (item.length == 3) {
								int itemID = Integer.valueOf(item[0]);
								int amountID = Integer.valueOf(item[2]);
								int dataID = Integer.valueOf(item[1]);

								player.getInventory().addItem(new ItemStack(itemID, amountID, (short) dataID));
							} else if (item.length == 2) {

								int itemID = Integer.valueOf(item[0]);
								int amountID = Integer.valueOf(item[1]);

								player.getInventory().addItem(new ItemStack(itemID, amountID));
							}

							util.updateInventory(player);
						}
						player.sendMessage(main.prefix + util.replaceColorCodes(main.getConfig().getString("Messages.LikePresent").replace("%day%", day + "")));
					}

					if (main.getConfig().getBoolean(dateInConfig + ".DoCommand", false)) {
						for (String cmd : allCommands) {

							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
						}
					}
				}
			}

			if (main.getConfig().getBoolean("sendMessage", false)) {
				util.sendOpenedDoor(player);
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
