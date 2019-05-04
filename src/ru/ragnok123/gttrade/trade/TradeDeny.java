package ru.ragnok123.gttrade.trade;

import cn.nukkit.scheduler.Task;

public class TradeDeny extends Task{
	
	private Trade trade;
	private boolean check = false;
	
	public TradeDeny(Trade trade) {
		this.trade = trade;
		this.check = true;
	}

	@Override
	public void onRun(int currentTick) {
		this.trade.cancelTrading();
	}

}
