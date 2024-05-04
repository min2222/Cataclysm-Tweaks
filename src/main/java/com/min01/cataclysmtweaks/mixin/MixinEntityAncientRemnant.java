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

import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.Ancient_Remnant_Entity;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

@Mixin(Ancient_Remnant_Entity.class)
public abstract class MixinEntityAncientRemnant extends  Mob implements ITamable
{
	private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(Ancient_Remnant_Entity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(Ancient_Remnant_Entity.class, EntityDataSerializers.OPTIONAL_UUID);
	private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Ancient_Remnant_Entity.class, EntityDataSerializers.BYTE);

	@Shadow
	@Final
	private CMBossInfoServer bossEvent;
	
	public MixinEntityAncientRemnant(EntityType<? extends Mob> p_21368_, Level p_21369_) 
	{
		super(p_21368_, p_21369_);
	}
	
	@Inject(at = @At("HEAD"), method = "registerGoals", cancellable = true)
	private void registerGoals(CallbackInfo ci)
	{
		if(this.isTame())
		{
			Ancient_Remnant_Entity.class.cast(this).goalSelector.addGoal(2, new CataclysmFollowOwnerGoal((ITamable) Ancient_Remnant_Entity.class.cast(this), 1.3D, 4.0F, 2.0F, true));
			Ancient_Remnant_Entity.class.cast(this).targetSelector.addGoal(1, new CataclysmOwnerHurtByTargetGoal((ITamable) Ancient_Remnant_Entity.class.cast(this)));
			Ancient_Remnant_Entity.class.cast(this).targetSelector.addGoal(2, new CataclysmOwnerHurtTargetGoal((ITamable) Ancient_Remnant_Entity.class.cast(this)));
		}
	}
	
	@Inject(at = @At("HEAD"), method = "tick", cancellable = true)
	private void tick(CallbackInfo ci)
	{
		TameUtil.tick(this, this, this.bossEvent);
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
	public void travel(Vec3 vec)
	{
		if(this.isVehicle() && this.isTame())
		{
			if(this.getOwner() != null && this.getFirstPassenger() == this.getOwner())
			{
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
    			super.travel(vec.add(travelVector));
			}
		}
		else
		{
			super.travel(vec);
		}
	}
	
	@Override
	public double getPassengersRidingOffset() 
	{
		return super.getPassengersRidingOffset() + 0.5;
	}
	
	@Override
	public void positionRider(Entity p_20312_) 
	{
		Vec3 lookPos = this.getLookPos(0, this.getYRot(), 0, 3);
		p_20312_.setPos(this.position().add(lookPos.x, lookPos.y + this.getPassengersRidingOffset(), lookPos.z));
	}
	
	private Vec3 getLookPos(float xRot, float yRot, float yPos, double distance)
	{
		float f = -Mth.sin(yRot * ((float)Math.PI / 180F)) * Mth.cos(xRot * ((float)Math.PI / 180F));
		float f1 = -Mth.sin((xRot + yPos) * ((float)Math.PI / 180F));
		float f2 = Mth.cos(yRot * ((float)Math.PI / 180F)) * Mth.cos(xRot * ((float)Math.PI / 180F));
		return new Vec3(f, f1, f2).scale(distance);
	}
	
	@Override
	public Team getTeam()
	{
		return TameUtil.getTeam(this, super.getTeam());
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
			return uuid == null ? null : this.level.getPlayerByUUID(uuid);
		} 
		catch (IllegalArgumentException illegalargumentexception)
		{
			return null;
		}
	}
}
