package ru.ragnok123.gttrade.trade;

import java.util.*;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.form.*;
import cn.nukkit.form.element.*;
import cn.nukkit.form.response.*;
import cn.nukkit.form.window.*;
import cn.nukkit.inventory.*;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.*;
import cn.nukkit.network.protocol.*;
import cn.nukkit.scheduler.*;
import ru.ragnok123.gttrade.GTTrade;
import ru.ragnok123.gttrade.event.PlayerTradeEvent;

public class Trade implements Listener{

	public Player player1;
	public Player player2;
	public int seconds = 10;
	public Task timer = new TradeTixk(this);
	public Task queue = new TradeQueue(this);
	public Task tradedeny = new TradeDeny(this);
	public Task tradelobby = new TradeLobbyTimer(this);
	public HashMap<Player, HashMap<Integer, Item>> itemArray = new HashMap<Player, HashMap<Integer, Item>>();


	public Trade(Player p1, Player p2) {
		player1 = p1;
		player2 = p2;
		openTradeWindow();
		GTTrade.trades.add(this);
	}

	public void openTradeWindow() {
		TraderData d1 = GTTrade.data(player1.getName());
		TraderData d2 = GTTrade.data(player2.getName());
		
		FormWindowSimple w1 = new FormWindowSimple("§l§eTrade", "\n\n\n\n\n§l§2Trading with §b" + player2.getName());
		w1.addButton(new ElementButton("§l§2Your menu"));
		w1.addButton(new ElementButton("§l§2Trader's menu"));
		w1.addButton(new ElementButton("§l§2Accept"));
		w1.addButton(new ElementButton("§l§cCancel trade"));
		d1.windowMain = w1;
		Random r1 = new Random();
		int num1 = r1.nextInt(1000000000);
		player1.showFormWindow(d1.windowMain, num1);
		d1.setTrading(true);
		d1.num1 = num1;
		d1.trader = player2;
		d1.actualTrade = this;
		d1.createInventory();
		d1.createHologram();
		player1.setImmobile(true);
		d1.sendText("§l§e"+player1.getName()+": " + d1.getStatus() + "\n§l§e"+player2.getName()+": " + d2.getStatus());
		Server.getInstance().getScheduler().scheduleDelayedTask(new PitchTimer(player1), (int) (20*1.5));

		
		FormWindowSimple w2 = new FormWindowSimple("§l§eTrade", "\n\n\n\n\n§l§2Trading with §b" + player1.getName());
		w2.addButton(new ElementButton("§l§2Your menu"));
		w2.addButton(new ElementButton("§l§2Trader's menu"));
		w2.addButton(new ElementButton("§l§2Accept"));
		w2.addButton(new ElementButton("§l§cCancel trade"));
		d2.windowMain = w2;
		Random r2 = new Random();
		int num2 = r2.nextInt(1000000000);
		player2.showFormWindow(d2.windowMain, num2);
		d2.setTrading(true);
		d2.num1 = num2;
		d2.trader = player1;
		d2.actualTrade = this;
		d2.createInventory();
		d2.createHologram();
		player2.setImmobile(true);
		d2.sendText("§l§e"+player1.getName()+": " + d1.getStatus() + "\n§l§e"+player2.getName()+": " + d2.getStatus());
		Server.getInstance().getScheduler().scheduleDelayedTask(new PitchTimer(player2), (int) (20*1.5));
	}

	
	public void cancelTasks() {
		Server.getInstance().getScheduler().cancelTask(timer.getTaskId());
		Server.getInstance().getScheduler().cancelTask(queue.getTaskId());
	}

	public String itemsToString(TraderData data) {
		String list = "";
		Inventory inv = data.getTradeInventory();
		Map<Integer, Item> items = inv.getContents();
		for(Item item : items.values()) {
			list += ", ";
			list += ("§f"+item.getName().toUpperCase() + "(Count: " + String.valueOf(item.getCount()) + (item.hasCustomName() ? ", Name: " + item.getCustomName() : "") + "§f)");
		}
		return list;
	}

	public void openTradeMenu(Player player, TraderData data) { //from player menu to main menu
		if(data.isTrading()) {
			player.showFormWindow(data.windowMain, data.num1);
		}
	}
	
	public void openOtherPlayerMenuAgain(Player player, TraderData data, Player trader, TraderData otherData) {
		if(data.isTrading()) {
			//FormWindowCustom window = new FormWindowCustom("§l§a" +trader.getName()+ " menu");
			//window.addElement(new ElementLabel("§l§b" +trader.getName()+ " §ewant trade these things: ")); //0
			//window.addElement(new ElementLabel("§l§eTokens: " + String.valueOf(otherData.tokens))); //1
			//window.addElement(new ElementLabel("§l§eItems: " + itemsToString(otherData))); //2
			String title = "\n\n\n\n\n§l§eYou have 10 seconds, after just close menu\n §l§cWARNING: trader can cancel trade if he wants\n" + "§l§b" +trader.getName()+ " §ewant trade these things: \n" + "§l§e -Tokens: " + String.valueOf(otherData.tokens) + "\n" + "§l§e -Items: " + itemsToString(otherData) + "\n";
			FormWindowSimple window = new FormWindowSimple("§l§2" +trader.getName()+ " menu", title);
			window.addButton(new ElementButton("§l§cDENY TRADE")); //0
			Random r2 = new Random();
			int num2 = r2.nextInt(1000000000);
			if(data.traderWindowAgain == null) {
				data.traderWindowAgain = window;
				data.num5 = num2;
			}
			data.traderWindowAgain = null;
			data.traderWindowAgain = window;
			player.showFormWindow(data.traderWindowAgain, data.num5);
		}
		
	}

	public void openPlayerMenu(Player player, TraderData data) { //from main menu to player menu
		if(data.isTrading()) {
			FormWindowSimple window = new FormWindowSimple("§l§eTrade", "\n\n\n\n\n§l§2Your menu");
			window.addButton(new ElementButton("§l§2Tokens: " + String.valueOf(data.tokens)));
			window.addButton(new ElementButton("§l§2Inventory"));
			window.addButton(new ElementButton("§l§cBack"));
			Random rand = new Random();
			int num = rand.nextInt(1000000000);
			if(data.windowTrade == null) {
				data.windowTrade = window;
				data.num2 = num;
			}
			data.windowTrade = null;
			data.windowTrade = window;
			player.showFormWindow(data.windowTrade, data.num2);
		}
		
	}

	public void openOtherPlayerMenu(Player player, TraderData data, Player trader, TraderData otherData) { //from main menu to other trader menu
		if(data.isTrading()) {
			FormWindowCustom window = new FormWindowCustom("§l§2" +trader.getName()+ " menu");
			window.addElement(new ElementLabel("\n\n\n\n\n§l§b" +trader.getName()+ " §ewant trade these things: ")); //0
			window.addElement(new ElementLabel("§l§eTokens: " + String.valueOf(otherData.tokens))); //1
			window.addElement(new ElementLabel("§l§eItems: " + itemsToString(otherData))); //2
			Random rand = new Random();
			int num = rand.nextInt(1000000000);
			if(data.traderWindow == null) {
				data.traderWindow = window;
				data.num4 = num;
			}
			data.traderWindow = null;
			data.traderWindow = window;
			player.showFormWindow(data.traderWindow, data.num4);
		}
		
	}

	public void openTokenMenu(Player player, TraderData data) { // from player menu to tokens section
		if(data.isTrading()) {
			FormWindowCustom window = new FormWindowCustom("§l§2Tokens");
			window.addElement(new ElementLabel("\n\n\n\n\n§l§2Here you can set tokens")); //0
			window.addElement(new ElementInput("Tokens"));
			Random rand = new Random();
			int num = rand.nextInt(1000000000);
			if(data.windowTokens == null) {
				data.windowTokens = window;
				data.num3 = num;
			}
			data.windowTokens = null;
			data.windowTokens = window;
			player.showFormWindow(data.windowTokens, data.num3);
		}
		
	}
	
	public void openTokenMenuAgain(Player player, TraderData data) { // from player menu to tokens section
		if(data.isTrading()) {
			FormWindowCustom window = new FormWindowCustom("§l§2Tokens");
			window.addElement(new ElementLabel("\n\n\n\n\n§l§cYou dont have enough tokens. Please, set tokens, which you have")); //0
			window.addElement(new ElementInput("Tokens"));
			Random rand = new Random();
			int num = rand.nextInt(1000000000);
			if(data.windowTokensError == null) {
				data.windowTokensError = window;
				data.num6 = num;
			}
			data.windowTokensError = null;
			data.windowTokensError = window;
			player.showFormWindow(data.windowTokensError, data.num6);
		}
		
	}

	public void openInventoryMenu(Player player, TraderData data) { // from player menu to trade inventory
		player.addWindow(data.getTradeInventory());
		data.openedInv = true;
	}

	public void checkAccept() {
		TraderData d1 = GTTrade.data(player1.getName());
		TraderData d2 = GTTrade.data(player2.getName());
		d1.sendText("§l§e"+player1.getName()+": " + d1.getStatus() + "\n§l§e"+player2.getName()+": " + d2.getStatus());
		d2.sendText("§l§e"+player1.getName()+": " + d1.getStatus() + "\n§l§e"+player2.getName()+": " + d2.getStatus());
		if(d1.getAcceptStatus() == 1 && d2.getAcceptStatus() == 1) {
			PlayerTradeEvent event = new PlayerTradeEvent(this, player1, player2, d1.getTradeInventory(), d2.getTradeInventory());
			Server.getInstance().getPluginManager().callEvent(event);
			if(!event.isCancelled()){
				startTrading();
			} else {
				returnItems();
				cancelTrading();
			}
		}
	}
	
	public void returnItems() {
		TraderData d1 = GTTrade.data(player1.getName());
		TraderData d2 = GTTrade.data(player2.getName());
		Inventory inv1 = d1.getTradeInventory();
		Map<Integer, Item> items1 = inv1.getContents();
		for(Item item : items1.values()) {
			player1.getInventory().addItem(item);
		}
		Inventory inv2 = d2.getTradeInventory();
		Map<Integer, Item> items2 = inv2.getContents();
		for(Item item : items2.values()) {
			player2.getInventory().addItem(item);
		}
	}
	
	private void startTrading() {
		Server.getInstance().getScheduler().scheduleRepeatingTask(timer, 20);
		Server.getInstance().getScheduler().scheduleDelayedTask(queue, 20*10);
		String deny = "§l§cDENY";
		TraderData d1 = GTTrade.data(player1.getName());
		TraderData d2 = GTTrade.data(player2.getName());
		openOtherPlayerMenuAgain(player1, d1, player2, d2);
		openOtherPlayerMenuAgain(player2, d2, player1, d1);
			/*Item item11 = player1.getInventory().getItem(0);
			Item item12 = player1.getInventory().getItem(4);
			Item item13 = player1.getInventory().getItem(8);
			Item item21 = player2.getInventory().getItem(0);
			Item item22 = player2.getInventory().getItem(4);
			Item item23 = player2.getInventory().getItem(8);
			HashMap<Integer, Item> s1 = new HashMap<Integer, Item>();
			s1.put(0, item11);
			s1.put(4, item12);
			s1.put(8, item13);
			HashMap<Integer, Item> s2 = new HashMap<Integer, Item>();
			s2.put(0, item21);
			s2.put(4, item22);
			s2.put(8, item23);
			itemArray.put(player1, s1);
			itemArray.put(player2, s2);
			Item clock1 = Item.get(Item.CLOCK);
			clock1.setCustomName(deny);
			Item clock2 = Item.get(Item.CLOCK);
			Item clock3 = Item.get(Item.CLOCK);
			clock2.setCustomName("§l§e" + player2.getName() + "'s TRADE MENU");
			clock3.setCustomName("§l§e" + player1.getName() + "'s TRADE MENU");
			player1.getInventory().setItem(0, clock1);
			player1.getInventory().setItem(4, clock2);
			player1.getInventory().setItem(8, clock1);
			player2.getInventory().setItem(0, clock1);
			player2.getInventory().setItem(4, clock3);
			player2.getInventory().setItem(8, clock1);*/
	}
	
	public void addQueue() {
		String s = String.valueOf(this.seconds);
		String seconds = "§l§b" + s;
		String text = "  §l§cTime remaining: " + seconds;
		TraderData d1 = GTTrade.data(player1.getName());
		TraderData d2 = GTTrade.data(player2.getName());
		switch(this.seconds) {
		case 0:
			String text2 = "  §l§aTRADE SUCCESS!"; 
			player1.sendTitle(seconds);
			player2.sendTitle(seconds);
			addSound(player1, secondsToVolume(this.seconds));
			addSound(player2, secondsToVolume(this.seconds));
			d1.sendText(text2);
			d2.sendText(text2);
			break;
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
			player1.sendTitle(seconds);
			player2.sendTitle(seconds);
			addSound(player1, secondsToVolume(this.seconds));
			addSound(player2, secondsToVolume(this.seconds));
			d1.sendText(text);
			d2.sendText(text);
			break;
			
		}
	}
	
	public void addSound(Player player, float volume) {
		String s = "";
		switch(this.seconds) {
		case 0:
			
		case 1:
			s = "random.anvil_land";
			break;
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
			s = "note.harp";
			break;
		}
		PlaySoundPacket pk = new PlaySoundPacket();
		pk.name = s;
		pk.volume = volume;
		pk.pitch = 1;
		pk.x = player.getFloorX();
		pk.y = player.getFloorY();
		pk.z = player.getFloorZ();
		player.dataPacket(pk);
	}
	
	public float secondsToVolume(int seconds) {
		float volume = 0;
		switch(seconds) {
		case 10:
			float v = 0;
			volume = v;
			break;
		case 9:
			 v = (float) 0.1;
				volume = v;
			break;
		case 8:
			v = (float) 0.2;
			volume = v;
			break;
		case 7:
			v = (float) 0.3;
			volume = v;
			break;
		case 6:
			v = (float) 0.4;
			volume = v;
			break;
		case 5: 
			v = (float) 0.5;
			volume = v;
			break;
		case 4:
			v = (float) 0.6;
			volume = v;
			break;
		case 3:
			v = (float) 0.7;
			volume = v;
			break;
		case 2:
			v = (float) 0.8;
			volume = v;
			break;
		case 1:
			v = (float) 0.9;
			volume = v;
			break;
		case 0:
			v = 1;
			break;
		}
		return volume;
	}

	public void continueTrading(boolean ans) {
		TraderData d1 = GTTrade.data(player1.getName());
		TraderData d2 = GTTrade.data(player2.getName());
		//from 2 to 1
		
		me.onebone.economyapi.EconomyAPI.getInstance().addMoney(player1, (double) d2.tokens);
		me.onebone.economyapi.EconomyAPI.getInstance().addMoney(player2, (double) -d2.tokens);
		Inventory inv1 = d2.getTradeInventory();
		Map<Integer, Item> items1 = inv1.getContents();
		for(Item item : items1.values()) {
			player1.getInventory().addItem(item);
		}

		//now from 1 to 2
		me.onebone.economyapi.EconomyAPI.getInstance().addMoney(player2, (double) d1.tokens);
		me.onebone.economyapi.EconomyAPI.getInstance().addMoney(player1, (double) -d1.tokens);
		Inventory inv2 = d1.getTradeInventory();
		Map<Integer, Item> items2 = inv2.getContents();
		for(Item item : items2.values()) {
			player2.getInventory().addItem(item);
			//player1.getInventory().removeItem(item);
		}
		FormWindowCustom form = new FormWindowCustom("§l§eTrade");
		form.addElement(new ElementLabel("§l§2Thanks for trading"));
		player1.showFormWindow(form, -228);
		player2.showFormWindow(form, -228);
		cancelTrading();
	}
	
	public void startDeny() {
		Server.getInstance().getScheduler().scheduleDelayedTask(tradedeny, 20*3);
	}
	
	public void startLobby() {
		Server.getInstance().getScheduler().scheduleDelayedTask(tradelobby, 40);
	}
	
	public void lobby() {
		TraderData d1 = GTTrade.data(player1.getName());
		TraderData d2 = GTTrade.data(player2.getName());
		d1.sendText("§l§e"+player1.getName()+": " + d1.getStatus() + "\n§l§e"+player2.getName()+": " + d2.getStatus());
		d2.sendText("§l§e"+player1.getName()+": " + d1.getStatus() + "\n§l§e"+player2.getName()+": " + d2.getStatus());
	}

	public void cancelTrading() {
		TraderData d1 = GTTrade.data(player1.getName());
		TraderData d2 = GTTrade.data(player2.getName());
		if(/*itemArray.containsKey(player1) && itemArray.containsKey(player2) &&*/ d1.isTrading() && d2.isTrading()) {
		/*	Item item11 = itemArray.get(player1).get(0);
			Item item12 = itemArray.get(player1).get(4);
			Item item13 = itemArray.get(player1).get(8);
			player1.getInventory().setItem(0, item11);
			player1.getInventory().setItem(4, item12);
			player1.getInventory().setItem(8, item13);
			Item item21 = itemArray.get(player2).get(0);
			Item item22 = itemArray.get(player2).get(4);
			Item item23 = itemArray.get(player2).get(8);
			player2.getInventory().setItem(0, item21);
			player2.getInventory().setItem(4, item22);
			player2.getInventory().setItem(8, item23);
			itemArray.remove(player1);
			itemArray.remove(player2);
		*/
		player1.setImmobile(false);
		player2.setImmobile(false);
		d1.destroy();
		d2.destroy();
		d1.actualTrade = null;
		d2.actualTrade = null;
		player1 = null;
		player2 = null;
		GTTrade.trades.remove(this);
		}
			
	}

	public Player[] getTraders() {
		return new Player[] {player1, player2};
	}

}
