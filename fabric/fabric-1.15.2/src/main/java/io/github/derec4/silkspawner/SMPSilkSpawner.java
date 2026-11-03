package io.github.derec4.silkspawner;

import io.github.derec4.silkspawner.config.ConfigManager;
import io.github.derec4.silkspawner.listener.BlockBreakHandler;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SMPSilkSpawner implements ModInitializer {
    public static final String MOD_ID = "smp_silk_spawner";
    public static final Logger LOGGER = LogManager.getLogger("SMPSilkSpawner");

    @Override
    public void onInitialize() {
        ConfigManager.load();
        BlockBreakHandler.register();
    }
}

