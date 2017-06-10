package org.christolis.main;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class LevelManager {
	
	public static void setLevel(Player player, int amount) {
		if(amount >= 50 || amount < 0)
			Bukkit.getLogger().info("You can only put levels 0 - 50.. stupid Chris.. I hope you don't code around 1AM again!");
		else {
			try {
				player.setLevel(amount);
				player.setTotalExperience(0);
				Main.sqlite.query("UPDATE christolis_data SET Level = '"+amount+"', XP = '0' WHERE Name = '"+player.getName()+"'");
				
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void addLevel(Player player, int amount) {
		if((getLevel(player) + amount) > 50)
			Bukkit.getLogger().info("This player will have reached the maximum limit of levels if he updates to this amount.");
		else {
			try {
				player.setLevel(player.getLevel() + amount);
				player.setExp(0);
				Main.sqlite.query("UPDATE christolis_data SET Level = '"+player.getLevel() + amount+"' WHERE Name = '"+player.getName()+"'");
				if(getLevel(player) == 50)
					Main.sqlite.query("UPDATE christolis_data SET XP = '0' WHERE Name = '"+player.getName()+"'");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static int getLevel(Player player) {
		try {
			int result;
			ResultSet rs = Main.sqlite.query("SELECT * FROM christolis_data WHERE Name = '"+player.getName()+"'");
			rs.next();
			result = Integer.parseInt(rs.getString("Level"));
			rs.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static int getXP(Player player) {
		try {
			int result;
			ResultSet rs = Main.sqlite.query("SELECT * FROM christolis_data WHERE Name = '"+player.getName()+"'");
			Main.scoreboard(player, Main.statsScoreboard.get(player.getName()));
			rs.next();
			result = Integer.valueOf(rs.getString("XP"));
			rs.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static void addXP(Player player, int amount) {
		try {
			XPUpdateEvent(player);
			player.setTotalExperience(getXP(player)+amount);
			Main.sqlite.query("UPDATE christolis_data SET XP = '"+(player.getTotalExperience()+amount)+"' WHERE Name = '"+player.getName()+"'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	//Events
	private static void XPUpdateEvent(Player player) {
		if(getXP(player) == 100 && getLevel(player) <= 50) {
			try {
				Main.sqlite.query("UPDATE christolis_data SET XP = '0'");
				setLevel(player, getLevel(player)+1);
				Main.updateScoreboard(player, Main.statsScoreboard.get(player.getName()));
				player.setLevel(getLevel(player)+1);
				player.setExp(0);
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 10, 1);
				player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
				player.sendMessage(ChatColor.BLUE + "Level System>" + ChatColor.WHITE + "You leveled up!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
