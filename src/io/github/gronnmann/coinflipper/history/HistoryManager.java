package io.github.gronnmann.coinflipper.history;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.gronnmann.coinflipper.CoinFlipper;
import io.github.gronnmann.coinflipper.SQLManager;
import io.github.gronnmann.coinflipper.customizable.CustomMaterial;
import io.github.gronnmann.coinflipper.customizable.Message;
import io.github.gronnmann.utils.coinflipper.ItemUtils;
import io.github.gronnmann.utils.pagedinventory.coinflipper.PagedInventory;

public class HistoryManager{
	private HistoryManager() {}
	private static HistoryManager logger = new HistoryManager();
	public static HistoryManager getLogger() {return logger;}
	
	
	public void logGame(String winner, String loser, double money, double moneyPot, double tax) {
		Connection conn = SQLManager.getManager().getSQLConnection();
		new BukkitRunnable() {
			public void run() {
				try {
					PreparedStatement log = conn.prepareStatement(
							"INSERT INTO coinflipper_history(time, winner, loser, moneyWon,moneyPot, tax) VALUES (?,?,?,?,?,?)");
					log.setLong(1, System.currentTimeMillis());
					log.setString(2, winner);
					log.setString(3, loser);
					log.setDouble(4, money);
					log.setDouble(5, moneyPot);
					log.setDouble(6, tax);
					
					log.execute();
					
				} catch (SQLException e) {
					System.out.println("Failed logging game...");
					e.printStackTrace();
				}
			}
			
		}.runTaskAsynchronously(CoinFlipper.getMain());
		
	}
	
	
	public Inventory getHistory(Player p) {
		PagedInventory gonnaBeHistory = new PagedInventory(Message.HISTORY_INVENTORY.getMessage().replaceAll("%player%", p.getName()),
				ItemUtils.createItem(Material.ARROW, Message.NEXT.getMessage()),
				ItemUtils.createItem(Material.ARROW, Message.PREVIOUS.getMessage()), 
				ItemUtils.createItem(CustomMaterial.BACK.getMaterial(), Message.BACK.getMessage()),
				"coinflipper_history_" + p.getName(),
				null);
		BukkitRunnable historyGetter = new BukkitRunnable() {
			
			
			String uuid = p.getUniqueId().toString();
			
			@Override
			public void run() {
				try {
					PreparedStatement getHistory = SQLManager.getManager().getSQLConnection().prepareStatement(
							"SELECT * FROM coinflipper_history WHERE ? IN (winner, loser) ORDER BY time DESC"
							);
					getHistory.setString(1, uuid);
					
					ResultSet rs = getHistory.executeQuery();
					
					
					while(rs.next()) {
						String winner = rs.getString("winner");
						String loser = rs.getString("loser");
						int id = rs.getInt("id");
						long time = rs.getLong("time");
						double moneyWon = rs.getDouble("moneyWon");
						double moneyPot = rs.getDouble("moneyPot");
						double tax = rs.getDouble("tax");
						
						boolean won = winner.equals(uuid) ? true : false;
						
						int colorId = won ? 5 : 14;
						
						
						ItemStack historyId = ItemUtils.createItem(Material.STAINED_GLASS_PANE, Message.HISTORY_GAME.getMessage().replaceAll("%id%", id+""), colorId);
						
						Date timeDate = new Date(time);
						
						ItemUtils.addToLore(historyId, Message.HISTORY_TIME.getMessage().replace("%time%", timeDate.toLocaleString()));
						
						
						String opponent = won ? loser : winner;
						if (!opponent.equals(SQLManager.convertedUUID)) {
							opponent = Bukkit.getOfflinePlayer(UUID.fromString(opponent)).getName();
							ItemUtils.addToLore(historyId, Message.HISTORY_OPPONENT.getMessage().replace("%opponent%", opponent));
						}
						ItemUtils.addToLore(historyId, "");
						
						String status = won ? Message.HISTORY_VICTORY.getMessage() : Message.HISTORY_LOSS.getMessage();
						ItemUtils.addToLore(historyId, status);
						
						ItemUtils.addToLore(historyId, Message.HISTORY_IN_POT.getMessage().replace("%money%", moneyPot+""));
						
						double moneyReplace = won ? moneyWon : moneyPot/2;
						String moneyStatus = won ? Message.HISTORY_MONEY_WON.getMessage().replace("%money%", moneyReplace+"")
								: Message.HISTORY_MONEY_LOST.getMessage().replace("%money%", moneyReplace+"");
						ItemUtils.addToLore(historyId, moneyStatus);
						
						ItemUtils.addToLore(historyId, Message.HISTORY_TAX.getMessage().replace("%tax%", tax+""));
						
						gonnaBeHistory.addItem(historyId);
						
					}
					
					
				}catch(Exception e) {
					e.printStackTrace();
					System.out.println("[CoinFlipper] Failed fetching game history for " + p.getName());
				}
			}
		};
		
		historyGetter.runTaskAsynchronously(CoinFlipper.getMain());
		
		
		
		
		return gonnaBeHistory.getPage(0);
		
		
	}
}
