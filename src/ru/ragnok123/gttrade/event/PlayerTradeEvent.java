package ru.ragnok123.gttrade.event;

import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import ru.ragnok123.gttrade.trade.Trade;

public class PlayerTradeEvent extends Event implements Cancellable{
	    private static HandlerList handlers = new HandlerList();
	    private Player player1;
	    private Player player2;
	    private Inventory inventory1;
	    private Inventory inventory2;
	    private Trade trade;

	    public static HandlerList getHandlers() {
	        return handlers;
	    }
	    
	    public PlayerTradeEvent(Trade trade, Player player1, Player player2, Inventory i1, Inventory i2) {
	    	this.player1 = player1;
	    	this.player2 = player2;
	    	this.inventory1 = i1;
	    	this.inventory2 = i2;
	    	this.trade = trade;
	    }
	    
	    public Player getPlayerOne() {
	    	return this.player1;
	    }
	    
	    public Player getPlayerTwo() {
	    	return this.player2;
	    }
	    
	    public Trade getTrade() {
	    	return this.trade;
	    }
	    
	    public Item itemOneExists(Item item) {
	    	if(this.inventory1.contains(item)) {
	    		return item;
	    	}
	    	return null;
	    }
	    
	    public Item itemTwoExists(Item item) {
	    	if(this.inventory2.contains(item)) {
	    		return item;
	    	}
	    	return null;
	    }
	
	
}
