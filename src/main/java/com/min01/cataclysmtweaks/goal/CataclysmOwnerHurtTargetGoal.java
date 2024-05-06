package com.min01.cataclysmtweaks.goal;

import java.util.EnumSet;

import com.min01.cataclysmtweaks.misc.ITamable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class CataclysmOwnerHurtTargetGoal extends TargetGoal
{
	private final ITamable tameAnimal;
	private LivingEntity ownerLastHurt;
	private int timestamp;

	public CataclysmOwnerHurtTargetGoal(ITamable p_26114_) 
	{
		super((Mob) p_26114_, false);
		this.tameAnimal = p_26114_;
		this.setFlags(EnumSet.of(Goal.Flag.TARGET));
	}
	
	@Override
	public boolean canUse() 
	{
		if (this.tameAnimal.isTame() && this.tameAnimal.getCommand() != 2) 
		{
			LivingEntity livingentity = this.tameAnimal.getOwner();
			if (livingentity == null)
			{
				return false;
			} 
			else 
			{
				LivingEntity lastHurt = livingentity.getLastHurtMob();
				boolean flag = lastHurt instanceof TamableAnimal animal ? !animal.isOwnedBy(this.tameAnimal.getOwner()) : true;
				if(flag)
				{
					this.ownerLastHurt = livingentity.getLastHurtMob();
					int i = livingentity.getLastHurtMobTimestamp();
					return i != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT);
				}
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	@Override
	public void start()
	{
		this.mob.setTarget(this.ownerLastHurt);
		LivingEntity livingentity = this.tameAnimal.getOwner();
		if (livingentity != null) 
		{
			this.timestamp = livingentity.getLastHurtMobTimestamp();
		}
		
		super.start();
	}
}
