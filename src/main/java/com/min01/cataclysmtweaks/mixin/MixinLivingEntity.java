package com.min01.cataclysmtweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.L_Ender.cataclysm.entity.BossMonsters.The_Leviathan.The_Leviathan_Entity;
import com.min01.cataclysmtweaks.misc.ITamableLeviathan;

import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity
{
	@Inject(at = @At("HEAD"), method = "canAttack", cancellable = true)
	protected void canAttack(LivingEntity living, CallbackInfoReturnable<Boolean> ci)
	{
		if(LivingEntity.class.cast(this) instanceof The_Leviathan_Entity)
		{
			The_Leviathan_Entity leviathan = (The_Leviathan_Entity) LivingEntity.class.cast(this);
			if(this.isOwnedBy(leviathan, living))
			{
				ci.setReturnValue(false);
			}
		}
	}
	
	public boolean isOwnedBy(The_Leviathan_Entity leviathan, LivingEntity p_21831_) 
	{
		return p_21831_ == ((ITamableLeviathan) leviathan).getOwner();
	}
}
