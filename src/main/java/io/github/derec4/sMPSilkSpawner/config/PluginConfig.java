package io.github.derec4.sMPSilkSpawner.config;

import io.github.derec4.sMPSilkSpawner.listener.BlockBreakListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class PluginConfig {

    private static final double DEFAULT_DROP_CHANCE = 0.5;

    private static final double DEFAULT_CHANCE_SMALL = 0.70;
    private static final double DEFAULT_CHANCE_LARGE = 0.40;
    private static final double DEFAULT_CHANCE_MASSIVE = 0.10;

    private static final float DEFAULT_POWER_SMALL = 2.0f;
    private static final float DEFAULT_POWER_LARGE = 4.0f;
    private static final float DEFAULT_POWER_MASSIVE = 6.0f;

    private static double dropChance = DEFAULT_DROP_CHANCE;

    private static double chanceSmall = DEFAULT_CHANCE_SMALL;
    private static double chanceLarge = DEFAULT_CHANCE_LARGE;
    private static double chanceMassive = DEFAULT_CHANCE_MASSIVE;

    private static float powerSmall = DEFAULT_POWER_SMALL;
    private static float powerLarge = DEFAULT_POWER_LARGE;
    private static float powerMassive = DEFAULT_POWER_MASSIVE;

    private PluginConfig() {
    }

    public static void load(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        FileConfiguration config = plugin.getConfig();

        dropChance = clampChance(config.getDouble("break.drop-chance", DEFAULT_DROP_CHANCE), DEFAULT_DROP_CHANCE, plugin, "break.drop-chance");
        BlockBreakListener.setSpawnerDropChance(dropChance);

        chanceSmall = clampChance(config.getDouble("break.explosions.chance-small", DEFAULT_CHANCE_SMALL), DEFAULT_CHANCE_SMALL, plugin, "break.explosions.chance-small");
        chanceLarge = clampChance(config.getDouble("break.explosions.chance-large", DEFAULT_CHANCE_LARGE), DEFAULT_CHANCE_LARGE, plugin, "break.explosions.chance-large");
        chanceMassive = clampChance(config.getDouble("break.explosions.chance-massive", DEFAULT_CHANCE_MASSIVE), DEFAULT_CHANCE_MASSIVE, plugin, "break.explosions.chance-massive");

        powerSmall = clampPower(config.getDouble("break.explosions.power-small", DEFAULT_POWER_SMALL), DEFAULT_POWER_SMALL, plugin, "break.explosions.power-small");
        powerLarge = clampPower(config.getDouble("break.explosions.power-large", DEFAULT_POWER_LARGE), DEFAULT_POWER_LARGE, plugin, "break.explosions.power-large");
        powerMassive = clampPower(config.getDouble("break.explosions.power-massive", DEFAULT_POWER_MASSIVE), DEFAULT_POWER_MASSIVE, plugin, "break.explosions.power-massive");

        plugin.getLogger().info("Loaded config.yml");
    }

    static double getChanceSmall() {
        return chanceSmall;
    }

    static double getChanceLarge() {
        return chanceLarge;
    }

    static double getChanceMassive() {
        return chanceMassive;
    }

    static float getPowerSmall() {
        return powerSmall;
    }

    static float getPowerLarge() {
        return powerLarge;
    }

    static float getPowerMassive() {
        return powerMassive;
    }

    private static double clampChance(double value, double fallback, JavaPlugin plugin, String path) {
        if (value < 0.0 || value > 1.0) {
            plugin.getLogger().warning("Invalid " + path + " (" + value + "); using default " + fallback);
            return fallback;
        }
        return value;
    }

    private static float clampPower(double value, float fallback, JavaPlugin plugin, String path) {
        if (value <= 0.0 || Double.isNaN(value) || Double.isInfinite(value)) {
            plugin.getLogger().warning("Invalid " + path + " (" + value + "); using default " + fallback);
            return fallback;
        }
        return (float) value;
    }
}
