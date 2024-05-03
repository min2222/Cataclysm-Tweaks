package com.min01.cataclysmtweaks.goal;

import java.util.EnumSet;

import com.min01.cataclysmtweaks.misc.ITamable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class CataclysmOwnerHurtByTargetGoal extends TargetGoal 
{
	private final ITamable tameAnimal;
	private LivingEntity ownerLastHurtBy;
	private int timestamp;

	public CataclysmOwnerHurtByTargetGoal(ITamable p_26107_) 
	{
		super((Mob) p_26107_, false);
		this.tameAnimal = p_26107_;
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
				this.ownerLastHurtBy = livingentity.getLastHurtByMob();
				int i = livingentity.getLastHurtByMobTimestamp();
				return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT);
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
		this.mob.setTarget(this.ownerLastHurtBy);
		LivingEntity livingentity = this.tameAnimal.getOwner();
		if (livingentity != null)
		{
			this.timestamp = livingentity.getLastHurtByMobTimestamp();
		}

		super.start();
	}
}
