package com.min01.cataclysmtweaks.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.L_Ender.cataclysm.entity.BossMonster.The_Leviathan.Dimensional_Rift_Entity;
import com.github.L_Ender.cataclysm.entity.effect.Cm_Falling_Block_Entity;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.init.ModSounds;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

@Mixin(Dimensional_Rift_Entity.class)
public abstract class MixinEntityDimensionalRift extends Entity
{
	@Shadow
	private boolean madeOpenNoise;
	  
	@Shadow
	private boolean madeCloseNoise;
	  
	@Shadow
	private boolean madeParticle;
	
	@Shadow
	@Nullable
	private LivingEntity owner;
	
	@Shadow
	public int ambientSoundTime;
	  
	public MixinEntityDimensionalRift(EntityType<?> p_19870_, Level p_19871_) 
	{
		super(p_19870_, p_19871_);
	}

	@Inject(at = @At("HEAD"), method = "tick", cancellable = true)
	private void tick(CallbackInfo ci)
	{
		ci.cancel();
	    super.tick();
	    if (!this.madeOpenNoise)
	    {
	    	gameEvent(GameEvent.ENTITY_PLACE);
	    	playSound((SoundEvent)ModSounds.BLACK_HOLE_OPENING.get(), 0.7F, 1.0F + this.random.nextFloat() * 0.2F);
	    	this.madeOpenNoise = true;
	    } 
	    for (Entity entity : this.level.getEntities(this, getBoundingBox().inflate(30.0D))) 
	    {
	    	if (entity == this.owner || (entity instanceof Player && (((Player)entity).getAbilities()).invulnerable) || entity instanceof com.github.L_Ender.cataclysm.entity.BossMonster.The_Leviathan.The_Leviathan_Entity || this.isAlliedTo(entity))
	    		continue; 
	    	Vec3 diff = entity.position().subtract(position().add(0.0D, 0.0D, 0.0D));
	    	if (entity instanceof LivingEntity) 
	    	{
	    		diff = diff.normalize().scale(getStage() * 0.015D);
	    		if(this.owner != null && !this.owner.isAlliedTo(entity))
	    		{
		    		entity.setDeltaMovement(entity.getDeltaMovement().subtract(diff));
	    		}
	    		continue;
	    	} 
	    	diff = diff.normalize().scale(getStage() * 0.045D);
	    	entity.setDeltaMovement(entity.getDeltaMovement().subtract(diff));
	    } 
	    berserkBlockBreaking(15, 15, 15);
	    for (LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.2D, 0.0D, 0.2D)))
	    {
	    	damage(livingentity); 
	    }
	    for (Entity entity : this.level.getEntities(this, getBoundingBox().inflate(0.5D))) 
	    {
	    	if (entity instanceof Cm_Falling_Block_Entity)
	    	{
	    		entity.remove(Entity.RemovalReason.DISCARDED); 
	    	}
	    } 
	    if (this.random.nextInt(3000) < this.ambientSoundTime++)
	    {
	    	resetAmbientSoundTime();
	    	playSound((SoundEvent)ModSounds.BLACK_HOLE_LOOP.get(), 0.7F, 1.0F + this.random.nextFloat() * 0.2F);
	    } 
	    setLifespan(getLifespan() - 1);
	    if (getLifespan() <= 100)
	    {
	    	if (!this.madeCloseNoise)
	    	{
	    		gameEvent(GameEvent.ENTITY_PLACE);
	    		playSound((SoundEvent)ModSounds.BLACK_HOLE_CLOSING.get(), 0.7F, 1.0F + this.random.nextFloat() * 0.2F);
	    		this.madeCloseNoise = true;
	    	} 
	    	if (this.tickCount % 40 == 0)
	    	{
	    		setStage(getStage() - 1); 
	    	}
	    	if (getStage() <= 0)
	    	{
	    		if (!this.madeParticle) 
	    		{
	    			if (this.level.isClientSide)
	    			{
	    				this.level.addParticle((ParticleOptions)ModParticle.SHOCK_WAVE.get(), getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
	    			} 
	    			else
	    			{
	    				this.level.explode((Entity)this.owner, getX(), getY(), getZ(), 4.0F, false, Explosion.BlockInteraction.NONE);
	    			} 
	    			this.madeParticle = true;
	    		}
	    		else 
	    		{
	    			discard();
	    		}  
	    	}
	    } 
	}
	
	@Shadow
	private void damage(LivingEntity Hitentity) 
	{
		 
	}
	
	@Shadow
	private void berserkBlockBreaking(int x, int y, int z) 
	{
		  
	}
	
	@Shadow
	private void resetAmbientSoundTime() 
	{
		  
	}
	
	@Shadow
	public int getLifespan()
	{
		throw new IllegalStateException();
	}
		  
	@Shadow
	public void setLifespan(int i) 
	{
		
	}
		  
	@Shadow
	public int getStage() 
	{
		throw new IllegalStateException();
	}
		
	@Shadow
	public void setStage(int i) 
	{
		
	}
}
