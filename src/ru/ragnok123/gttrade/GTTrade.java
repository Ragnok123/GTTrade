package ru.ragnok123.gttrade;

import java.util.ArrayList;
import java.util.List;
import java.util.*;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.*;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.plugin.*;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import ru.ragnok123.gttrade.entity.TradeHologram;
import ru.ragnok123.gttrade.commands.TradeCommand;
import ru.ragnok123.gttrade.event.PlayerTradeEvent;
import ru.ragnok123.gttrade.trade.Trade;
import ru.ragnok123.gttrade.trade.TradeRequest;
import ru.ragnok123.gttrade.trade.TraderData;
import ru.ragnok123.gttrade.trade.TradeListener;

public class GTTrade extends PluginBase implements cn.nukkit.event.Listener{
	
	public static List<Trade> trades = new ArrayList<Trade>();
	public static List<TradeRequest> requests = new ArrayList<TradeRequest>();
	public static HashMap<String, TraderData> players = new HashMap<String, TraderData>();
	public static GTTrade plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		Plugin eapi = Server.getInstance().getPluginManager().getPlugin("EconomyAPI");
		if (eapi == null) {
			Server.getInstance().getLogger().info("EconomyAPI not found. Please, if you want to run Trade plugin with EconomyAPI, install it.");
		}
		Server.getInstance().getCommandMap().register("trade", new TradeCommand("trade"));
		Server.getInstance().getPluginManager().registerEvents(this,  this);
		Server.getInstance().getPluginManager().registerEvents(new TradeListener(),  this);
		Entity.registerEntity("TradeHologram", TradeHologram.class);
		Server.getInstance().getScheduler().scheduleDelayedTask(new Runnable() {
			@Override
			public void run() {
				for(Level levels : Server.getInstance().getLevels().values()) {
					for(Entity entities : levels.getEntities()) {
						if(entities instanceof TradeHologram) {
							entities.close();
						}
					}
				}
			}
		},20);
	}
	
	public static TraderData data(String p) {
		return players.get(p.toLowerCase());
	}
	
	@EventHandler
	public void handleJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		TraderData data = new TraderData(p);
		players.put(p.getName().toLowerCase(), data);
	}
	
	@EventHandler
	public void handleQuit(PlayerQuitEvent event) {
		TraderData data = data(event.getPlayer().getName());
		Trade trade = data.getActualTrade();
		for(TradeRequest request : requests) {
			if(request.getTraders().equals(event.getPlayer())) {
				requests.remove(request);
			}
		}
		if(data.isTrading()) {
			trade.startDeny();
			trades.remove(trade);
		}
	}
	
	@EventHandler
	public void handleDeath(PlayerDeathEvent event) {
		Player player = (Player) event.getEntity();
		TraderData data = data(player.getName());
		Trade trade = data.getActualTrade();
		for(TradeRequest request : requests) {
			if(request.getTraders().equals(player)) {
				requests.remove(request);
			}
		}
		if(data.isTrading()) {
			trade.cancelTrading();
			trades.remove(trade);
		}
	}
	
	@EventHandler
	public void checkDmg(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if(entity instanceof TradeHologram) {
			event.setCancelled();
		}
	}
	
	public static void setPitch(Player sender) {
		float x = sender.getFloorX();
		float y = sender.getFloorY();
		float z = sender.getFloorZ();
		double yaw = sender.yaw;
		sender.teleport(new Location(x, y, z, 180, 14, sender.getLevel()));
	}
	


}
