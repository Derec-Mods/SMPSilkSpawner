package io.github.derec4.silkspawner;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import io.github.derec4.silkspawner.config.ConfigManager;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = SMPSilkSpawner.MOD_ID, name = SMPSilkSpawner.NAME, version = Tags.VERSION, acceptedMinecraftVersions = "[1.7.10]")
public class SMPSilkSpawner {
    public static final String MOD_ID = "smp_silk_spawner";
    public static final String NAME = "SMP Silk Spawner";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigManager.load(event.getModConfigurationDirectory());
        MinecraftForge.EVENT_BUS.register(new BlockEventHandler());
    }
}

