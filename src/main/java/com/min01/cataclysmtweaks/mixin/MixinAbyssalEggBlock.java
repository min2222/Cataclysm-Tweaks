package com.min01.cataclysmtweaks.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.L_Ender.cataclysm.blocks.Abyssal_Egg_Block;
import com.min01.cataclysmtweaks.config.CataclysmTweaksConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

@Mixin(Abyssal_Egg_Block.class)
public class MixinAbyssalEggBlock 
{
	@Shadow
	@Final
    private static int RANDOM_HATCH_OFFSET_TICKS;

	@Inject(at = @At("HEAD"), method = "onPlace", cancellable = true)
    public void onPlace(BlockState p_277964_, Level p_277827_, BlockPos p_277526_, BlockState p_277618_, boolean p_277819_, CallbackInfo ci) 
    {
    	ci.cancel();
    	int j = CataclysmTweaksConfig.abyssalEggHatchTime.get() / 3;
    	p_277827_.gameEvent(GameEvent.BLOCK_PLACE, p_277526_, GameEvent.Context.of(p_277964_));
    	p_277827_.scheduleTick(p_277526_, Abyssal_Egg_Block.class.cast(this), j + p_277827_.random.nextInt(RANDOM_HATCH_OFFSET_TICKS));
    }
}
