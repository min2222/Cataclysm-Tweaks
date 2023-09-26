package com.min01.cataclysmtweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.L_Ender.cataclysm.entity.BossMonsters.The_Leviathan.The_Leviathan_Entity;
import com.min01.cataclysmtweaks.misc.ITamableLeviathan;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.Team;

@Mixin(Entity.class)
public abstract class MixinEntity
{
	@Inject(at = @At("HEAD"), method = "getTeam", cancellable = true)
	protected void getTeam(CallbackInfoReturnable<Team> ci)
	{
		if(Entity.class.cast(this) instanceof The_Leviathan_Entity)
		{
			The_Leviathan_Entity leviathan = (The_Leviathan_Entity) Entity.class.cast(this);
			if (((ITamableLeviathan) leviathan).isTame())
			{
				LivingEntity livingentity = ((ITamableLeviathan) leviathan).getOwner();
				if (livingentity != null)
				{
					ci.setReturnValue(livingentity.getTeam());
				}
			}
		}
	}
}
