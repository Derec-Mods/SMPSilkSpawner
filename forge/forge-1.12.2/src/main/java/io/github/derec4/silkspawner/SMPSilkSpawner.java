package io.github.derec4.silkspawner;

import io.github.derec4.silkspawner.config.ConfigManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = SMPSilkSpawner.MOD_ID, name = SMPSilkSpawner.NAME, version = SMPSilkSpawner.VERSION, acceptedMinecraftVersions = "[1.12.2]")
public class SMPSilkSpawner {
    public static final String MOD_ID = "smp_silk_spawner";
    public static final String NAME = "SMP Silk Spawner";
    public static final String VERSION = "1.0.0.0";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigManager.load(event.getModConfigurationDirectory());
        MinecraftForge.EVENT_BUS.register(BlockEventHandler.class);
    }
}

