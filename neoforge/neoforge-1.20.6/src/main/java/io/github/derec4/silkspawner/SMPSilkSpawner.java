package io.github.derec4.silkspawner;

import io.github.derec4.silkspawner.config.ConfigManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(SMPSilkSpawner.MOD_ID)
public class SMPSilkSpawner {
    public static final String MOD_ID = "smp_silk_spawner";
    public static final String NAME = "SMP Silk Spawner";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public SMPSilkSpawner(IEventBus modEventBus) {
        modEventBus.addListener(this::setup);
        NeoForge.EVENT_BUS.register(BlockEventHandler.class);
    }

    private void setup(FMLCommonSetupEvent event) {
        ConfigManager.load(FMLPaths.CONFIGDIR.get().toFile());
    }
}

