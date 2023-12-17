package com.min01.cataclysmtweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public interface EntityInvoker 
{
	@Invoker("getBlockJumpFactor")
	public float invoke_getBlockJumpFactor();
}
