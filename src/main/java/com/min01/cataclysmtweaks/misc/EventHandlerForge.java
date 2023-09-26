package com.min01.cataclysmtweaks.misc;

import java.util.Iterator;
import java.util.List;

import com.github.L_Ender.cataclysm.entity.BossMonsters.The_Leviathan.The_Leviathan_Entity;
import com.github.L_Ender.cataclysm.entity.Pet.The_Baby_Leviathan_Entity;
import com.github.L_Ender.cataclysm.init.ModEntities;
import com.github.L_Ender.cataclysm.init.ModItems;
import com.min01.cataclysmtweaks.CataclysmTweaks;
import com.min01.cataclysmtweaks.config.CataclysmTweaksConfig;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = CataclysmTweaks.MODID, bus = Bus.FORGE)
public class EventHandlerForge 
{
	@SubscribeEvent
	public static void babyLeviathanTame(PlayerInteractEvent.EntityInteract event)
	{
		if(CataclysmTweaksConfig.canBabyLeviathanGrow.get())
		{
			if(event.getTarget() instanceof The_Baby_Leviathan_Entity)
			{
				The_Baby_Leviathan_Entity baby = (The_Baby_Leviathan_Entity) event.getTarget();
				if(baby.isTame() && baby.isOwnedBy(event.getEntity()))
				{
					String value = CataclysmTweaksConfig.leviathanGrowItem.get();
					if(event.getItemStack().getItem() == ForgeRegistries.ITEMS.getValue(new ResourceLocation(value.split(":")[0], value.split(":")[1])))
					{
						if(!event.getEntity().getAbilities().instabuild)
						{
							event.getItemStack().shrink(1);
						}
						The_Leviathan_Entity leviathan = new The_Leviathan_Entity(ModEntities.THE_LEVIATHAN.get(), event.getLevel());
						leviathan.setPos(baby.position());
						((ITamableLeviathan)leviathan).tame((Player) baby.getOwner());
						((ITamableLeviathan)leviathan).setCommand(baby.getCommand());
						((ITamableLeviathan)leviathan).setOrderedToSit(baby.isSitting());
						leviathan.goalSelector.addGoal(2, new LeviathanFollowOwnerGoal(leviathan, 1.3D, 4.0F, 2.0F, true));
						leviathan.targetSelector.addGoal(1, new LeviathanOwnerHurtByTargetGoal(leviathan));
						leviathan.targetSelector.addGoal(2, new LeviathanOwnerHurtTargetGoal(leviathan));
						event.getLevel().addFreshEntity(leviathan);
						baby.discard();
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void leviathanDrop(LivingDropsEvent event)
	{
		if(event.getEntity() instanceof The_Leviathan_Entity)
		{
			The_Leviathan_Entity leviathan = (The_Leviathan_Entity) event.getEntity();
			if(((ITamableLeviathan) leviathan).isTame())
			{
				for(ItemEntity entity : event.getDrops())
				{
					if(entity.getItem().getItem() == ModItems.TIDAL_CLAWS.get())
					{
						entity.discard();
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void leviathanExplosion(ExplosionEvent.Detonate event)
	{
		if(event.getExplosion().getSourceMob() instanceof The_Leviathan_Entity)
		{
			The_Leviathan_Entity leviathan = (The_Leviathan_Entity) event.getExplosion().getSourceMob();
			if(((ITamableLeviathan) leviathan).isTame())
			{
				List<Entity> list = event.getAffectedEntities();
				for(Iterator<Entity> itr = list.iterator(); itr.hasNext();)
				{
					Entity entity = itr.next();
					if(entity instanceof LivingEntity)
					{
						LivingEntity player = (LivingEntity) entity;
						if(player == ((ITamableLeviathan)leviathan).getOwner())
						{
							itr.remove();
						}
					}
				}
			}
		}
	}
}
