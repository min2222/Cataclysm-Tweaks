package com.min01.cataclysmtweaks.util;

import java.util.UUID;

import com.github.L_Ender.cataclysm.entity.Pet.AnimationPet;
import com.github.L_Ender.cataclysm.entity.etc.CMBossInfoServer;
import com.min01.cataclysmtweaks.misc.ITamable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class TameUtil 
{
	public static void tick(Mob entity, ITamable tame, CMBossInfoServer bossInfo)
	{
		if(tame.isTame())
		{
			Entity owner = tame.getOwner();
			Entity target = ((Mob) tame).getTarget();
			if(owner != null)
			{
				entity.setPersistenceRequired();
				bossInfo.setVisible(false);
				
				if(entity.isVehicle())
				{
					if(entity.getFirstPassenger() == owner)
					{
						if(owner.isShiftKeyDown())
						{
							owner.stopRiding();
						}
					}
				}
				
				if(target != null)
				{
					if(target == tame.getOwner() || target.isAlliedTo(tame.getOwner()))
					{
						Entity lastHurtMob = ((LivingEntity) owner).getLastHurtMob();
						Entity lastHurtByMob = ((LivingEntity) owner).getLastHurtByMob();
						boolean flag = lastHurtMob == null ? true : target != lastHurtMob;
						boolean flag2 = lastHurtByMob == null ? true : target != lastHurtByMob;
						if(flag || flag2)
						{
							((Mob) tame).setTarget(null);
						}
					}
				}
			}
		}
	}
	
	public static void addTag(CompoundTag compound, ITamable tame)
	{
	    compound.putInt("Command", tame.getCommand());
	    if (tame.getOwnerUUID() != null) 
	    {
	    	compound.putUUID("Owner", tame.getOwnerUUID());
	    }
	}
	
	public static void readData(CompoundTag compound, ITamable tame)
	{
		tame.setCommand(compound.getInt("Command"));
	    UUID uuid;
	    if (compound.hasUUID("Owner"))
	    {
	    	uuid = compound.getUUID("Owner");
	    }
	    else
	    {
	    	String s = compound.getString("Owner");
	    	uuid = OldUsersConverter.convertMobOwnerIfNecessary(((Entity) tame).getServer(), s);
	    }
	      
	    if (uuid != null)
	    {
	    	try 
	    	{
	    		tame.setOwnerUUID(uuid);
	    		tame.setTame(true);
	    	} 
	    	catch (Throwable throwable) 
	    	{
	    		tame.setTame(false);
	    	}
	    }
	}
	
	public static boolean canAttack(ITamable tame, LivingEntity living, boolean original)
	{
		if(living == tame.getOwner())
		{
			return false;
		}
		return original;
	}
	
	public static boolean isAlliedTo(ITamable tame, Entity entity)
	{
		if (tame.isTame()) 
		{
			LivingEntity livingentity = tame.getOwner();
			return entity == livingentity;
		}
		return false;
	}
	
	public static void setupTame(ITamable tame, AnimationPet pet)
	{
		tame.tame((Player) pet.getOwner());
		tame.setCommand(pet.getCommand());
	}
}
