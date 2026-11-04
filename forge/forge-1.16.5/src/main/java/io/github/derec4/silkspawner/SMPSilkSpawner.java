package io.github.derec4.silkspawner;

import io.github.derec4.silkspawner.config.ConfigManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SMPSilkSpawner.MOD_ID)
public class SMPSilkSpawner {
    public static final String MOD_ID = "smp_silk_spawner";
    public static final String NAME = "SMP Silk Spawner";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public SMPSilkSpawner() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(BlockEventHandler.class);
    }

    private void setup(FMLCommonSetupEvent event) {
        ConfigManager.load(FMLPaths.CONFIGDIR.get().toFile());
    }
}

