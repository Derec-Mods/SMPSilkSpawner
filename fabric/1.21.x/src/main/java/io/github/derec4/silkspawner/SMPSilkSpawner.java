package io.github.derec4.silkspawner;

import io.github.derec4.silkspawner.config.ConfigManager;
import io.github.derec4.silkspawner.listener.BlockBreakHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMPSilkSpawner implements ModInitializer {

    public static final String MOD_ID = "smp_silk_spawner";
    public static final Logger LOGGER = LoggerFactory.getLogger("SMPSilkSpawner");

    @Override
    public void onInitialize() {
        ConfigManager.load();
        BlockBreakHandler.register();
        logBanner();
    }

    private static void logBanner() {
        String version = FabricLoader.getInstance()
                .getModContainer(MOD_ID)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
        String minecraftVersion = FabricLoader.getInstance()
                .getModContainer("minecraft")
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");

        LOGGER.info("");
        LOGGER.info("  |_______|                             ");
        LOGGER.info("  | Derex |     SMP Silk Spawner v{}", version);
        LOGGER.info("  |_______|     Running on Fabric - {}", minecraftVersion);
        LOGGER.info("");
    }
}
