package io.github.gronnmann.coinflipper.customizable;

import org.bukkit.configuration.file.FileConfiguration;

import io.github.gronnmann.coinflipper.ConfigManager;
import net.md_5.bungee.api.ChatColor;

public enum Message {
	CMD_PLAYER_ONLY("This command is for players only"),
	NO_PERMISSION("&cYou don't have permission to use this command!"),
	PLAYER_NOT_FOUND("&cPlayer not found."),
	DISABLED_IN_WORLD("&cCoinFlipper is disabled in this world."),
	CMD_HELP("help"),
	CMD_GUI("gui"),
	CMD_STATS("stats"),
	CMD_ANIMATION("animation"),
	CMD_CLEAR("clear"),
	CMD_RELOAD("reload"),
	CMD_CONFIG("config"),
	CMD_CONVERT("convert"),
	CONVERT_START("&a[CoinFlipper] Converting .yml stats to SQLite..."),
	CONVERT_SUCCESS("&a[CoinFlipper] Done"),
	CONVERT_FAIL("&c[CoinFlipper] Converting failed. Please see console for more information."),
	HEADS("Heads"),
	TAILS("Tails"),
	WRONG_MONEY("&c%NUMBER% is not recognized as a number."),
	SYNTAX_L1("&6Syntax"),
	SYNTAX_L2("&a/coinflipper [amount] [heads/tails] &e- &ePlaces a CoinFlipper game"),
	SYNTAX_L3("&a/coinflipper gui &e- &eOpens the GUI"),
	SYNTAX_L4("&a/coinflipper stats &e- View your stats"),
	SYNTAX_L5("&a/coinflipper animation &e- Create or delete animations."),
	MIN_BET("&cPlease provide a bet over &a%MIN_BET%"),
	MAX_BET("&cPlease provide a bet under &a%MAX_BET%"),
	PLACE_TRIAL_PICKSIDE("&cPlease choose a site. (heads/tails)"),
	PLACE_SUCCESSFUL("&aSuccessfully placed a CoinFlipper game."),
	PLACE_FAILED_ALREADYGAME("&cYou have reached your limit of CoinFlipper games simultaneously."),
	PLACE_FAILED_MAXBETS("&cThere can only be %NUM% bets active at once."),
	PLACE_FAILED_NOMONEY("&cYou don't have enough money to do this."),
	PLACE_NOT_POSSIBLE_NOMONEY("&cYou need atleast &e%MINMON% &cto place bets."),
	CLEAR_SUCCESSFUL("&aCleared all bets"),
	CLEAR_FAILED_NOBETS("&cThere are no bets to clear."),
	HELP_ITEM_L1("&6&lCoinFlipper Help"),
	HELP_ITEM_L2("&eLeft-click to challenge flip"),
	HELP_ITEM_L3("&eRight-click to remove your flip"),
	HELP_ITEM_L4("&e'/coinflipper help' for more help with the commands."),
	CREATE("&eCreate"),
	MENU_HEAD_GAME("&6Game #%ID%"),
	MENU_HEAD_PLAYER("&2Player: &a%PLAYER%"),
	MENU_HEAD_MONEY("&2Money: &a%MONEY%"),
	MENU_HEAD_TIMEREMAINING("&2Time remaining: &a%HOURS% hours, %MINUTES% minutes"),
	MENU_HEAD_SIDE("&2Side: &a%SIDE%"),
	BET_REMOVE_SELF_CONFIRM("&cRight click once more to confirm you want to remove your bet."),
	BET_REMOVE_SELF_SUCCESSFUL("&aSuccessfully removed your bet."),
	BET_REMOVE_OTHER_CONFIRM("&cRight click once more to confirm you want to remove %PLAYER%'s bet."),
	BET_REMOVE_OTHER_SUCCESSFUL("&aSuccessfully removed %PLAYER%'s bet."),
	BET_REMOVE_OTHER_NOTIFICATION("&cYour bet has been removed"),
	BET_EXPIRE_REFUND("&cYour bet has expired. You have been refunded &e%MONEY%"),
	BET_CHALLENGE_CANTSELF("&cYou can't challenge your own bet!"),
	BET_CHALLENGE_ALREADYSPINNING("&cThe player you are trying to challenge is already in a CoinFlipper game."),
	BET_CHALLENGE_NOMONEY("&cYou can't afford to challenge this bet."),
	BET_TITLE_VICTORY("&aVictory"),
	BET_TITLE_LOSS("&cLoss"),
	BET_WON("&aYou have won your bet against &e%LOSER% &awith the value &e%MONEY%"),
	BET_LOST("&cYou have lost your bet against &e%WINNER% &cwith the value &e%MONEY%"),
	HIGH_GAME_BROADCAST("&e%WINNER% &ajust won a bet worth &e%MONEY% &a against &e%LOSER%&a."),
	BET_TITLE_COMBAT("&cCombat"),
	BET_START_COMBAT("&eA CoinFlipper game you participate in is running but you are in combat."),
	STATS_NOSTATS("&cYou have no stats to be found. Please relog to get it."),
	STATS_STATS("&6&l%PLAYER%'s Statistics"),
	STATS_GAMESWON("&2Games won: &a%AMOUNT%"),
	STATS_GAMESLOST("&2Games lost: &a%AMOUNT%"),
	STATS_WINPERCENTAGE("&2Win percentage: &a%AMOUNT%%"),
	STATS_MONEYWON("&2Money won: &a%AMOUNT%"),
	STATS_MONEYSPENT("&2Money spent: &a%AMOUNT%"),
	STATS_MONEYEARNED("&2Money earned: &a%AMOUNT%"),
	RELOAD_SUCCESS("&aCoinFlipper Files reloaded successfully."),
	GUI_SELECTION("CoinFlipper Selection"),
	GUI_CONFIGURATOR("CoinFlipper Configurator"),
	GUI_GAME("CoinFlipper"),
	GUI_GAME_18("CoinFlipper Game"),
	CREATION_NAME("CoinFlipper Creation"),
	CREATION_MONEY("&9&lMoney: &3%MONEY%"),
	CREATION_SIDE("&9&lSide: &3%SIDE%"),
	CREATION_BET("&9&lBet"),
	CREATION_MONEY_COLOR("&3"),
	CREATION_MONEY_T1("$1"),
	CREATION_MONEY_T2("$10"),
	CREATION_MONEY_T3("$100"),
	CREATION_MONEY_T4("$1000"),
	CREATION_MONEY_T5("$10000"),
	CREATION_MONEY_MAX("Maximum bet"),
	CREATION_MONEY_LEFTTOADD("&aLeft click to add this to your bet"),
	CREATION_MONEY_RIGHTTOREMOVE("&cRight click to remove this from your bet."),
	CREATION_MONEY_CUSTOM("Custom value"),
	CREATION_MONEY_CUSTOM_DESC("&aEnter custom value"),
	CREATION_MONEY_CUSTOM_SPEC("&aPlease specify how much money you want to set your bet to. Exit typing &e'exit'&a."),
	CREATION_MONEY_CUSTOM_TOOMUCH("&cThe amount you specified is too big."),
	CREATION_MONEY_CUSTOM_TOOLITTLE("&cThe amount you specified is too small."),
	CREATION_MONEY_CUSTOM_NOMONEY("&cYou can't afford this bet."),
	CREATION_MONEY_CUSTOM_SUCCESS("&aBet money sat to $%MONEY%"),
	ANIMATION_CREATE_GIVENAME("&ePlease write the name you want the animation to create."),
	ANIMATION_CREATE_ALREADYEXISTS("&cAn animation with name %ANIMATION% already exists."),
	ANIMATION_CREATE_SUCCESS("&aNew animation with name %ANIMATION% generated."),
	ANIMATION_CLONE_GIVENAME("&ePlease write the name of the new copied animation."),
	ANIMATION_CLONE_SUCCESS("&aAnimation copied successfully."),
	ANIMATION_REMOVE_CANT_REMOVE_DEFAULT("&cYou can't remove an default animation."),
	ANIMATION_REMOVE_CANT_REMOVE_ALL("&cYou can't remove the last remaining animation."),
	ANIMATION_REMOVE_SUCCESS("&aSuccessfully removed animation %ANIMATION%."),
	ANIMATION_GUI_CREATE("&a&lCreate"),
	ANIMATION_GUI_DELETE("&c&lDelete"),
	ANIMATION_GUI_CLONE("&9&lClone"),
	ANIMATION_GUI_DEFANIM("&eDefault animation"),
	ANIMATION_FRAMEEDITOR_BACK("&c&lBack"),
	ANIMATION_FRAMEEDITOR_COPYLAST("&dCopy last frame"),
	ANIMATION_FRAMEEDITOR_P1HEAD("&9Player 1 Skull"),
	ANIMATION_FRAMEEDITOR_P2HEAD("&9Player 2 Skull"),
	ANIMATION_FRAMEEDITOR_WINNERHEAD("&9Winner Skull"),
	ANIMATION_FRAMEEDITOR_NEXT("&6Next"),
	ANIMATION_FRAMEEDITOR_CURRENT("&eCurrent frame: %FRAME%"),
	ANIMATION_FRAMEEDITOR_PREV("&6Previous"),
	ANIMATION_ROLL_P1SKULL("&9%PLAYER%"),
	ANIMATION_ROLL_P2SKULL("&9%PLAYER%"),
	ANIMATION_ROLL_WINNERSKULL("&b&lWinner: &9%PLAYER%"),
	ANIMATION_ROLL_ROLLING("Rolling..."),
	ANIMATION_ROLL_WINNERCHOSEN("&aWinner chosen."),
	INPUT_NOTNUM("&cPlease specify a number"),
	CONFIGURATOR_SPEC("&aPleace specify the new value for %CVAR%. &aType 'cancel' to cancel."),
	CONFIGURATOR_EDIT_SUCCESSFUL("&aSuccessfully changed value of &e%CVAR% &ato &e%VALUE%"),
	CONFIGURATOR_MESSAGE_PREVIEW("&ePlease preview new message for %CVAR%"),
	CONFIGURATOR_MESSAGE_CONFIRM("&a&lConfirm"),
	CONFIGURATOR_MESSAGE_CHANGE("&6&lChange"),
	CONFIGURATOR_MESSAGE_CANCEL("&c&lCancel");
	
	private String msg, defaultMsg;
	
	
	Message(String msg) {
		this.msg = msg;
		this.defaultMsg = msg;
	}
	
	public String getMessage() {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	public String getDefaultMessage() {
		return ChatColor.translateAlternateColorCodes('&', defaultMsg);
	}
	
	public void setMessage(String message) {
		this.msg = message;
		this.save();
	}
	
	
	public void load() {
		FileConfiguration msgs = ConfigManager.getManager().getMessages();
		
		if (msgs == null)return;
		
		if (msgs.getString(this.toString()) == null) {
			msgs.set(this.toString(), msg);
			save();
			return;
		}
		
		msg = msgs.getString(this.toString());
	}
	
	
	public void save() {
		FileConfiguration msgs = ConfigManager.getManager().getMessages();
		
		msgs.set(this.toString(), msg);
		
		ConfigManager.getManager().saveMessages();
	}
}
