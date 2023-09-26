package com.min01.cataclysmtweaks.misc;

import java.util.EnumSet;

import com.github.L_Ender.cataclysm.entity.BossMonsters.The_Leviathan.The_Leviathan_Entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class LeviathanOwnerHurtByTargetGoal extends TargetGoal 
{
	private final The_Leviathan_Entity tameAnimal;
	private LivingEntity ownerLastHurtBy;
	private int timestamp;

	public LeviathanOwnerHurtByTargetGoal(The_Leviathan_Entity p_26107_) 
	{
		super(p_26107_, false);
		this.tameAnimal = p_26107_;
		this.setFlags(EnumSet.of(Goal.Flag.TARGET));
	}

	@Override
	public boolean canUse()
	{
		if (((ITamableLeviathan) this.tameAnimal).isTame() && !((ITamableLeviathan) this.tameAnimal).isSitting())
		{
			LivingEntity livingentity = ((ITamableLeviathan) this.tameAnimal).getOwner();
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
		LivingEntity livingentity = ((ITamableLeviathan) this.tameAnimal).getOwner();
		if (livingentity != null)
		{
			this.timestamp = livingentity.getLastHurtByMobTimestamp();
		}
		super.start();
	}
}
