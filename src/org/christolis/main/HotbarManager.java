package org.christolis.main;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class HotbarManager implements Listener {
	
	public static ArrayList<Player> invisiblePeople = new ArrayList<Player>();
	
	public static ItemStack minigameSelector = new ItemStack(Material.COMPASS, 1);
	public static ItemStack gadgetSelector = new ItemStack(Material.CHEST, 1);
	public static ItemStack invisible_on = new ItemStack(Material.INK_SACK, 1, (byte)10);
	public static ItemStack invisible_off = new ItemStack(Material.INK_SACK, 1, (byte)8);
	
	public static Inventory minigameSelectorInv = Bukkit.createInventory(null, 54, "Minigame selector");
	public static ItemStack comingSoon = new ItemStack(Material.BARRIER, 1);
	
	public static Inventory gadgetSelectorInv = Bukkit.createInventory(null, 54, "Gadget selector");
	public static ItemStack comingSoon2 = new ItemStack(Material.BARRIER, 1);
	
	public static void giveHotbar(Player player) {
		ItemMeta comingSoonMeta = comingSoon.getItemMeta();
		comingSoonMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Minigames coming soon!");
		comingSoon.setItemMeta(comingSoonMeta);
		minigameSelectorInv.setItem(22, comingSoon);
		
		ItemMeta comingSoon2Meta = comingSoon2.getItemMeta();
		comingSoon2Meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Gadgets coming soon!");
		comingSoon2.setItemMeta(comingSoon2Meta);
		gadgetSelectorInv.setItem(22, comingSoon2);
		
		//Minigame Selector
		ItemStack minigameSelector = new ItemStack(Material.COMPASS, 1);
		ItemMeta minigameSelectorMeta = minigameSelector.getItemMeta();
		minigameSelectorMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Minigame Selector" + ChatColor.GRAY + " (Right Click)");
		minigameSelector.setItemMeta(minigameSelectorMeta);
		
		//Gadget Selector
		ItemMeta gadgetSelectorMeta = gadgetSelector.getItemMeta();
		gadgetSelectorMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Gadgets" + ChatColor.GRAY + " (Right Click)");
		gadgetSelector.setItemMeta(gadgetSelectorMeta);
		
		//Players Visible (SHOWN)
		ItemMeta invisibleMeta_on = invisible_on.getItemMeta();
		invisibleMeta_on.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Players visible" + ChatColor.GRAY + " (Right Click)");
		invisible_on.setItemMeta(invisibleMeta_on);
		
		//Players Visible (HIDDEN)
		ItemMeta invisibleMeta_off = invisible_off.getItemMeta();
		invisibleMeta_off.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Players invisible" + ChatColor.GRAY + " (Right Click)");
		invisible_off.setItemMeta(invisibleMeta_off);
		
		player.getInventory().setItem(0, minigameSelector);
		player.getInventory().setItem(4, gadgetSelector);
		player.getInventory().setItem(8, invisible_on);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		e.setCancelled(true);
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
	
			if(e.getPlayer().getItemInHand().getType() == Material.COMPASS) {
				e.getPlayer().openInventory(minigameSelectorInv);
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
				e.getPlayer().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Opening minigame selector menu...");
			}
			else if(e.getPlayer().getItemInHand().getType() == Material.CHEST) {
				e.getPlayer().openInventory(gadgetSelectorInv);
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
				e.getPlayer().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Opening gadget selector menu...");
				LevelManager.addXP(e.getPlayer(), 25);
			}
			else if(e.getPlayer().getItemInHand().getType() == Material.INK_SACK && !invisiblePeople.contains(e.getPlayer())) {
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
				e.getPlayer().getInventory().setItem(8, invisible_off);
				invisiblePeople.add(e.getPlayer());
				for(Player p_online : Bukkit.getOnlinePlayers())
					e.getPlayer().hidePlayer(p_online);
			}
			else if(e.getPlayer().getItemInHand().getType() == Material.INK_SACK && invisiblePeople.contains(e.getPlayer())) {
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
				e.getPlayer().getInventory().setItem(8, invisible_on);
				invisiblePeople.remove(e.getPlayer());
				for(Player p_online : Bukkit.getOnlinePlayers())
					e.getPlayer().showPlayer(p_online);			
			}
		}
	}
}
