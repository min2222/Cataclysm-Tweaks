package com.min01.cataclysmtweaks.misc;

import java.util.UUID;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public interface ITamableLeviathan
{
	public int getCommand();
		  
	public void setCommand(int command);
		  
	public boolean isSitting();
		  
	public void setOrderedToSit(boolean sit);
	
	public boolean isTame();
	 
	public void setTame(boolean p_21836_);

	public void tame(Player p_21829_);
	
	public UUID getOwnerUUID();
	
	public void setOwnerUUID(UUID p_21817_);
	
	public LivingEntity getOwner();
}
