package Christmas;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import Christmas.ChristmasConfig.ChristmasConfigLoader;
import Christmas.Commands.ChristmasCommand;
import Christmas.Listeners.ChristmasListener;
import Christmas.Metrics.Metrics;
import Christmas.Util.ChristmasUtil;

public class Christmas extends JavaPlugin {

	public ChristmasListener listener;
	public ChristmasUtil util;
	public ChristmasConfigLoader ccl;
	public ChristmasCommand cmd;
	public boolean sendMessage;
	public String prefix = ChatColor.AQUA + "[" + ChatColor.GREEN + "Christmas" + ChatColor.AQUA + "] " + ChatColor.WHITE;
	public final String String = (new SimpleDateFormat("dd").format(new Date()));

	/* Enable the Plugin */
	public void onEnable() {

		/* Metrics */
		try {
			Metrics m = new Metrics(this);
			
			if(m.isOptOut()) {
				System.out.println("[Christmas] Metrics disabled!");
			} else {
				System.out.println("[Christmas] This Plugin is using Metrics by Hidendra!");
				m.start();
			}
		} catch (IOException e) {
			System.out.println("[Christmas] An Error occured while start Metrics." + e);
		}
		//Test
		/* get Util */
		util = new ChristmasUtil(this);

		/* Config */
		ccl = new ChristmasConfigLoader(this);

		/* Manage Configs*/
		ccl.load();

		/* Command */
		cmd = new ChristmasCommand(this);
		getCommand("christmas").setExecutor(cmd);

		/* Register Events */
		listener = new ChristmasListener(this);

		/* Load config.yml*/
		loadConfig();
		/* Start Scheduler */
		if (ccl.getConfig().getConfigurationSection("ChristmasSign") != null) {
			util.startScheduler(this);
		}
		/* Output Message */
		System.out.println("[Christmas] Plugin successfully loaded.");
	}

	/* Disable the Plugin */
	public void onDisable() {
		System.out.println("[Christmas] Plugin successful unloaded.");
	}

	/* Manage config.yml*/
	public void loadConfig() {

		FileConfiguration config;
		if (new File("plugins/Christmas/config.yml").exists()) {
			config = this.getConfig();
			config.options().copyDefaults(true);
			System.out.println("[Christmas] Config successfully loaded.");
		} else {
			saveDefaultConfig();
			config = this.getConfig();
			System.out.println("[Christmas] New config file has been successfully created.");
		}
		getConfig().options().header("How to add the Items you'll get when you open a door: \nExample: - 35,14,2 \nWith this you will get: Item with ID 35 and the data value 14 (Red Wool) and amount 2 :) Understand it?");
		saveConfig();
	}
}
