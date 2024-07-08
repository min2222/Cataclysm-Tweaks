package com.min01.cataclysmtweaks.mixin;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.The_Leviathan_Entity;
import com.github.L_Ender.cataclysm.entity.etc.CMBossInfoServer;
import com.min01.cataclysmtweaks.goal.CataclysmFollowOwnerGoal;
import com.min01.cataclysmtweaks.goal.CataclysmOwnerHurtByTargetGoal;
import com.min01.cataclysmtweaks.goal.CataclysmOwnerHurtTargetGoal;
import com.min01.cataclysmtweaks.misc.ITamable;
import com.min01.cataclysmtweaks.util.TameUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

@Mixin(The_Leviathan_Entity.class)
public abstract class MixinEntityLeviathan extends Mob implements ITamable
{
	//0 == wandering
	//1 == follow
	//2 == sit
	private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(The_Leviathan_Entity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(The_Leviathan_Entity.class, EntityDataSerializers.OPTIONAL_UUID);
	private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(The_Leviathan_Entity.class, EntityDataSerializers.BYTE);

	@Shadow
	@Final
	private CMBossInfoServer bossInfo;
	
	protected MixinEntityLeviathan(EntityType<? extends Mob> p_20966_, Level p_20967_)
	{
		super(p_20966_, p_20967_);
	}
	
	@Inject(at = @At("HEAD"), method = "registerGoals", cancellable = true)
	private void registerGoals(CallbackInfo ci)
	{
		if(this.isTame())
		{
			The_Leviathan_Entity.class.cast(this).goalSelector.addGoal(2, new CataclysmFollowOwnerGoal((ITamable) The_Leviathan_Entity.class.cast(this), 1.3D, 4.0F, 2.0F, true));
			The_Leviathan_Entity.class.cast(this).targetSelector.addGoal(1, new CataclysmOwnerHurtByTargetGoal((ITamable) The_Leviathan_Entity.class.cast(this)));
			The_Leviathan_Entity.class.cast(this).targetSelector.addGoal(2, new CataclysmOwnerHurtTargetGoal((ITamable) The_Leviathan_Entity.class.cast(this)));
		}
	}
	
	@Inject(at = @At("HEAD"), method = "tick", cancellable = true)
	private void tick(CallbackInfo ci)
	{
		TameUtil.tick(this, this, this.bossInfo);
	}
	
	@Inject(at = @At("HEAD"), method = "defineSynchedData", cancellable = true)
	private void defineSynchedData(CallbackInfo ci)
	{
		this.getEntityData().define(COMMAND, 0);
		this.getEntityData().define(DATA_FLAGS_ID, (byte)0);
		this.getEntityData().define(OWNER_UUID, Optional.empty());
	}
	  
	@Inject(at = @At("HEAD"), method = "addAdditionalSaveData", cancellable = true)
	private void addAdditionalSaveData(CompoundTag compound, CallbackInfo ci)
	{
		TameUtil.addTag(compound, this);
	}
	
	@Inject(at = @At("HEAD"), method = "readAdditionalSaveData", cancellable = true)
	private void readAdditionalSaveData(CompoundTag compound, CallbackInfo ci)
	{
		TameUtil.readData(compound, this);
	}
	
	@Override
	public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider)
	{
		if(this.isTame() && this.getOwner() == rider)
		{
			return true;
		}
		return super.canBeRiddenUnderFluidType(type, rider);
	}
	
	@Inject(at = @At("HEAD"), method = "travel", cancellable = true)
	private void travel(Vec3 vec, CallbackInfo ci)
	{
		if(this.isVehicle() && this.isTame())
		{
			if(this.getOwner() != null && this.getFirstPassenger() == this.getOwner())
			{
				ci.cancel();
				boolean jumping = ObfuscationReflectionHelper.getPrivateValue(LivingEntity.class, this.getOwner(), "f_20899_");
				if(jumping)
				{
					if(this.onGround() || this.isInWater())
					{
						this.jumpFromGround();
					}
				}
				Vec3 travelVector = new Vec3(this.getOwner().xxa, this.getOwner().yya, this.getOwner().zza);
	            this.setYRot(this.getOwner().getYRot());
	            this.yRotO = this.getYRot();
	            this.setXRot(this.getOwner().getXRot() * 0.5F);
	            this.setRot(this.getYRot(), this.getXRot());
	            this.yBodyRot = this.getYRot();
	            this.yHeadRot = this.yBodyRot;
	            if(this.isEffectiveAi())
	            {
	    			this.moveRelative((float) this.getOwner().getAttributeBaseValue(Attributes.MOVEMENT_SPEED), travelVector);
	    			this.move(MoverType.SELF, this.getDeltaMovement());
	    			this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
	    			if (this.getTarget() == null && The_Leviathan_Entity.class.cast(this).getAnimation() == The_Leviathan_Entity.NO_ANIMATION)
	    			{
	    				this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
	    			}
	            }
    			super.travel(travelVector);
			}
		}
	}
	
	@Override
	public boolean canAttack(LivingEntity p_21171_)
	{	
		return TameUtil.canAttack(this, p_21171_, super.canAttack(p_21171_));
	}
	
	@Inject(at = @At("HEAD"), method = "isAlliedTo", cancellable = true)
	private void isAlliedTo(Entity entity, CallbackInfoReturnable<Boolean> ci)
	{
		if(TameUtil.isAlliedTo(this, entity))
		{
			ci.setReturnValue(true);
		}
	}

	@Inject(at = @At("HEAD"), method = "canPlayMusic", cancellable = true, remap = false)
	private void canPlayMusic(CallbackInfoReturnable<Boolean> ci)
	{
		ci.setReturnValue(!this.isTame());
	}

	@Override
	public int getCommand() 
	{
		return this.getEntityData().get(COMMAND);
	}

	@Override
	public void setCommand(int command)
	{
		this.getEntityData().set(COMMAND, command);
	}

	@Override
	public boolean isTame()
	{
		return (this.getEntityData().get(DATA_FLAGS_ID) & 4) != 0;
	}

	@Override
	public void setTame(boolean p_21836_) 
	{
		byte b0 = this.getEntityData().get(DATA_FLAGS_ID);
		if (p_21836_)
		{
			this.getEntityData().set(DATA_FLAGS_ID, (byte)(b0 | 4));
		} 
		else 
		{
			this.getEntityData().set(DATA_FLAGS_ID, (byte)(b0 & -5));
		}
	}

	@Override
	public UUID getOwnerUUID()
	{
		return this.getEntityData().get(OWNER_UUID).orElse((UUID)null);
	}

	@Override
	public void setOwnerUUID(UUID p_21817_)
	{
		this.getEntityData().set(OWNER_UUID, Optional.ofNullable(p_21817_));
	}

	@Override
	public void tame(Player p_21829_) 
	{
		this.setTame(true);
		this.setOwnerUUID(p_21829_.getUUID());
	}

	@Override
	public LivingEntity getOwner() 
	{
		try 
		{
			UUID uuid = this.getOwnerUUID();
			return uuid == null ? null : this.level().getPlayerByUUID(uuid);
		} 
		catch (IllegalArgumentException illegalargumentexception)
		{
			return null;
		}
	}
}
