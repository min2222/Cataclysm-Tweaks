package com.min01.cataclysmtweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.Dimensional_Rift_Entity;
import com.min01.cataclysmtweaks.misc.ITamable;
import com.min01.cataclysmtweaks.util.TameUtil;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

@Mixin(Dimensional_Rift_Entity.class)
public abstract class MixinEntityDimensionalRift extends Entity
{
	public MixinEntityDimensionalRift(EntityType<?> p_19870_, Level p_19871_) 
	{
		super(p_19870_, p_19871_);
	}

	@Override
	public boolean isAlliedTo(Entity p_20355_)
	{
		if(Dimensional_Rift_Entity.class.cast(this).getOwner() != null)
		{
			ITamable tame = (ITamable) Dimensional_Rift_Entity.class.cast(this).getOwner();
			return TameUtil.isAlliedTo(tame, p_20355_);
		}
		return super.isAlliedTo(p_20355_);
	}
}
