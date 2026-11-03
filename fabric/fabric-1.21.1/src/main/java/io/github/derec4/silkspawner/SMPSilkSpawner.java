package io.github.derec4.silkspawner;

import io.github.derec4.silkspawner.config.ConfigManager;
import io.github.derec4.silkspawner.listener.BlockBreakHandler;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMPSilkSpawner implements ModInitializer {
    public static final String MOD_ID = "smp_silk_spawner";
    public static final Logger LOGGER = LoggerFactory.getLogger("SMPSilkSpawner");

    @Override
    public void onInitialize() {
        ConfigManager.load();
        BlockBreakHandler.register();
    }
}

