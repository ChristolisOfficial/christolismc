package org.christolis.main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener {
	public static Database sqlite;
	
	public static Scoreboard stats;
	
	double[] lobbyPos = {189.5, 64.5, 5.5, 90f, 0f};
	public ArrayList<Player> doubleJump = new ArrayList<Player>();
	public static HashMap<String, Scoreboard> statsScoreboard = new HashMap<String, Scoreboard>();
	
	public void teleportToSpawn(Entity entity) {
		Location lobbySpawn = new Location(entity.getWorld(), 189.5, 64.5, 5.5, 90f, 0f);
		entity.teleport(lobbySpawn);
	}
	
	public static void scoreboard(Player player, Scoreboard scoreboard) {
		stats = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		Objective objective = stats.registerNewObjective("stats", "dummy");
		
		try {
			int gold = 0, reputation = 0, rank = 0;
			String rankString;
			
			ResultSet res = sqlite.query("SELECT * FROM christolis_data WHERE Name = '"+player.getName()+"'");
			
			if(res.next()) {
				gold = Integer.valueOf(res.getString("Gold"));
				reputation = Integer.valueOf(res.getString("Reputation"));
				rank = Integer.parseInt(res.getString("Rank"));
				switch(rank) {
					case 0: {rankString = "Member";break;}
					case 1: {rankString = "VIP";break;}
					case 2: {rankString = "MVP";break;}
					case 3: {rankString = "Helper";break;}
					case 4: {rankString = "Moderator";break;}
					case 5: {rankString = "Owner";break;}
					default: {rankString = "Error";break;}
				}
			
				if(statsScoreboard.containsKey(player.getName()))
					statsScoreboard.remove(player.getName());
				
				objective.setDisplaySlot(DisplaySlot.SIDEBAR);
				objective.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "  ChristolisMC  ");
				objective.getScore("").setScore(8);
				objective.getScore(ChatColor.GOLD + "Rank: " + ChatColor.WHITE + rankString).setScore(7);
				objective.getScore(" ").setScore(6);
				objective.getScore(ChatColor.GOLD + "Gold: " + ChatColor.WHITE + gold).setScore(5);
				objective.getScore(ChatColor.GOLD + "Reputation: " + ChatColor.WHITE + reputation).setScore(4);
				objective.getScore("  ").setScore(3);
				objective.getScore(ChatColor.GOLD + "Level: " + ChatColor.WHITE + LevelManager.getLevel(player) + " ("+LevelManager.getXP(player)+"/100XP)").setScore(2);
				objective.getScore("    ").setScore(1);
				objective.getScore(ChatColor.GRAY + DateTimeFormatter.ofPattern("dd/MM/yyy").format(LocalDate.now())).setScore(0);
				statsScoreboard.put(player.getName(), stats);
				player.setScoreboard(statsScoreboard.get(player.getName()));
				res.close();
			}
			else {
				sqlite.query("INSERT INTO christolis_data (Name, Rank, Gold, Reputation, XP, Level) VALUES ('"+player.getName()+"', '0', '25', '0', '0', '0')");
				ResultSet res_registered = sqlite.query("SELECT * FROM christolis_data WHERE Name = '"+player.getName()+"'");
				if(res_registered.next()) {
					gold = Integer.valueOf(res_registered.getString("Gold"));
					reputation = Integer.valueOf(res_registered.getString("Reputation"));
					rank = Integer.parseInt(res_registered.getString("Rank"));
					switch(rank) {
						case 0: {rankString = "Member";break;}
						case 1: {rankString = "VIP";break;}
						case 2: {rankString = "MVP";break;}
						case 3: {rankString = "Helper";break;}
						case 4: {rankString = "Moderator";break;}
						case 5: {rankString = "Owner";break;}
						default: {rankString = "Error";break;}
					}
		
					if(statsScoreboard.containsKey(player.getName()))
						statsScoreboard.remove(player.getName());					
					
					objective.setDisplaySlot(DisplaySlot.SIDEBAR);
					objective.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "  ChristolisMC  ");
					objective.getScore("").setScore(8);
					objective.getScore(ChatColor.GOLD + "Rank: " + ChatColor.WHITE + rankString).setScore(7);
					objective.getScore(" ").setScore(6);
					objective.getScore(ChatColor.GOLD + "Gold: " + ChatColor.WHITE + gold).setScore(5);
					objective.getScore(ChatColor.GOLD + "Reputation: " + ChatColor.WHITE + reputation).setScore(4);
					objective.getScore("  ").setScore(3);
					objective.getScore(ChatColor.GOLD + "Level: " + ChatColor.WHITE + LevelManager.getLevel(player) + " ("+LevelManager.getXP(player)+"/100XP)").setScore(2);
					objective.getScore("    ").setScore(1);
					objective.getScore(ChatColor.GRAY + DateTimeFormatter.ofPattern("dd/MM/yyy").format(LocalDate.now())).setScore(0);
					statsScoreboard.put(player.getName(), stats);
					player.setScoreboard(statsScoreboard.get(player.getName()));
					
					res_registered.close();
				}
			}
		} catch (SQLException e) {e.printStackTrace();}
	}
	
	public static void updateScoreboard(Player player, Scoreboard scoreboard) {
		try {
			ResultSet res = sqlite.query("SELECT * FROM christolis_data WHERE Name = '"+player.getName()+"'");
			if(res.next()) {
				if(statsScoreboard.containsKey(player.getName()))
					statsScoreboard.remove(player.getName());
				String rankString;
				
				switch(Integer.valueOf(res.getString("Rank"))) {
				case 0: {rankString = "Member";break;}
				case 1: {rankString = "VIP";break;}
				case 2: {rankString = "MVP";break;}
				case 3: {rankString = "Helper";break;}
				case 4: {rankString = "Moderator";break;}
				case 5: {rankString = "Owner";break;}
				default: {rankString = "Error";break;}
				}
				player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore("").setScore(8);
				player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(ChatColor.GOLD + "Rank: " + ChatColor.WHITE + rankString).setScore(7);
				player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(" ").setScore(6);
				player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(ChatColor.GOLD + "Gold: " + ChatColor.WHITE + Integer.valueOf(res.getString("Gold"))).setScore(5);
				player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(ChatColor.GOLD + "Reputation: " + ChatColor.WHITE + Integer.valueOf(res.getString("Reputation"))).setScore(4);
				player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore("  ").setScore(3);
				player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(ChatColor.GOLD + "Level: " + ChatColor.WHITE + LevelManager.getLevel(player) + " ("+LevelManager.getXP(player)+"/100XP)").setScore(2);
				player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore("    ").setScore(1);
				player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(ChatColor.GRAY + DateTimeFormatter.ofPattern("dd/MM/yyy").format(LocalDate.now())).setScore(0);
				statsScoreboard.put(player.getName(), stats);
				
				player.setScoreboard(statsScoreboard.get(player.getName()));
				res.close();
			}
		} catch (SQLException e) {e.printStackTrace();}
	}
	
	@SuppressWarnings("deprecation")
	public void onEnable() {		
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getServer().getPluginManager().registerEvents(new HotbarManager(), this);
		Plugin plugin = this;
		
		sqlite = new SQLite(getLogger(), "LOBBY", plugin.getDataFolder().getAbsolutePath(), "christolis_data");
		sqlite.open();
		try {
			if(!sqlite.checkTable("christolis_data"))
				sqlite.query("CREATE TABLE christolis_data (Name, Rank, Gold, Reputation, XP, Level)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void onDisable() {
		sqlite.close();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		BossBar bossBar = Bukkit.createBossBar(ChatColor.AQUA + "You are playing on " + ChatColor.YELLOW + "" + ChatColor.BOLD + "ChristolisMC", BarColor.WHITE, BarStyle.SOLID, BarFlag.DARKEN_SKY);
		
		scoreboard(player, statsScoreboard.get(player.getName()));
		bossBar.setProgress(0f);
		bossBar.addPlayer(player);
		e.setJoinMessage(null);
		teleportToSpawn( (Entity)player );
		player.sendTitle(ChatColor.YELLOW + "ChristolisMC", ChatColor.GREEN + "Have fun!", 20, 80, 20);
		player.setGameMode(GameMode.ADVENTURE);
		player.setHealth(20);
		player.setFoodLevel(20);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, 0), true);
		player.setLevel(LevelManager.getLevel(player));
		for(int i = 0; i < 8; i++)
			player.getInventory().remove(i);
		HotbarManager.giveHotbar(player);
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		doubleJump.remove(e.getPlayer());
		HotbarManager.invisiblePeople.remove(e.getPlayer());
		e.setQuitMessage(null);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		Entity entity = e.getEntity();
		
		e.setCancelled(true);
		if(e.getCause() == DamageCause.VOID)
			teleportToSpawn(entity);
	}
	
	
	@EventHandler
	public void onPlayerLoseFood(FoodLevelChangeEvent e) {
		e.setFoodLevel(20);
	}
	
	@EventHandler
	public void onPlayerBreakBlock(BlockBreakEvent e) {
		if( !(e.getPlayer().getName().equals("Christolis")) ) e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerPlaceBlock(BlockPlaceEvent e) {
		if( !(e.getPlayer().getName().equals("Christolis")) ) e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if((player.getGameMode()!=GameMode.CREATIVE) && (player.getLocation().subtract(0, 1, 0).getBlock().getType()!= Material.AIR) && (!player.isFlying()) && !doubleJump.contains(player));
			player.setAllowFlight(true);
	}
	
	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		
		if(player.getGameMode() == GameMode.CREATIVE) return;
		if(!doubleJump.contains(player)) {
			event.setCancelled(true);
			player.setAllowFlight(false);
			player.setFlying(false);
			player.setVelocity(player.getLocation().getDirection().multiply(1.8).setY(1.1));
			player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_LAUNCH, 10, 1);
			doubleJump.add(player);
		}
		player.setFlying(false);
		player.setAllowFlight(false);
		
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				@Override
				public void run() {
					doubleJump.remove(player);
				}
		}, 60);
	}
}
