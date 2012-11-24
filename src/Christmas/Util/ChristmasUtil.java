package Christmas.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import Christmas.Christmas;

public class ChristmasUtil {

	public ArrayList<String> player = new ArrayList<String>();
	public Date date;
	
	public Christmas main;
	
	public ChristmasUtil(Christmas main) {
		this.main = main;
	}
	
	public final String daate = (new SimpleDateFormat("dd.mm.yyyy").format(new Date()));

	public ArrayList<String> getOpener(String name) {
		name = player.get(0);
		return player;
	}
	
	public String replaceColorCodes(String message) {
		return message.replaceAll("(?i)&([a-n0-9])", "§$1");
	}

	public void removeOpener(ArrayList<String> arrayList, String name) {
		arrayList.remove(name);
	}

	public Date getDate() {
		Calendar cal = Calendar.getInstance();
		date = cal.getTime();
		return date;
	}
	
	public void sendMessage(Player player) {
		String message = main.getConfig().getString("Message");
		Bukkit.broadcastMessage(replaceColorCodes(message).replace("%player%", player.getName()).replace("%date%", daate));
	}

	public void startScheduler(final int millis, final double x, final double y, final double z) {

		new Thread() {

			public void run() {

				try {
					
					Location sloc = new Location(Bukkit.getServer().getWorld("world"), x, y, z);
					Block block = (Block) Bukkit.getServer().getWorld("world").getBlockAt(sloc);
					Sign sign = (Sign) block.getState();

					if (!(block.getState() instanceof Sign)) {
						return;
					}

					/* Set the Sign */
					sign.setLine(1, ChatColor.AQUA + daate);
					sign.update(true);
					
					Thread.sleep(millis);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

}
