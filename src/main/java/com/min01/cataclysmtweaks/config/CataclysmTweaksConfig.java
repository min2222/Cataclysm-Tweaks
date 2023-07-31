package com.min01.cataclysmtweaks.config;

import java.io.File;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class CataclysmTweaksConfig 
{
    private static ForgeConfigSpec.Builder builder;
    public static ForgeConfigSpec config;
	public static ForgeConfigSpec.BooleanValue canBabyLeviathanGrow;
	public static ConfigValue<String> leviathanMountItem;
	public static ConfigValue<String> leviathanGrowItem;
    
    public static void loadConfig(ForgeConfigSpec config, String path) 
    {
        CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        config.setConfig(file);
    }
    
    static 
    {
    	builder = new ForgeConfigSpec.Builder();
    	CataclysmTweaksConfig.init(CataclysmTweaksConfig.builder);
        config = CataclysmTweaksConfig.builder.build();
    }
	
    public static void init(ForgeConfigSpec.Builder builder) 
    {
    	builder.push("Tweak Settings");
    	CataclysmTweaksConfig.canBabyLeviathanGrow = builder.comment("setting this to true will allows baby leviathan to grow into an adult").define("canBabyLeviathanGrow", true);
    	CataclysmTweaksConfig.leviathanMountItem = builder.comment("item used for mount tamed adult leviathan").define("leviathanMountItem", "minecraft:saddle");
    	CataclysmTweaksConfig.leviathanGrowItem = builder.comment("item used for grow baby leviathan to adult").define("leviathanGrowItem", "minecraft:dragon_egg");
    	builder.pop();
    }
}
