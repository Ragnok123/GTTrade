package ru.ragnok123.gttrade.economy;

import cn.nukkit.Player;

public interface EconomyHandler {
	
	void addMoney(Player p, int amount);
	int getMoney(Player p);
}
