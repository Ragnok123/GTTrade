package ru.ragnok123.gttrade.trade;

import cn.nukkit.scheduler.*;
import cn.nukkit.Server;

public class TradeTixk extends Task{
	
	public Trade trade;
	
	public TradeTixk(Trade trade) {
		this.trade = trade;
	}
	
	public void onRun(int tick) {
		this.trade.seconds--;
		this.trade.addQueue();
		if(this.trade.seconds <= 0) {
			Server.getInstance().getScheduler().cancelTask(this.getTaskId());
		}
	}

}
