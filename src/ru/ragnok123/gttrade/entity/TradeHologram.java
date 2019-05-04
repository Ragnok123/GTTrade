package ru.ragnok123.gttrade.entity;

import cn.nukkit.entity.mob.EntitySlime;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class TradeHologram extends EntitySlime{

	public TradeHologram(FullChunk chunk, CompoundTag nbt) {
		super(chunk, nbt);
	}
	
	public void setText(String text) {
		this.setNameTag(text);
		this.setNameTagAlwaysVisible();
		this.setNameTagVisible();
	}

}
