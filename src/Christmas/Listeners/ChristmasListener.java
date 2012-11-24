package Christmas.Listeners;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String name = player.getName();
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		main.ccl.load();
		Configuration cconfig = main.ccl.getConfig();
		Configuration config = main.getConfig();
		List<String> presents = cconfig.getStringList("PresentGet");
		if (presents != null) {
			for (String ep : cconfig.getStringList("PresentGet")) {
				if (ep.equalsIgnoreCase(name)) {
					return;
				}
			}
		}
		int aday = -1;
		for (String sub : config.getKeys(false)) {
			String index = sub;
			int iday = Integer.parseInt(index.replace("Day", ""));
			if (iday == day) {
				aday = iday;
			}
		}
		if (aday == -1) {
			return;
		}
		Boolean giveItems = config.getBoolean("Day" + aday + ".GiveItems");
		Boolean doCommand = config.getBoolean("Day" + aday + ".DoCommand");
		if (giveItems == true) {
			List<String> ilist = config.getStringList("Day" + aday + ".Items");
			for (String istr : ilist) {
				String[] split = istr.split(",");
				int id = 0;
				int amount = 0;
				int counter = 1;
				for (String part : split) {
					if (counter == 1) {
						id = Integer.parseInt(part);
					}
					if (counter == 2) {
						amount = Integer.parseInt(part);
					}
					counter++;
				}
				if (amount == 0) {
					amount = 1;
				}
				ItemStack is = new ItemStack(id, amount);
				player.getInventory().addItem(is);
			}
		}
		if (doCommand == true) {
			String cmd = config.getString("Day" + aday + ".Command");
			cmd.replace("%player%", name);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}
		presents.add(name);
		cconfig.set("PresentGet", presents);
		main.ccl.save();
		return;
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {

		Player player = event.getPlayer();

		double x = event.getBlock().getX();
		double y = event.getBlock().getY();
		double z = event.getBlock().getZ();

		if (event.getLine(0).equalsIgnoreCase("[Christmas]")) {

			if (!(player.hasPermission("christmas.sign.create"))) {
				player.sendMessage(main.prefix + "You dont have Permissions!");
				event.getBlock().breakNaturally();
				return;
			}

			String date = (new SimpleDateFormat("dd.mm.yyyy").format(new Date()));

			event.setLine(0, "[" + ChatColor.GREEN + "Christmas" + ChatColor.BLACK + "]");
			event.setLine(1, ChatColor.AQUA + date);

			main.ccl.load();

			if (main.ccl.getConfig().getString("ChristmasSign.X") == null) {
				main.ccl.getConfig().addDefault("ChristmasSign.X", x);
				main.ccl.getConfig().addDefault("ChristmasSign.Y", y);
				main.ccl.getConfig().addDefault("ChristmasSign.Z", z);
				player.sendMessage(main.prefix + "New Sign successful created");
			} else {
				main.ccl.getConfig().set("ChristmasSign.X", x);
				main.ccl.getConfig().set("ChristmasSign.Y", y);
				main.ccl.getConfig().set("ChristmasSign.Z", z);
				player.sendMessage(main.prefix + "SignLocation successful changed!");
			}
			
			main.ccl.save();
		}
	}
	
	@EventHandler
	public void onSignIntract(PlayerInteractEvent event) {
	
		Player player = event.getPlayer();
		
		if(!(event.getClickedBlock().getState() instanceof Sign)) {
			return;
		}
		
		Sign sign = (Sign) event.getClickedBlock().getState();
		
		if(sign.getLine(0).equalsIgnoreCase("[" + ChatColor.GREEN + "Christmas" + ChatColor.BLACK + "]")) {
			
			if(main.ccl.getConfig().getStringList("PresentGet").contains(player.getName())) {
				player.sendMessage(main.prefix + "You already got your Present for today.");
				return;
			}
			
			String date = sign.getLine(1);
			String[] newdate = date.split(".");
 		}
	}
}