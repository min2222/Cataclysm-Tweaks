package com.min01.cataclysmtweaks.config;

import java.io.File;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class CataclysmTweaksConfig 
{
    private static ForgeConfigSpec.Builder BUILDER;
    public static ForgeConfigSpec CONFIG;
	public static ForgeConfigSpec.BooleanValue canBabyLeviathanGrow;
	public static ConfigValue<String> leviathanGrowItem;
	
	public static ForgeConfigSpec.BooleanValue canModernRemnantGrow;
	public static ConfigValue<String> remnantGrowItem;

	public static ConfigValue<String> remnantTameItem;
	public static ConfigValue<String> leviathanTameItem;
	
	public static ConfigValue<Integer> abyssalEggHatchTime;
    
    public static void loadConfig(ForgeConfigSpec config, String path) 
    {
        CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        config.setConfig(file);
    }
    
    static 
    {
    	BUILDER = new ForgeConfigSpec.Builder();
    	CataclysmTweaksConfig.init(CataclysmTweaksConfig.BUILDER);
        CONFIG = CataclysmTweaksConfig.BUILDER.build();
    }
	
    public static void init(ForgeConfigSpec.Builder builder) 
    {
    	builder.push("Tweak Settings");
    	CataclysmTweaksConfig.canBabyLeviathanGrow = builder.comment("setting this to true will allows baby leviathan to grow into an adult").define("canBabyLeviathanGrow", true);
    	CataclysmTweaksConfig.leviathanGrowItem = builder.comment("item used for grow baby leviathan to adult").define("leviathanGrowItem", "minecraft:dragon_egg");
    	CataclysmTweaksConfig.leviathanTameItem = builder.comment("item used for tame baby leviathan").define("leviathanTameItem", "minecraft:tropical_fish");
    	
    	CataclysmTweaksConfig.canModernRemnantGrow = builder.comment("setting this to true will allows modern remnant to grow into an ancient remnant").define("canModernRemnantGrow", true);
    	CataclysmTweaksConfig.remnantGrowItem = builder.comment("item used for modern remnant to ancient remnant").define("remnantGrowItem", "minecraft:brush");
    	CataclysmTweaksConfig.remnantTameItem = builder.comment("item used for tame modern remnant").define("remnantTameItem", "minecraft:sniffer_egg");

    	CataclysmTweaksConfig.abyssalEggHatchTime = builder.comment("second until baby leviathan hatch").define("abyssalEggHatchTime", 12000);
    	
    	builder.pop();
    }
}
