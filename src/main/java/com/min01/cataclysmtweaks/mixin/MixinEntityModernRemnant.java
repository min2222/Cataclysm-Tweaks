package com.min01.cataclysmtweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.github.L_Ender.cataclysm.entity.Pet.Modern_Remnant_Entity;
import com.min01.cataclysmtweaks.config.CataclysmTweaksConfig;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

@Mixin(Modern_Remnant_Entity.class)
public class MixinEntityModernRemnant 
{
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Ingredient;of([Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/item/crafting/Ingredient;"), method = "registerGoals")
	private Ingredient registerGoals(ItemLike[] p_43930_)
	{
		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(CataclysmTweaksConfig.remnantTameItem.get()));
		return Ingredient.of(item);
	}
	
	//FIXME not working
	/*@Inject(at = @At("HEAD"), method = "mobInteract", cancellable = true)
	private void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir)
	{
        if(CataclysmTweaksConfig.canModernRemnantGrow.get()) 
        {
			if(Modern_Remnant_Entity.class.cast(this).isTame() && Modern_Remnant_Entity.class.cast(this).isOwnedBy(player))
			{
		        ItemStack itemstack = player.getItemInHand(hand);
		        Item item = itemstack.getItem();
				Item growItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(CataclysmTweaksConfig.remnantGrowItem.get()));
				if(item == Items.BRUSH && growItem instanceof BrushItem)
				{
		        	player.startUsingItem(hand);
		        	cir.setReturnValue(InteractionResult.PASS);
				}
			}
        }
	}
	
	@Redirect(at = @At(value = "INVOKE", target = "Lcom/github/L_Ender/cataclysm/entity/Pet/Modern_Remnant_Entity;setCommand(I)V"), method = "mobInteract", remap = false)
	private void setCommand(Modern_Remnant_Entity instance, int command)
	{
        ItemStack itemstack = instance.getOwner().getItemInHand(instance.getOwner().getUsedItemHand());
        Item item = itemstack.getItem();
		Item growItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(CataclysmTweaksConfig.remnantGrowItem.get()));
        boolean flag = CataclysmTweaksConfig.canModernRemnantGrow.get() ? item != Items.BRUSH && !(growItem instanceof BrushItem) : true;
        if(flag)
        {
			instance.setCommand(command);
        }
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lcom/github/L_Ender/cataclysm/entity/Pet/Modern_Remnant_Entity;setOrderedToSit(Z)V"), method = "mobInteract", remap = false)
	private void setOrderedToSit(Modern_Remnant_Entity instance, boolean sit)
	{
        ItemStack itemstack = instance.getOwner().getItemInHand(instance.getOwner().getUsedItemHand());
        Item item = itemstack.getItem();
		Item growItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(CataclysmTweaksConfig.remnantGrowItem.get()));
        boolean flag = CataclysmTweaksConfig.canModernRemnantGrow.get() ? item != Items.BRUSH && !(growItem instanceof BrushItem) : true;
        if(flag)
        {
			instance.setOrderedToSit(sit);
        }
	}*/
}
