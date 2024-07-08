package com.min01.cataclysmtweaks.mixin;

import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.github.L_Ender.cataclysm.entity.Pet.The_Baby_Leviathan_Entity;
import com.min01.cataclysmtweaks.config.CataclysmTweaksConfig;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

@Mixin(The_Baby_Leviathan_Entity.class)
public class MixinEntityBabyLeviathan
{
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Ingredient;of([Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/item/crafting/Ingredient;"), method = "registerGoals")
	private Ingredient registerGoals(ItemLike[] p_43930_)
	{
		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(CataclysmTweaksConfig.leviathanTameItem.get()));
		return Ingredient.of(item);
	}
}
