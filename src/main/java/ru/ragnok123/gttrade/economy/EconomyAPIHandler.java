package ru.ragnok123.gttrade.economy;

import cn.nukkit.Player;

public class EconomyAPIHandler implements EconomyHandler {

	@Override
	public void addMoney(Player p, int amount) {
		me.onebone.economyapi.EconomyAPI.getInstance().addMoney(p, (double) amount);
	}
	
	@Override
	public int getMoney(Player p) {
		return (int)me.onebone.economyapi.EconomyAPI.getInstance().myMoney(p);
	}

}
