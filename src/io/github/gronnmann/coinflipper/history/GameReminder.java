package io.github.gronnmann.coinflipper.history;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.gronnmann.coinflipper.CoinFlipper;
import io.github.gronnmann.coinflipper.SQLManager;
import io.github.gronnmann.coinflipper.customizable.Message;

public class GameReminder implements Listener{
	
	
	@EventHandler
	public void remindOnJoin(PlayerJoinEvent e) {
		//remind of games when player been offline
		new BukkitRunnable() {
			public void run() {
				try {
					PreparedStatement countGames = SQLManager.getManager().getSQLConnection().prepareStatement(
							"SELECT COUNT(id) AS gameNum FROM coinflipper_history WHERE ? IN (winner, loser) AND time > ?");
					
					countGames.setString(1, e.getPlayer().getUniqueId().toString());
					countGames.setLong(2, System.currentTimeMillis());
					
					
					ResultSet res = countGames.executeQuery();
					
					if (res.next() && res.getInt("gameNum") > 0) {
						if (e.getPlayer() != null)e.getPlayer().sendMessage(Message.GAMES_PLAYED_WHEN_OFFLINE.getMessage());
					}
					
					
					
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
				
			}
		}.runTaskAsynchronously(CoinFlipper.getMain());
	}
}
