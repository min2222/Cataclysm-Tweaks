package com.min01.cataclysmtweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.Ancient_Remnant_Entity;
import com.github.L_Ender.cataclysm.entity.Pet.Modern_Remnant_Entity;
import com.github.L_Ender.cataclysm.init.ModEntities;
import com.min01.archaeology.init.ArchaeologySounds;
import com.min01.archaeology.item.BrushItem;
import com.min01.cataclysmtweaks.config.CataclysmTweaksConfig;
import com.min01.cataclysmtweaks.goal.CataclysmFollowOwnerGoal;
import com.min01.cataclysmtweaks.goal.CataclysmOwnerHurtByTargetGoal;
import com.min01.cataclysmtweaks.goal.CataclysmOwnerHurtTargetGoal;
import com.min01.cataclysmtweaks.misc.ITamable;
import com.min01.cataclysmtweaks.util.TameUtil;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistries;

@Mixin(BrushItem.class)
public class MixinBrushItem 
{
	@Inject(method = "onUseTick", at = @At(value = "HEAD"), cancellable = true)
	public void onUseTick(Level p_273467_, LivingEntity p_273619_, ItemStack p_273316_, int p_273101_, CallbackInfo ci) 
	{
		if (p_273101_ >= 0 && p_273619_ instanceof Player player) 
		{
			HitResult hitresult = this.calculateHitResult(p_273619_);
			if(hitresult instanceof EntityHitResult entityhitresult)
			{
				Entity entity = entityhitresult.getEntity();
				if(entity instanceof Modern_Remnant_Entity remnant && CataclysmTweaksConfig.canModernRemnantGrow.get())
				{
					Item growItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(CataclysmTweaksConfig.remnantGrowItem.get()));
					if(growItem instanceof BrushItem && remnant.isTame() && remnant.isOwnedBy(player))
					{
						int i = BrushItem.class.cast(this).getUseDuration(p_273316_) - p_273101_ + 1;
						boolean flag = i % 10 == 5;
						if (flag)
						{
							HumanoidArm humanoidarm = p_273619_.getUsedItemHand() == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
							this.spawnDustParticles(p_273467_, remnant, humanoidarm);
				        	remnant.gameEvent(GameEvent.EAT);
							p_273467_.playSound(player, remnant.blockPosition(), ArchaeologySounds.BRUSH_GENERIC.get(), SoundSource.BLOCKS, 1, 1);
				        	if(!player.getAbilities().instabuild)
				        	{
				        		p_273316_.hurtAndBreak(1, player, (p_219739_) -> 
				        		{
				        			p_219739_.broadcastBreakEvent(p_273619_.getUsedItemHand());
				        			net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, p_273316_, p_273619_.getUsedItemHand());
				        		});
				        	}
				            if (i >= 150) 
				            {
								Ancient_Remnant_Entity ancient = new Ancient_Remnant_Entity(ModEntities.ANCIENT_REMNANT.get(), p_273467_);
								TameUtil.setupTame((ITamable) ancient, remnant);
								ancient.setPos(remnant.position());
								ancient.goalSelector.addGoal(2, new CataclysmFollowOwnerGoal((ITamable) ancient, 1.3D, 4.0F, 2.0F, true));
								ancient.targetSelector.addGoal(1, new CataclysmOwnerHurtByTargetGoal((ITamable) ancient));
								ancient.targetSelector.addGoal(2, new CataclysmOwnerHurtTargetGoal((ITamable) ancient));
								p_273467_.addFreshEntity(ancient);
								remnant.discard();
				            }
						}
					}
				}
			}
		}
	}
	
	@Unique
	private void spawnDustParticles(Level p_278327_, Modern_Remnant_Entity p_278272_, HumanoidArm p_285071_) 
	{
		int i = p_285071_ == HumanoidArm.RIGHT ? 1 : -1;
		int j = p_278327_.getRandom().nextInt(7, 12);
		BlockParticleOption blockparticleoption = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState());
		for(int k = 0; k < j; ++k)
		{
			p_278327_.addParticle(blockparticleoption, p_278272_.getX(), p_278272_.getEyeY(), p_278272_.getZ(), (double)i * 3.0D * p_278327_.getRandom().nextDouble(), 0.0D, (double)i * 3.0D * p_278327_.getRandom().nextDouble());
		}
	}

	@Shadow
	private HitResult calculateHitResult(LivingEntity p_281264_)
	{
		throw new IllegalStateException();
	}
}
