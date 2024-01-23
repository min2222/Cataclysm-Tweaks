package com.min01.cataclysmtweaks.mixin;

import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.L_Ender.cataclysm.entity.BossMonsters.The_Leviathan.The_Leviathan_Entity;
import com.github.L_Ender.cataclysm.entity.etc.CMBossInfoServer;
import com.min01.cataclysmtweaks.misc.EventHandlerForge;
import com.min01.cataclysmtweaks.misc.ITamableLeviathan;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.damagesource.DamageSource;
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
		if(this.isTame())
		{
			this.setPersistenceRequired();
			this.bossInfo.setVisible(false);
			
			if(this.getTarget() != null)
			{
				if(EventHandlerForge.TARGET_MAP.get(this.getOwner()) != this.getTarget())
				{
					this.setTarget(null);
				}
			}
			else
			{
				if(EventHandlerForge.TARGET_MAP.get(this.getOwner()) instanceof LivingEntity living)
				{
					this.setTarget(living);
				}
			}
			
			if(this.isVehicle())
			{
				if(this.getFirstPassenger() == this.getOwner())
				{
					if(this.getOwner().isShiftKeyDown())
					{
						this.getOwner().stopRiding();
					}
				}
			}
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
	    UUID uuid;
	    if (compound.hasUUID("Owner"))
	    {
	    	uuid = compound.getUUID("Owner");
	    } 
	    else
	    {
	    	String s = compound.getString("Owner");
	    	uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
	    }
	      
	    if (uuid != null)
	    {
	    	try 
	    	{
	    		this.setOwnerUUID(uuid);
	    		this.setTame(true);
	    	} 
	    	catch (Throwable throwable) 
	    	{
	    		this.setTame(false);
	    	}
	    }
	}
	
	@Override
	public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider)
	{
		if(this.isTame() && this.getOwner() != null)
		{
			return rider == this.getOwner();
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
					if(this.isOnGround() || this.isInWater())
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
	
	@Inject(at = @At("HEAD"), method = "HoldAttack", cancellable = true, remap = false)
	private void HoldAttack(CallbackInfo ci) 
	{
		LivingEntity lifted = getHeldEntity();
		if (lifted != null) 
		{
			if(lifted == this.getOwner())
			{
				ci.cancel();
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
	
	@Inject(at = @At("HEAD"), method = "canPlayMusic", cancellable = true, remap = false)
	private void canPlayMusic(CallbackInfoReturnable<Boolean> ci)
	{
		if(this.isTame())
		{
			ci.setReturnValue(false);
		}
	}
	
	@Shadow
	public void setheldEntity(int p_175463_1_)
	{
		
	}
	
	@Nullable
	@Shadow
	public LivingEntity getHeldEntity()
	{
		throw new IllegalStateException();
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
