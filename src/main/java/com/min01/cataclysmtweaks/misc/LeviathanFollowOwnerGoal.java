package com.min01.cataclysmtweaks.misc;

import java.util.EnumSet;

import com.github.L_Ender.cataclysm.entity.BossMonsters.The_Leviathan.The_Leviathan_Entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class LeviathanFollowOwnerGoal extends Goal 
{
	private final The_Leviathan_Entity tameable;

	private final LevelReader world;

	private final double followSpeed;

	private final float maxDist;

	private final float minDist;

	private final boolean teleportToLeaves;

	private LivingEntity owner;

	private int timeToRecalcPath;

	private float oldWaterCost;

	public LeviathanFollowOwnerGoal(The_Leviathan_Entity tamed, double speed, float minDist, float maxDist, boolean leaves) 
	{
		this.tameable = tamed;
		this.world = (LevelReader) tamed.level;
		this.followSpeed = speed;
		this.minDist = minDist;
		this.maxDist = maxDist;
		this.teleportToLeaves = leaves;
		setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	@Override
	public boolean canUse() 
	{
		LivingEntity owner = ((ITamableLeviathan) this.tameable).getOwner();
		if (owner == null)
			return false;
		if (owner.isSpectator())
			return false;
		if (((ITamableLeviathan) this.tameable).getCommand() != 1 || isInCombat())
			return false;
		if (this.tameable.distanceToSqr((Entity) owner) < (this.minDist * this.minDist))
			return false;
		if (this.tameable.getTarget() != null && this.tameable.getTarget().isAlive())
			return false;
		return true;
	}

	@Override
	public boolean canContinueToUse() 
	{
		if (this.tameable.getNavigation().isDone() || isInCombat())
			return false;
		if (((ITamableLeviathan) this.tameable).getCommand() != 1)
			return false;
		if (this.tameable.getTarget() != null && this.tameable.getTarget().isAlive())
			return false;
		return (this.tameable.distanceToSqr((Entity) this.owner) > (this.maxDist * this.maxDist));
	}

	private boolean isInCombat()
	{
		LivingEntity livingEntity = ((ITamableLeviathan) this.tameable).getOwner();
		if (livingEntity != null)
			return (this.tameable.distanceTo((Entity) livingEntity) < 30.0F && this.tameable.getTarget() != null
					&& this.tameable.getTarget().isAlive());
		return false;
	}
		
	@Override
	public void start()
	{
		this.owner = ((ITamableLeviathan) this.tameable).getOwner();
		this.timeToRecalcPath = 0;
		this.oldWaterCost = this.tameable.getPathfindingMalus(BlockPathTypes.WATER);
		this.tameable.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
	}

	@Override
	public void stop()
	{
		this.owner = null;
		this.tameable.getNavigation().stop();
		this.tameable.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
	}

	@Override
	public void tick() 
	{
		this.tameable.getLookControl().setLookAt((Entity) this.owner, 10.0F, this.tameable.getMaxHeadXRot());
		if (--this.timeToRecalcPath <= 0) 
		{
			this.timeToRecalcPath = 10;
			if (!this.tameable.isLeashed() && !this.tameable.isPassenger())
			{
				if (this.tameable.distanceToSqr((Entity) this.owner) >= 144.0D) 
				{
					tryToTeleportNearEntity();
				} 
				else 
				{
					this.tameable.getNavigation().moveTo((Entity) this.owner, this.followSpeed);
				}
			}
		}
	}

	private void tryToTeleportNearEntity() 
	{
		BlockPos lvt_1_1_ = this.owner.blockPosition();
		for (int lvt_2_1_ = 0; lvt_2_1_ < 10; lvt_2_1_++) 
		{
			int lvt_3_1_ = getRandomNumber(-3, 3);
			int lvt_4_1_ = getRandomNumber(-1, 1);
			int lvt_5_1_ = getRandomNumber(-3, 3);
			boolean lvt_6_1_ = tryToTeleportToLocation(lvt_1_1_.getX() + lvt_3_1_, lvt_1_1_.getY() + lvt_4_1_, lvt_1_1_.getZ() + lvt_5_1_);
			if (lvt_6_1_)
				return;
		}
	}

	private boolean tryToTeleportToLocation(int p_226328_1_, int p_226328_2_, int p_226328_3_)
	{
		if (Math.abs(p_226328_1_ - this.owner.getX()) < 2.0D && Math.abs(p_226328_3_ - this.owner.getZ()) < 2.0D)
			return false;
		if (!isTeleportFriendlyBlock(new BlockPos(p_226328_1_, p_226328_2_, p_226328_3_)))
			return false;
		this.tameable.moveTo(p_226328_1_ + 0.5D, p_226328_2_, p_226328_3_ + 0.5D, this.tameable.getYRot(), this.tameable.getXRot());
		this.tameable.getNavigation().stop();
		return true;
	}

	private boolean isTeleportFriendlyBlock(BlockPos pos)
	{
		BlockPathTypes blockPathType = WalkNodeEvaluator.getBlockPathTypeStatic((BlockGetter) this.world, pos.mutable());
		if (this.world.getFluidState(pos).is(FluidTags.WATER) || (!this.world.getFluidState(pos).is(FluidTags.WATER) && this.world.getFluidState(pos.below()).is(FluidTags.WATER)))
			return true;
		if (blockPathType != BlockPathTypes.WALKABLE || avoidsLand())
			return false;
		BlockState lvt_3_1_ = this.world.getBlockState(pos.below());
		if (!this.teleportToLeaves && lvt_3_1_.getBlock() instanceof net.minecraft.world.level.block.LeavesBlock)
			return false;
		BlockPos lvt_4_1_ = pos.subtract((Vec3i) this.tameable.blockPosition());
		return this.world.noCollision((Entity) this.tameable, this.tameable.getBoundingBox().move(lvt_4_1_));
	}

	public boolean avoidsLand() 
	{
		return false;
	}

	private int getRandomNumber(int p_226327_1_, int p_226327_2_)
	{
		return this.tameable.getRandom().nextInt(p_226327_2_ - p_226327_1_ + 1) + p_226327_1_;
	}
}