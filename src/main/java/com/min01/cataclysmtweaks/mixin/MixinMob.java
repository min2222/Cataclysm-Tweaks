package com.min01.cataclysmtweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.L_Ender.cataclysm.entity.BossMonsters.The_Leviathan.The_Leviathan_Entity;
import com.min01.cataclysmtweaks.config.CataclysmTweaksConfig;
import com.min01.cataclysmtweaks.misc.ITamableLeviathan;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mixin(Mob.class)
public abstract class MixinMob
{
	@Inject(at = @At("HEAD"), method = "mobInteract", cancellable = true)
	protected void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> ci)
	{
		if(Mob.class.cast(this) instanceof The_Leviathan_Entity)
		{
			The_Leviathan_Entity leviathan = (The_Leviathan_Entity) Mob.class.cast(this);
		    ItemStack itemstack = player.getItemInHand(hand);
		    if (((ITamableLeviathan) leviathan).isTame() && itemstack.is(ItemTags.FISHES))
		    {
		        if (The_Leviathan_Entity.class.cast(this).getHealth() < The_Leviathan_Entity.class.cast(this).getMaxHealth()) 
		        {
		        	this.usePlayerItem(player, hand, itemstack);
		        	The_Leviathan_Entity.class.cast(this).gameEvent(GameEvent.EAT);
		        	The_Leviathan_Entity.class.cast(this).heal(5.0F);
		        	ci.setReturnValue(InteractionResult.SUCCESS);
		        }
		    } 
		    InteractionResult interactionresult = itemstack.interactLivingEntity(player, The_Leviathan_Entity.class.cast(this), hand);
		    if (interactionresult != InteractionResult.SUCCESS /*&& super.mobInteract(player, hand) != InteractionResult.SUCCESS*/ && ((ITamableLeviathan) leviathan).isTame() && isOwnedBy(leviathan, (LivingEntity)player) && !player.isShiftKeyDown())
		    {
				String value = CataclysmTweaksConfig.leviathanMountItem.get();
		    	if(itemstack.getItem() != ForgeRegistries.ITEMS.getValue(new ResourceLocation(value.split(":")[0], value.split(":")[1])))
		    	{
			    	((ITamableLeviathan) leviathan).setCommand(((ITamableLeviathan) leviathan).getCommand() + 1);
			    	if (((ITamableLeviathan) leviathan).getCommand() == 3)
			    	{
			    		((ITamableLeviathan) leviathan).setCommand(0); 
			    	}
			    	player.displayClientMessage((Component)Component.translatable("entity.cataclysm.all.command_" + ((ITamableLeviathan) leviathan).getCommand(), new Object[] { The_Leviathan_Entity.class.cast(this).getName() }), true);
			    	boolean sit = (((ITamableLeviathan) leviathan).getCommand() == 2);
			    	if (sit)
			    	{
			    		((ITamableLeviathan) leviathan).setOrderedToSit(true);
			    		ci.setReturnValue(InteractionResult.SUCCESS);
			    	} 
			    	((ITamableLeviathan) leviathan).setOrderedToSit(false);
		    		ci.setReturnValue(InteractionResult.SUCCESS);
		    	}
		    	else if(itemstack.getItem() == ForgeRegistries.ITEMS.getValue(new ResourceLocation(value.split(":")[0], value.split(":")[1])))
		    	{
		            if (!The_Leviathan_Entity.class.cast(this).level.isClientSide)
		            {
		            	player.startRiding(The_Leviathan_Entity.class.cast(this));
		            }
		            ci.setReturnValue(InteractionResult.sidedSuccess(The_Leviathan_Entity.class.cast(this).level.isClientSide));
		    	}
		    }
		}
	}
	
	public boolean isOwnedBy(The_Leviathan_Entity leviathan, LivingEntity p_21831_) 
	{
		return p_21831_ == ((ITamableLeviathan) leviathan).getOwner();
	}
	
	protected void usePlayerItem(Player p_148715_, InteractionHand p_148716_, ItemStack p_148717_)
	{
		if (!p_148715_.getAbilities().instabuild)
		{
			p_148717_.shrink(1);
		}
	}
}
