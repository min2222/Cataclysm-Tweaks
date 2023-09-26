package com.min01.cataclysmtweaks.misc;

import java.util.EnumSet;

import com.github.L_Ender.cataclysm.entity.BossMonsters.The_Leviathan.The_Leviathan_Entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class LeviathanOwnerHurtTargetGoal extends TargetGoal
{
	private final The_Leviathan_Entity tameAnimal;
	private LivingEntity ownerLastHurt;
	private int timestamp;

	public LeviathanOwnerHurtTargetGoal(The_Leviathan_Entity p_26114_)
	{
		super(p_26114_, false);
		this.tameAnimal = p_26114_;
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
				this.ownerLastHurt = livingentity.getLastHurtMob();
	            int i = livingentity.getLastHurtMobTimestamp();
	            return i != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT);
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
		LivingEntity livingentity = ((ITamableLeviathan) this.tameAnimal).getOwner();
		if (livingentity != null) 
		{
			this.timestamp = livingentity.getLastHurtMobTimestamp();
		}
		
		super.start();
	}
}
