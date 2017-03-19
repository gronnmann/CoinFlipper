package io.github.gronnmann.coinflipper.bets;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import io.github.gronnmann.coinflipper.ConfigManager;
import io.github.gronnmann.coinflipper.Main;
import io.github.gronnmann.coinflipper.MessagesManager;
import io.github.gronnmann.coinflipper.MessagesManager.Message;
import io.github.gronnmann.coinflipper.animations.Animation;
import io.github.gronnmann.coinflipper.animations.AnimationsManager;
import io.github.gronnmann.coinflipper.events.BetPlaceEvent;
import io.github.gronnmann.coinflipper.gui.SelectionScreen;
import io.github.gronnmann.coinflipper.stats.StatsManager;
import io.github.gronnmann.utils.Debug;
import net.milkbowl.vault.economy.EconomyResponse;

public class BettingManager {
	private BettingManager(){}
	private static BettingManager manager = new BettingManager();
	public static BettingManager getManager(){
		return manager;
	}
	private FileConfiguration conf;
	
	private ArrayList<Bet> bets = new ArrayList<Bet>();
	
	public void load(){
		conf = ConfigManager.getManager().getBets();
		
		if (conf.getConfigurationSection("bets") == null)return;
		for (String ids : conf.getConfigurationSection("bets").getKeys(false)){
			int booster = conf.getInt("bets."+ids+".booster");
			String player = conf.getString("bets."+ids+".player");
			double money = conf.getDouble("bets."+ids+".money");
			int side = conf.getInt("bets."+ids+".side");
			int time = conf.getInt("bets."+ids+".time");
			Animation animation = AnimationsManager.getManager().getAnimation(conf.getString("bets."+ids+".animation"));
			
			Bet bet = new Bet(player, side, money, Integer.parseInt(ids), booster, animation);
			bet.setTimeRemaining(time);
			
			bets.add(bet);
		}
	}
	
	public void save(){
		conf.set("bets", null);
		for (Bet b : bets){
			conf.set("bets."+b.getID()+".booster", b.getBooster());
			conf.set("bets."+b.getID()+".player", b.getPlayer());
			conf.set("bets."+b.getID()+".money", b.getAmount());
			conf.set("bets."+b.getID()+".side", b.getSide());
			conf.set("bets."+b.getID()+".time", b.getTimeRemaining());
			conf.set("bets."+b.getID()+".animation", b.getAnimation().getName());
		}
		
		ConfigManager.getManager().saveBets();
	}
	
	
	public Bet addBet(Player p, int side, double amount){
		//Booster
		int booster = 0;
		for (int i = 0;i<=100;i++){
			if (p.hasPermission("coinflipper.boost."+i)){
				if (i > booster){
					booster=i;
				}
			}
		}
		
		Animation animation = AnimationsManager.getManager().getAnimationToUse(p);
		
		//Rest
		Bet b = new Bet(p.getName(), side, amount, this.getNextAvaibleID(), booster, animation);
		bets.add(b);
		return b;
	}
	
	
	
	public int getNextAvaibleID(){
		if (bets.isEmpty()){
			return 1;
		}
		
		int greatestID = 1;
		
		for (Bet g : bets){
			if (g.getID() > greatestID){
				greatestID = g.getID();
			}
		}
		
		return greatestID+1;
			
	}
	
	public Bet getBet(int id){
		for (Bet g : bets){
			if (g.getID() == id){
				return g;
			}
		}
		
		return null;
	}
	
	
	public boolean betExists(int id){
		for (Bet g : bets){
			if (id == g.getID())return true;
		}
		return false;
	}
	
	public void removeBet(Bet g){
		bets.remove(g);
	}
	
	public void removeBet(int id){
		bets.remove(this.getBet(id));
	}
	
	public String challengeBet(Bet b, Player p){
		
		int[] chances = this.getChances(p, b);
		
		Debug.print("Betowner chances: " + chances[1]);
		Debug.print("Challenger chances: " + chances[0]);
		
		Random rn = new Random();
		int r = rn.nextInt(chances[0]+chances[1]);
		
		if (r <= chances[1]){
			StatsManager.getManager().getStats(p).addLose();
			StatsManager.getManager().getStats(Bukkit.getOfflinePlayer(b.getPlayer()).getUniqueId().toString()).addWin();
			return b.getPlayer();
		}else{
			StatsManager.getManager().getStats(p).addWin();
			StatsManager.getManager().getStats(Bukkit.getOfflinePlayer(b.getPlayer()).getUniqueId().toString()).addLose();
			return p.getName();
		}
	}
	
	public int[] getChances(Player p1, Bet b){
		
		if (ConfigManager.getManager().getConfig().getString("boosters_enabled") != null){
			if (!ConfigManager.getManager().getConfig().getBoolean("boosters_enabled")){
				return new int[]{50, 50};
			}
		}
		
		int i1 = 50;
		int i2  = 50;
		
		int booster1 = 0, booster2 = 0;
		
		for (int i = 0; i<=100;i++){
			if (p1.hasPermission("coinflipper.boost."+i)){
				if (i > booster1){
					booster1 = i;
				}
			}
		}
		booster2 = b.getBooster();
		
		if (booster1 == 0 && booster2 != 0){
			i2 = booster2;
			i1 = 100-booster2;
		}
		else if (booster1 != 0 && booster2 == 0){
			i1 = booster1;
			i2 = 100-booster1;
		}
		else if (booster1 != 0 && booster2 != 0){
			i1 = booster1;
			i2 = booster2;
		}
		
		
		int[] returned = {i1, i2};
		return returned;
		
	}
	
	public ArrayList<Bet> getBets(){
		return bets;
	}
	
	public void clearBets(){
		for (Bet b : bets){
			Main.getEcomony().depositPlayer(b.getPlayer(), b.getAmount());
		}
		bets.clear();
		SelectionScreen.getInstance().refreshGameManager();
	}
	
	public boolean isAlreadyThere(Player p){
		
		int limit = 1;
		int bets = 0;
		
		if (ConfigManager.getManager().getConfig().getString("bets_per_player") != null){
			limit = ConfigManager.getManager().getConfig().getInt("bets_per_player");
		}
			
		for (Bet b : BettingManager.getManager().getBets()){
			if (b.getPlayer().equals(p.getName())){
				bets++;
			}
		}
		
		if (bets >= limit){
			return true;
		}else return false;
	}
	
}


