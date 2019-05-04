package ru.ragnok123.gttrade.trade;

import cn.nukkit.Player;
import cn.nukkit.scheduler.Task;
import ru.ragnok123.gttrade.GTTrade;

public class PitchTimer extends Task{
	
	private Player trade;
	private boolean check = false;
	
	public PitchTimer(Player trade) {
		this.trade = trade;
		this.check = true;
	}

	@Override
	public void onRun(int currentTick) {
		GTTrade.setPitch(trade);
	}

}
