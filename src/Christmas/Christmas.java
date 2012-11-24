package Christmas;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import Christmas.ChristmasConfig.ChristmasConfigLoader;
import Christmas.Listeners.ChristmasListener;
import Christmas.Util.ChristmasUtil;

public class Christmas extends JavaPlugin {

	public ChristmasListener listener;
	public ChristmasUtil util;
	public ChristmasConfigLoader ccl = new ChristmasConfigLoader(this);
	
	public boolean sendMessage;
	
	public String prefix = ChatColor.AQUA + "[" + ChatColor.GREEN + "Christmas" + ChatColor.AQUA + "] " + ChatColor.WHITE;
	public final String String = (new SimpleDateFormat("dd").format(new Date()));

	/* Enable the Plugin */
	public void onEnable() {
		
		/* get Util */
		util = new ChristmasUtil(this);
		
		/* Manage Configs*/
		ccl.load();
		
		/* Register Events */
		listener = new ChristmasListener(this);
		
		/* Load config.yml*/
		loadConfig();
		
		System.out.println("[Christmas] Plugin successful loaded.");
		
		if(ccl.getConfig().getString("ChristmasSign.X") != null) {
			
			double x = ccl.getConfig().getDouble("ChristmasSign.X");
			double y = ccl.getConfig().getDouble("ChristmasSign.Y");
			double z = ccl.getConfig().getDouble("ChristmasSign.Z");
			
			util.startScheduler(40000, x, y, z);
		}
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
		saveConfig();
	}
}
