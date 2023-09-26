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

import com.github.L_Ender.cataclysm.entity.BossMonsters.The_Leviathan.The_Leviathan_Entity;
import com.github.L_Ender.cataclysm.entity.etc.CMBossInfoServer;
import com.min01.cataclysmtweaks.misc.ITamableLeviathan;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

@Mixin(The_Leviathan_Entity.class)
public abstract class MixinEntityLeviathan extends Mob implements ITamableLeviathan
{
	private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(The_Leviathan_Entity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(The_Leviathan_Entity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(The_Leviathan_Entity.class, EntityDataSerializers.OPTIONAL_UUID);
	private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(The_Leviathan_Entity.class, EntityDataSerializers.BYTE);

	@Shadow
	private int destroyBlocksTick;
	
	@Shadow
	private @Final CMBossInfoServer bossInfo;
	
	protected MixinEntityLeviathan(EntityType<? extends Mob> p_20966_, Level p_20967_)
	{
		super(p_20966_, p_20967_);
	}
	
	@Inject(at = @At("HEAD"), method = "tick", cancellable = true)
	private void tick(CallbackInfo ci)
	{
		if(this.isTame() && this.getFirstPassenger() != null)
		{
			if(this.getFirstPassenger().isShiftKeyDown())
			{
				this.getFirstPassenger().stopRiding();
			}
		}

		if(this.isTame())
		{
			this.bossInfo.setVisible(false);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "defineSynchedData", cancellable = true)
	private void defineSynchedData(CallbackInfo ci)
	{
		this.getEntityData().define(COMMAND, 0);
		this.getEntityData().define(SITTING, false);
		this.getEntityData().define(DATA_FLAGS_ID, (byte)0);
		this.getEntityData().define(DATA_OWNERUUID_ID, Optional.empty());
	}
	  
	@Inject(at = @At("HEAD"), method = "addAdditionalSaveData", cancellable = true)
	private void addAdditionalSaveData(CompoundTag compound, CallbackInfo ci)
	{
	    compound.putBoolean("CmPetSitting", isSitting());
	    compound.putInt("Command", getCommand());
	    if (this.getOwnerUUID() != null) 
	    {
	    	compound.putUUID("Owner", this.getOwnerUUID());
	    }
	}
	
	@Inject(at = @At("HEAD"), method = "readAdditionalSaveData", cancellable = true)
	private void readAdditionalSaveData(CompoundTag compound, CallbackInfo ci)
	{
	    setOrderedToSit(compound.getBoolean("CmPetSitting"));
	    setCommand(compound.getInt("Command"));
	    if (compound.hasUUID("Owner")) 
	    {
    		this.setOwnerUUID(compound.getUUID("Owner"));
    		this.setTame(true);
	    }
	}
	
	@Inject(at = @At("HEAD"), method = "travel", cancellable = true)
	private void travel(Vec3 travelVector, CallbackInfo ci)
	{
		ci.cancel();
		LivingEntity livingentity = (LivingEntity) this.getFirstPassenger();
		boolean flag = this.isVehicle() && livingentity != null && this.isTame() && livingentity == this.getOwner();	
		if (this.isVehicle() && livingentity != null)
		{
			if(this.isTame() && livingentity == this.getOwner())
			{
	            this.setYRot(livingentity.getYRot());
	            this.yRotO = this.getYRot();
	            this.setXRot(livingentity.getXRot() * 0.5F);
	            this.setRot(this.getYRot(), this.getXRot());
	            this.yBodyRot = this.getYRot();
	            this.yHeadRot = this.yBodyRot;
			}
		}
		Vec3 vec = flag ? new Vec3(livingentity.xxa, (boolean) ObfuscationReflectionHelper.getPrivateValue(LivingEntity.class, livingentity, "f_20899_") ? this.getBlockJumpFactor() + 2.7 : travelVector.y, livingentity.zza) : travelVector;
		if (this.isEffectiveAi() && this.isInWater()) 
		{
			super.travel(vec);
			this.moveRelative(this.getSpeed(), vec);
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
			if (this.getTarget() == null && The_Leviathan_Entity.class.cast(this).getAnimation() == The_Leviathan_Entity.NO_ANIMATION)
			{
				this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
			}
		} 
		else
		{
			super.travel(vec);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "positionRider", cancellable = true)
	public void positionRider(Entity passenger, CallbackInfo ci) 
	{
		ci.cancel();
		super.positionRider(passenger);
		if(passenger != this.getOwner())
		{
			if (this.hasPassenger(passenger))
			{
				if (The_Leviathan_Entity.class.cast(this).getAnimation() == The_Leviathan_Entity.LEVIATHAN_TENTACLE_HOLD_BLAST) 
				{
					if (The_Leviathan_Entity.class.cast(this).getAnimationTick() == 169)
					{
						passenger.stopRiding(); 
					}
					this.setXRot(this.xRotO);
					this.yBodyRot = this.getYRot();
					this.yHeadRot = this.getYRot();
				} 
				float f17 = this.getYRot() * 3.1415927F / 180.0F;
				float pitch = this.getXRot() * 3.1415927F / 180.0F;
				float f3 = Mth.sin(f17) * (1.0F - Math.abs(this.getXRot() / 90.0F));
		        float f18 = Mth.cos(f17) * (1.0F - Math.abs(this.getXRot() / 90.0F));
		        passenger.setPos(this.getX() + (f3 * -8.25F), this.getY() + (-pitch * 6.0F), this.getZ() + (-f18 * -8.25F));
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "hurt", cancellable = true)
	protected void hurt(DamageSource source, float damage, CallbackInfoReturnable<Boolean> ci)
	{
		//TODO
	}
	
	@Shadow
	private boolean canInFluidType(FluidType type) 
	{
		throw new IllegalStateException();
	}
	
	@Inject(at = @At("HEAD"), method = "isAlliedTo", cancellable = true)
	private void isAlliedTo(Entity entity, CallbackInfoReturnable<Boolean> ci)
	{
		if (this.isTame()) 
		{
			LivingEntity livingentity = this.getOwner();
			ci.setReturnValue(entity == livingentity);
		}
	}
	
	@Inject(at = @At("HEAD"), method = "handleEntityEvent", cancellable = true)
	private void handleEntityEvent(byte id, CallbackInfo ci)
	{
		if(id == 67 && this.isTame())
		{
			ci.cancel();
		}
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
	public boolean isSitting() 
	{
		return this.getEntityData().get(SITTING);
	}

	@Override
	public void setOrderedToSit(boolean sit) 
	{
		this.getEntityData().set(SITTING, sit);
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
		return this.getEntityData().get(DATA_OWNERUUID_ID).orElse((UUID)null);
	}

	@Override
	public void setOwnerUUID(UUID p_21817_)
	{
		this.getEntityData().set(DATA_OWNERUUID_ID, Optional.ofNullable(p_21817_));
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
			return uuid == null ? null : this.level.getPlayerByUUID(uuid);
		} 
		catch (IllegalArgumentException illegalargumentexception)
		{
			return null;
		}
	}
}
