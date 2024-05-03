package com.min01.cataclysmtweaks;

import com.min01.cataclysmtweaks.config.CataclysmTweaksConfig;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(CataclysmTweaks.MODID)
public class CataclysmTweaks
{
	public static final String MODID = "cataclysmtweaks";
	public static IEventBus MOD_EVENT_BUS;
	
	public CataclysmTweaks() 
	{
		MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
		CataclysmTweaksConfig.loadConfig(CataclysmTweaksConfig.CONFIG, FMLPaths.CONFIGDIR.get().resolve("cataclysm-tweaks.toml").toString());
	}
}
