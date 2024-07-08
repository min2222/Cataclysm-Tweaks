package com.min01.cataclysmtweaks.goal;

import java.util.EnumSet;

import com.min01.cataclysmtweaks.misc.ITamable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class CataclysmFollowOwnerGoal extends Goal 
{
	private final ITamable tameable;

	private final Level world;

	private final double followSpeed;

	private final float maxDist;

	private final float minDist;

	private final boolean teleportToLeaves;

	private LivingEntity owner;

	private int timeToRecalcPath;

	private float oldWaterCost;

	public CataclysmFollowOwnerGoal(ITamable tame, double speed, float minDist, float maxDist, boolean leaves) 
	{
		this.tameable = tame;
		this.world = ((Entity) tame).level();
		this.followSpeed = speed;
		this.minDist = minDist;
		this.maxDist = maxDist;
		this.teleportToLeaves = leaves;
		setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	@Override
	public boolean canUse() 
	{
		LivingEntity owner = ((ITamable) this.tameable).getOwner();
		if (owner == null)
			return false;
		if (owner.isSpectator())
			return false;
		if (((ITamable) this.tameable).getCommand() != 1 || isInCombat())
			return false;
		if (((Entity) this.tameable).distanceToSqr((Entity) owner) < (this.minDist * this.minDist))
			return false;
		if (((Mob) this.tameable).getTarget() != null && ((Mob) this.tameable).getTarget().isAlive())
			return false;
		return true;
	}

	@Override
	public boolean canContinueToUse() 
	{
		if (((Mob) this.tameable).getNavigation().isDone() || isInCombat())
			return false;
		if (((ITamable) this.tameable).getCommand() != 1)
			return false;
		if (((Mob) this.tameable).getTarget() != null && ((Mob) this.tameable).getTarget().isAlive())
			return false;
		return (((Entity) this.tameable).distanceToSqr((Entity) this.owner) > (this.maxDist * this.maxDist));
	}

	private boolean isInCombat()
	{
		LivingEntity livingEntity = ((ITamable) this.tameable).getOwner();
		if (livingEntity != null)
			return (((Entity) this.tameable).distanceTo((Entity) livingEntity) < 30.0F && ((Mob) this.tameable).getTarget() != null && ((Mob) this.tameable).getTarget().isAlive());
		return false;
	}
		
	@Override
	public void start()
	{
		this.owner = ((ITamable) this.tameable).getOwner();
		this.timeToRecalcPath = 0;
		this.oldWaterCost = ((Mob) this.tameable).getPathfindingMalus(BlockPathTypes.WATER);
		((Mob) this.tameable).setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
	}

	@Override
	public void stop()
	{
		this.owner = null;
		((Mob) this.tameable).getNavigation().stop();
		((Mob) this.tameable).setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
	}

	@Override
	public void tick() 
	{
		((Mob) this.tameable).getLookControl().setLookAt((Entity) this.owner, 10.0F, ((Mob) this.tameable).getMaxHeadXRot());
		if (--this.timeToRecalcPath <= 0) 
		{
			this.timeToRecalcPath = 10;
			if (!((Mob) this.tameable).isLeashed() && !((Entity) this.tameable).isPassenger())
			{
				if (((Entity) this.tameable).distanceToSqr((Entity) this.owner) >= 144.0D) 
				{
					tryToTeleportNearEntity();
				} 
				else 
				{
					((Mob) this.tameable).getNavigation().moveTo((Entity) this.owner, this.followSpeed);
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
		((Entity) this.tameable).moveTo(p_226328_1_ + 0.5D, p_226328_2_, p_226328_3_ + 0.5D, ((Entity) this.tameable).getYRot(), ((Entity) this.tameable).getXRot());
		((Mob) this.tameable).getNavigation().stop();
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
		BlockPos lvt_4_1_ = pos.subtract((Vec3i) ((Entity) this.tameable).blockPosition());
		return this.world.noCollision((Entity) this.tameable, ((Entity) this.tameable).getBoundingBox().move(lvt_4_1_));
	}

	public boolean avoidsLand() 
	{
		return false;
	}

	private int getRandomNumber(int p_226327_1_, int p_226327_2_)
	{
		return ((LivingEntity) this.tameable).getRandom().nextInt(p_226327_2_ - p_226327_1_ + 1) + p_226327_1_;
	}
}