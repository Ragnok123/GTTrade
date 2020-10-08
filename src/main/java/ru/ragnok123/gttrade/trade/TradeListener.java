package ru.ragnok123.gttrade.trade;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.form.*;
import cn.nukkit.form.element.*;
import cn.nukkit.form.response.*;
import cn.nukkit.form.window.*;
import cn.nukkit.inventory.*;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.*;
import cn.nukkit.network.protocol.*;
import cn.nukkit.scheduler.*;
import ru.ragnok123.gttrade.GTTrade;

public class TradeListener implements Listener{
	
	public TradeListener() {
		
	}
	
	@EventHandler
	public void handleForms(PlayerFormRespondedEvent event) {
		Player player = event.getPlayer();
		TraderData data = GTTrade.data(player.getName());
		Trade trade = data.getActualTrade();
		FormWindow window = event.getWindow();
		FormResponse response = event.getResponse();
		if(data.isTrading()) {

			
				if((int) event.getFormID() == data.num1) { //main form
					if((FormResponseSimple)response == null) {
						trade.openTradeMenu(player, data);
					} else {
						if(response instanceof FormResponseSimple && window instanceof FormWindowSimple) { //main and trade
							switch(((FormResponseSimple)response).getClickedButtonId()) {
							case 0:
								trade.openPlayerMenu(player, data);
								GTTrade.setPitch(player);
								break;
							case 1:
								trade.openOtherPlayerMenu(player, data, data.trader, GTTrade.data(data.trader.getName()));
								break;
							case 2:
								data.setAcceptStatus(TraderData.Status.ACCEPTED);
								trade.checkAccept();
								break;
							case 3:
								data.setAcceptStatus(TraderData.Status.DENIED);
								data.trader.sendMessage("§l§e" +player.getName()+ " cancelled trading with you");
								trade.returnItems();
								data.sendText("§l§cYOU DENIED TRADE");
								GTTrade.data(data.trader.getName()).sendText("§l§e" +player.getName()+ " CANCELED TRADE");
								trade.startDeny();
								break;
							}
						}
					}
				}
				if((int) event.getFormID() == data.num2) { //trade form
					
					if ((FormResponseSimple)response == null) {
						trade.openPlayerMenu(player, data);
					} else {
						if(response instanceof FormResponseSimple && window instanceof FormWindowSimple) {
							switch(((FormResponseSimple)response).getClickedButtonId()) {
							case 0:
								trade.openTokenMenu(player, data);
								break;
							case 1:
								trade.openInventoryMenu(player, data);
								break;
							case 2:
								trade.openTradeMenu(player, data);
								break;
							}
						}
					}
				}
				if((int) event.getFormID() == data.num5) {
					if((FormResponseSimple)response == null) {
						trade.openOtherPlayerMenuAgain(player, data, data.trader, GTTrade.data(data.trader.getName()));
					} else {
						if(response instanceof FormResponseSimple && window instanceof FormWindowSimple) {
							switch(((FormResponseSimple)response).getClickedButtonId()) {
							case 0:
								trade.cancelTasks();
								trade.returnItems();
								data.sendText("§l§cYOU DENIED TRADE");
								GTTrade.data(data.trader.getName()).sendText("§l§e" +player.getName()+ " CANCELED TRADE");
								trade.startDeny();
								break;
							}
						}
					}
				}
			 //tokens and another player form
				if((int) event.getFormID() == data.num3 || (int) event.getFormID() == data.num6) {
					
					if((FormResponseCustom)response == null) {
						trade.openTokenMenuAgain(player, data);
					} else {
						if(response instanceof FormResponseCustom && window instanceof FormWindowCustom) {
							if (((FormResponseCustom)response).getInputResponse(1).equals("")){
								data.tokens = 0;
								trade.openPlayerMenu(player, data);
							} else {
								try {
									int token = Integer.valueOf(((FormResponseCustom)response).getInputResponse(1).trim());
									int tokens = Math.abs(token);
									if(tokens <= GTTrade.getEconomyHandler().getMoney(player)) {
										data.tokens = tokens;
										trade.openPlayerMenu(player, data);
									} else if(tokens > GTTrade.getEconomyHandler().getMoney(player)) {
										trade.openTokenMenuAgain(player, data);
									}
								} catch (NumberFormatException e) {
									trade.openTokenMenuAgain(player, data);
								}
							}
						}
					}
				}

				if((int) event.getFormID() == data.num4) {
						if((FormResponseCustom)response == null) {
							trade.openOtherPlayerMenu(player, data, data.trader, GTTrade.data(data.trader.getName()));
						} else {
							if(response instanceof FormResponseCustom && window instanceof FormWindowCustom) {
								trade.openTradeMenu(player, data);
							}
							
						}
				}
			}
		}
	

	//@EventHandler
	public void handleInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Item item = event.getItem();
		TraderData data = GTTrade.data(player.getName());
		Trade trade = data.getActualTrade();
		if(data.isTrading()) {
			if(item.getCustomName().equals("§l§cDENY")) {
				trade.cancelTasks();
				trade.returnItems();
				trade.cancelTrading();
			}
			if(item.getCustomName().equals("§l§e" + data.trader.getName() + "'s TRADE MENU")) {
				trade.openOtherPlayerMenuAgain(player, data, data.trader, GTTrade.data(data.trader.getName()));
			}
		}
	}
	
	@EventHandler
	public void handlePacketReading(DataPacketReceiveEvent event) {
		Player player = event.getPlayer();
		DataPacket pk = event.getPacket();
		if(player.isOnline()) {
			if(pk instanceof ContainerClosePacket) {
				ContainerClosePacket packet = (ContainerClosePacket)pk;
				TraderData data = GTTrade.data(player.getName());
				Trade trade = data.getActualTrade();
				if(data.isTrading()) {
					if(data.openedInv == true) {
						data.openedInv = false;
						trade.openPlayerMenu(player, data);
					}
				}
			}
		}
		
	}

}
