package Christmas.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.minecraft.server.Packet103SetSlot;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import Christmas.Christmas;

public class ChristmasUtil {

	public ArrayList<String> player = new ArrayList<String>();
	
	public Christmas main;
	
	public ChristmasUtil(Christmas main) {
		this.main = main;
	}
	
	public final String date = (new SimpleDateFormat("dd.MM.yyyy").format(new Date()));

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
	
	public void sendMessage(Player player) {
		String message = main.getConfig().getString("Message");
		Bukkit.broadcastMessage(main.prefix + replaceColorCodes(message).replace("%player%", player.getName()).replace("%date%", date));
	}
	
	public void updateInventory(Player player) {

		CraftPlayer c = (CraftPlayer) player;

		for (int i = 0; i < 36; i++) {

			int nativeindex = i;

			if (i < 9)
				nativeindex = i + 36;

			ItemStack olditem = c.getInventory().getItem(i);
			net.minecraft.server.ItemStack item = null;

			if (olditem != null && olditem.getType() != Material.AIR) {
				item = new net.minecraft.server.ItemStack(0, 0, 0);
				item.id = olditem.getTypeId();
				item.count = olditem.getAmount();
			}

			Packet103SetSlot pack = new Packet103SetSlot(0, nativeindex, item);
			c.getHandle().netServerHandler.sendPacket(pack);
		}

	}

	public void startScheduler(final int sekunden, final double x, final double y, final double z) {

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
					sign.setLine(1, ChatColor.AQUA + date);
					sign.update(true);
					
					Thread.sleep(1000 * sekunden);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

}
