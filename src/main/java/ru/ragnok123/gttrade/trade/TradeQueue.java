package ru.ragnok123.gttrade.trade;

import cn.nukkit.scheduler.Task;

public class TradeQueue extends Task{
	
	private Trade trade;
	private boolean check = false;
	
	public TradeQueue(Trade trade) {
		this.trade = trade;
		this.check = true;
	}

	@Override
	public void onRun(int currentTick) {
		this.trade.continueTrading(this.check);
	}

}
