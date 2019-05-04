package ru.ragnok123.gttrade.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import ru.ragnok123.gttrade.GTTrade;
import ru.ragnok123.gttrade.trade.*;
import cn.nukkit.level.Location;
import cn.nukkit.Player;
import cn.nukkit.Server;

import java.util.Iterator;

public class TradeCommand extends Command{

	public TradeCommand(String name) {
		super(name);
	}

	@Override
	public boolean execute(CommandSender senderr, String label, String[] args) {
		Player sender = (Player) senderr;
		
			if(args.length == 0) {
				sender.sendMessage("§l§eUsage: /trade §b<nickname>");
			} else {
				Player receiver = Server.getInstance().getPlayerExact(args[0]);
				if(receiver == null) {
					if(args[0].equalsIgnoreCase("cancell")){
						TraderData data = GTTrade.data(sender.getName());
						Trade trade = data.getActualTrade();
						trade.startDeny();
					}
					if(args[0].equalsIgnoreCase("accept")) {
						Iterator<TradeRequest> itr = GTTrade.requests.iterator();
						while(itr.hasNext()) {
							TradeRequest r1 = itr.next();
							if(r1.check()) {
								if(r1.getSender() == Server.getInstance().getPlayerExact(args[1])) {
									new Trade(r1.getSender(), r1.getReceiver());
								}

							}
							itr.remove();
						}
					}
					else if(args[0].equalsIgnoreCase("deny")) {
						Iterator<TradeRequest> itr = GTTrade.requests.iterator();
						while(itr.hasNext()) {
							TradeRequest r1 = itr.next();
							if(r1.check()) {
								if(r1.getSender() == Server.getInstance().getPlayerExact(args[1])) {
									sender.sendMessage("§l§eYou've denied request");
									r1.getSender().sendMessage("§l§b" + sender.getName() + " §edenied your request");
								}

							}
							itr.remove();
						}
					} else {
						sender.sendMessage("§l§ePlayer §b" +args[0]+ " §eis offline");
					}
					} else {
					
					if(!args[0].equals(sender.getName().toLowerCase())) {
						if(receiver.isOnline()) {
							TradeRequest request = new TradeRequest(sender, receiver);
							GTTrade.requests.add(request);
							sender.sendMessage("§l§eSent request to §b"+ receiver.getName());
							receiver.sendMessage("§l§b" + sender.getName() + " §ewant to trade with you");
							receiver.sendMessage("§l§e- /trade §aaccept <nickname> §e to accept");
							receiver.sendMessage("§l§e- /trade §cdeny <nickname> §e to deny");
						} else {
							sender.sendMessage("§l§ePlayer §b" +args[0]+ " §eis offline");
						}
					} else {
						//if(sender.isOp()) {
							//TradeRequest request = new TradeRequest(sender, receiver);
							//GTTrade.requests.add(request);
							//sender.sendMessage("§l§eSent request to §b"+ receiver.getName());
							//receiver.sendMessage("§l§b" + sender.getName() + " §ewant to trade with you");
							//receiver.sendMessage("§l§e- /trade §aaccept §e to accept");
							//receiver.sendMessage("§l§e- /trade §cdeny §e to deny");
						//} else {
							sender.sendMessage("§l§eTrading with self? No, we don't do it here");
						//}
					}
					}
			}
		
		return true;
	}
	
	

}
