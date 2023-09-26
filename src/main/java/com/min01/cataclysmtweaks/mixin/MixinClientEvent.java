package com.min01.cataclysmtweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.L_Ender.cataclysm.client.event.ClientEvent;
import com.github.L_Ender.cataclysm.entity.BossMonsters.The_Leviathan.The_Leviathan_Entity;
import com.github.L_Ender.cataclysm.init.ModEffect;
import com.min01.cataclysmtweaks.misc.ITamableLeviathan;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

@Mixin(ClientEvent.class)
public class MixinClientEvent
{
	@SuppressWarnings("resource")
	@Inject(at = @At("HEAD"), method = "onRenderHUD", cancellable = true, remap = false)
	private void onRenderHUD(RenderGuiOverlayEvent.Pre event, CallbackInfo ci)
	{
		ci.cancel();
	    LocalPlayer localPlayer = (Minecraft.getInstance()).player;
	    if (localPlayer != null) 
	    {
	    	if (localPlayer.isPassenger())
	    	{
	    		if(localPlayer.getVehicle() instanceof com.github.L_Ender.cataclysm.entity.BossMonsters.Ignis_Entity)
	    		{
		    		if (event.getOverlay().id().equals(VanillaGuiOverlay.HELMET.id()))
		    		{
		    			(Minecraft.getInstance()).gui.setOverlayMessage((Component)Component.translatable("you_cant_escape"), false); 
		    		}
		    		if (event.getOverlay().id().equals(VanillaGuiOverlay.MOUNT_HEALTH.id()))
		    		{
		    			event.setCanceled(true); 
		    		}
	    		}
	    		else if(localPlayer.getVehicle() instanceof com.github.L_Ender.cataclysm.entity.BossMonsters.The_Leviathan.The_Leviathan_Entity)
	    		{
	    			The_Leviathan_Entity leviathan = (The_Leviathan_Entity) localPlayer.getVehicle();
	    			if(!((ITamableLeviathan)leviathan).isTame())
	    			{
			    		if (event.getOverlay().id().equals(VanillaGuiOverlay.HELMET.id()))
			    		{
			    			(Minecraft.getInstance()).gui.setOverlayMessage((Component)Component.translatable("you_cant_escape"), false); 
			    		}
			    		if (event.getOverlay().id().equals(VanillaGuiOverlay.MOUNT_HEALTH.id()))
			    		{
			    			event.setCanceled(true); 
			    		}
	    			}
	    		}
	    	} 
	    	Minecraft mc = Minecraft.getInstance();
	    	ForgeGui gui = (ForgeGui)mc.gui;
	    	if (event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type() && !mc.options.hideGui && gui.shouldDrawSurvivalElements() && (localPlayer.hasEffect((MobEffect)ModEffect.EFFECTABYSSAL_BURN.get()) || localPlayer.hasEffect((MobEffect)ModEffect.EFFECTABYSSAL_CURSE.get())))
	    	{
	    		CustomHealth(event, 25); 
	    	}
	    } 
	}
	
	@Shadow
	private void CustomHealth(RenderGuiOverlayEvent.Pre event, int back)
	{
		
	}
}
