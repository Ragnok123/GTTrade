package ru.ragnok123.gttrade.trade;

import cn.nukkit.scheduler.Task;

public class TradeLobbyTimer extends Task{
	
	private Trade trade;
	
	public TradeLobbyTimer(Trade trade) {
		this.trade = trade;
	}

	@Override
	public void onRun(int currentTick) {
		this.trade.lobby();
	}

}
