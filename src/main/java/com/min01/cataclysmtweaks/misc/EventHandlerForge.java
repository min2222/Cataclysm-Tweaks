package com.min01.cataclysmtweaks.misc;

import java.util.Iterator;
import java.util.List;

import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.Ancient_Remnant_Entity;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.The_Leviathan_Entity;
import com.github.L_Ender.cataclysm.entity.Pet.Modern_Remnant_Entity;
import com.github.L_Ender.cataclysm.entity.Pet.The_Baby_Leviathan_Entity;
import com.github.L_Ender.cataclysm.init.ModBlocks;
import com.github.L_Ender.cataclysm.init.ModEntities;
import com.github.L_Ender.cataclysm.init.ModItems;
import com.min01.cataclysmtweaks.CataclysmTweaks;
import com.min01.cataclysmtweaks.config.CataclysmTweaksConfig;
import com.min01.cataclysmtweaks.goal.CataclysmFollowOwnerGoal;
import com.min01.cataclysmtweaks.goal.CataclysmOwnerHurtByTargetGoal;
import com.min01.cataclysmtweaks.goal.CataclysmOwnerHurtTargetGoal;
import com.min01.cataclysmtweaks.util.TameUtil;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
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
	public static void onLivingTick(LivingTickEvent event)
	{
		Entity entity = event.getEntity();
		if(entity instanceof The_Leviathan_Entity leviathan)
		{
			if(((ITamable) leviathan).isTame())
			{
				leviathan.goalSelector.addGoal(2, new CataclysmFollowOwnerGoal((ITamable) leviathan, 1.3D, 4.0F, 2.0F, true));
				leviathan.targetSelector.addGoal(1, new CataclysmOwnerHurtByTargetGoal((ITamable) leviathan));
				leviathan.targetSelector.addGoal(2, new CataclysmOwnerHurtTargetGoal((ITamable) leviathan));
			}
		}
		
		if(entity instanceof Ancient_Remnant_Entity ancient)
		{
			if(((ITamable) ancient).isTame())
			{
				ancient.goalSelector.addGoal(2, new CataclysmFollowOwnerGoal((ITamable) ancient, 1.3D, 4.0F, 2.0F, true));
				ancient.targetSelector.addGoal(1, new CataclysmOwnerHurtByTargetGoal((ITamable) ancient));
				ancient.targetSelector.addGoal(2, new CataclysmOwnerHurtTargetGoal((ITamable) ancient));
			}
		}
	}
	
	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteract event)
	{
		Level level = event.getLevel();
		Entity target = event.getTarget();
		Player player = event.getEntity();
		ItemStack stack = event.getItemStack();
		if(CataclysmTweaksConfig.canBabyLeviathanGrow.get() && target instanceof The_Baby_Leviathan_Entity baby)
		{
			Item growItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(CataclysmTweaksConfig.leviathanGrowItem.get()));
			if(baby.isTame() && baby.isOwnedBy(player) && stack.getItem() == growItem)
			{
				if(!player.getAbilities().instabuild)
				{
					stack.shrink(1);
				}
				The_Leviathan_Entity leviathan = new The_Leviathan_Entity(ModEntities.THE_LEVIATHAN.get(), level);
				TameUtil.setupTame((ITamable) leviathan, baby);
				leviathan.setPos(baby.position());
				leviathan.goalSelector.addGoal(2, new CataclysmFollowOwnerGoal((ITamable) leviathan, 1.3D, 4.0F, 2.0F, true));
				leviathan.targetSelector.addGoal(1, new CataclysmOwnerHurtByTargetGoal((ITamable) leviathan));
				leviathan.targetSelector.addGoal(2, new CataclysmOwnerHurtTargetGoal((ITamable) leviathan));
				level.addFreshEntity(leviathan);
				baby.discard();
			}
		}
		
		if(CataclysmTweaksConfig.canModernRemnantGrow.get() && target instanceof Modern_Remnant_Entity remnant)
		{
			Item growItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(CataclysmTweaksConfig.remnantGrowItem.get()));
			if(remnant.isTame() && remnant.isOwnedBy(player) && stack.getItem() == growItem && !(growItem instanceof BrushItem))
			{
				if(!player.getAbilities().instabuild)
				{
					stack.shrink(1);
				}
				Ancient_Remnant_Entity ancient = new Ancient_Remnant_Entity(ModEntities.ANCIENT_REMNANT.get(), level);
				TameUtil.setupTame((ITamable) ancient, remnant);
				ancient.setPos(remnant.position());
				ancient.goalSelector.addGoal(2, new CataclysmFollowOwnerGoal((ITamable) ancient, 1.3D, 4.0F, 2.0F, true));
				ancient.targetSelector.addGoal(1, new CataclysmOwnerHurtByTargetGoal((ITamable) ancient));
				ancient.targetSelector.addGoal(2, new CataclysmOwnerHurtTargetGoal((ITamable) ancient));
				level.addFreshEntity(ancient);
				remnant.discard();
			}
		}
		
		if(target instanceof ITamable tame)
		{
			if(tame.getOwner() == player)
			{
				if(!player.isShiftKeyDown())
				{
					player.startRiding((Entity) tame);
				}
				else
				{
					int command = tame.getCommand() + 1;
					tame.setCommand(command >= 3 ? 0 : command);
	                player.displayClientMessage(Component.translatable("entity.cataclysm.all.command_" + tame.getCommand(), ((Entity) tame).getName()), true);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onDrop(LivingDropsEvent event)
	{
		if(event.getEntity() instanceof The_Leviathan_Entity)
		{
			The_Leviathan_Entity leviathan = (The_Leviathan_Entity) event.getEntity();
			if(((ITamable) leviathan).isTame())
			{
				for(ItemEntity entity : event.getDrops())
				{
					if(entity.getItem().getItem() != ModBlocks.ABYSSAL_EGG.get().asItem())
					{
						entity.discard();
					}
				}
			}
		}
		
		if(event.getEntity() instanceof Ancient_Remnant_Entity)
		{
			Ancient_Remnant_Entity remnant = (Ancient_Remnant_Entity) event.getEntity();
			if(((ITamable) remnant).isTame())
			{
				for(ItemEntity entity : event.getDrops())
				{
					if(entity.getItem().getItem() != ModItems.REMNANT_SKULL.get())
					{
						entity.discard();
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onDetonate(ExplosionEvent.Detonate event)
	{
		if(event.getExplosion().getIndirectSourceEntity() instanceof The_Leviathan_Entity)
		{
			The_Leviathan_Entity leviathan = (The_Leviathan_Entity) event.getExplosion().getIndirectSourceEntity();
			if(((ITamable) leviathan).isTame())
			{
				List<Entity> list = event.getAffectedEntities();
				for(Iterator<Entity> itr = list.iterator(); itr.hasNext();)
				{
					Entity entity = itr.next();
					if(entity instanceof LivingEntity)
					{
						LivingEntity player = (LivingEntity) entity;
						if(player == ((ITamable)leviathan).getOwner())
						{
							itr.remove();
						}
					}
				}
			}
		}
	}
}
