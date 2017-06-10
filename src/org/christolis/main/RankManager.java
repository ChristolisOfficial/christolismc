package org.christolis.main;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.entity.Player;

public class RankManager {
	
	/*
	 * MEMBER = 0
	 * VIP = 1
	 * MVP = 2
	 * HELPER = 3
	 * MODERATOR = 4
	 * OWNER = 5
	 */
	public int ranks[] = {
			0, 1, 2, 3, 4, 5
	};
	
	public static void setRank(Player player,int selectedRank) throws ClassNotFoundException {
		try {
			Main.sqlite.query("UPDATE christolis_data SET Rank = '"+selectedRank+"' WHERE Name = '"+player.getName()+"'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static int getRank(Player player) throws ClassNotFoundException {
		try {
			int resultedRank;
			ResultSet res = Main.sqlite.query("SELECT * FROM christolis_data WHERE Name = '"+player.getName()+"'");
			res.next();
			resultedRank = res.getInt("Rank");
			return resultedRank;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
}
