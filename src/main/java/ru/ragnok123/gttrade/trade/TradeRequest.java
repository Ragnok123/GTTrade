package ru.ragnok123.gttrade.trade;

import cn.nukkit.Player;

public class TradeRequest {
	
	private Player sender;
	private Player receiver;
	
	public TradeRequest(Player _1, Player _2) {
		sender = _1;
		receiver = _2;
	}
	
	public Player getSender() {
		return this.sender;
	}
	
	public Player getReceiver() {
		return this.receiver;
	}
	
	public boolean check() {
		return this.sender != null && this.receiver != null;
	}
	
	public Player[] getTraders() {
		return new Player[] {sender, receiver};
	}

}
