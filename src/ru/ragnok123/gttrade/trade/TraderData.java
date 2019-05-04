package ru.ragnok123.gttrade.trade;

import java.util.*;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.FloatEntityData;
import cn.nukkit.form.*;
import cn.nukkit.form.element.*;
import cn.nukkit.form.response.*;
import cn.nukkit.form.window.*;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import ru.ragnok123.gttrade.GTTrade;
import ru.ragnok123.gttrade.entity.*;

public class TraderData {
	
	public Player player;
	public Trade actualTrade = null;
	
	//some shit with inventories
	public Inventory inventory = null;
	public BlockEntity bentity = null;
	public Block changedBlock = null;
	public boolean openedInv = false;
	
	//main window simple
	public FormWindow windowMain = null;
	public int num1 = -1;
	
	//window with inventory and tokens simple
	public FormWindow windowTrade = null;
	public int num2 = -1;
	
	//token windows custom
	public FormWindow windowTokens = null;
	public int num3 = -1;
	
	//and now, other trader custom window, causeof impossibility of opening via infinity distance
	public FormWindow traderWindow = null;
	public int num4 = -1;
	
	//this is other trader menu, but again
	public FormWindow traderWindowAgain = null;
	public int num5 = -1;
	
	//this is error token menu
	public FormWindow windowTokensError = null;
	public int num6 = -1;
	
	public int tokens = 0;
	private boolean trading = false;
	public int accept = 0;
	public Player trader;
	public TradeHologram holo = null;
	
	
	public void createHologram() {
		CompoundTag nbt = new CompoundTag()
				.putList(new ListTag<DoubleTag>("Pos")
						.add(new DoubleTag("", player.getFloorX()))
						.add(new DoubleTag("", player.getFloorY() + 1))
						.add(new DoubleTag("", player.getFloorZ() - 2.5)))
		        .putList(new ListTag<DoubleTag>("Motion")
		        		.add(new DoubleTag("", 0))
		        		.add(new DoubleTag("", 0))
		        		.add(new DoubleTag("", 0)))
		        .putList(new ListTag<FloatTag>("Rotation")
		        		.add(new FloatTag("", (float) 0))
		        		.add(new FloatTag("", (float) 0)));
		TradeHologram hologram = new TradeHologram(player.getLevel().getChunk((int) player.x >> 4, (int) player.z >> 4), nbt);
		hologram.setText("§l§eTrade");
		hologram.setScale((float) 0.05);
		hologram.spawnTo(player);
		hologram.setImmobile(true);
		holo = hologram;
	}
	
	public TradeHologram getTradeHologram() {
		return this.holo;
	}
	
	public String getStatus() {
		String s = "§cUNREADY";
		if(getAcceptStatus() == 0) {
			
		} else if (getAcceptStatus() == 1) {
			s = "§aREADY";
		}
		return s;
	}
	
	
	public enum Status{
		ACCEPTED(1),
		DENIED(2);
		
		int acceptStatus;
		
		Status(int status) {
			acceptStatus = status;
		}
	}
	
	public void destroy() {
		if(player.getLevel().getBlockEntityById(bentity.getId()) != null) {
			player.getLevel().removeBlockEntity(bentity);
			changedBlock.level.sendBlocks(new Player[]{player}, new Block[]{changedBlock});
			inventory = null;
			bentity = null;
			changedBlock = null;
		}
		if(holo != null) {
			getTradeHologram().close();
			RemoveEntityPacket pk = new RemoveEntityPacket();
			pk.eid = getTradeHologram().getId();
			for(Player players : player.getLevel().getPlayers().values()) {
				players.dataPacket(pk);
			}
			holo = null;
		}
		
		windowMain = null;
		num1 = -1;
		windowTrade = null;
		num2 = -1;
		windowTokens = null;
		num3 = -1; 
		traderWindow = null;
		num4 = - 1;
		traderWindowAgain = null;
		num5 = -1;
		windowTokensError = null;
		num6 = -1;
		tokens = 0;
		trading = false;
		accept = 0;
		trader = null;
		actualTrade = null;
	}
	
	public void sendText(String text) {
		this.getTradeHologram().setText(text);
		GTTrade.setPitch(player);
	}
	
	public TraderData(Player p) {
		this.player = p;
	}
	
	public int getAcceptStatus() {
		return this.accept;
	}
	
	public void setAcceptStatus(Status status) {
		this.accept = status.acceptStatus;
	}
	
	public Trade getActualTrade() {
		return this.actualTrade;
	}
	
	public void setTrading(boolean value) {
		this.trading = value;
	}
	
	public boolean isTrading() {
		return this.trading;
	}
	
	public Inventory getTradeInventory() {
		return this.inventory;
	}
	
	public FormWindow getMainWindow() {
		return this.windowMain;
	}
	
	public FormWindow getPlayerWindow() {
		return this.windowTrade;
	}
	
	public void createInventory() {
		 BlockEntityChest chest = (BlockEntityChest) BlockEntity.createBlockEntity(
	                BlockEntity.CHEST,
	                this.player.chunk,
	                new CompoundTag()
	                    .putString("id", "Chest")
	                    .putInt("x", (int) player.x)
	                    .putInt("y", (int) (player.y - 2))
	                    .putInt("z", (int) player.z)
	        );
		 	bentity = chest;
		 	Vector3 v = new Vector3(player.x, player.y - 2, player.z);
		 	Block firstBlock = player.getLevel().getBlock(v);
		 	changedBlock = firstBlock;
	        Block block = Block.get(Block.CHEST);
	        block.x = chest.x;
	        block.y = chest.y;
	        block.z = chest.z;
	        block.level = chest.getLevel();
	        block.level.sendBlocks(new Player[]{player}, new Block[]{block});
	        inventory = chest.getInventory();
	}

}
