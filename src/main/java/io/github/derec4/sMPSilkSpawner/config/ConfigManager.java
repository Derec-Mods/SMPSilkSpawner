package io.github.derec4.sMPSilkSpawner.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConfigManager {

    private static final double DEFAULT_DROP_CHANCE = 0.5;
    private static final boolean DEFAULT_DROP_AS_ITEM = true;
    private static final int DEFAULT_DURABILITY_DAMAGE = 1024;
    private static final double DEFAULT_PLAYER_DAMAGE_PERCENT = 50.0;

    private static final double DEFAULT_CHANCE_SMALL = 0.70;
    private static final double DEFAULT_CHANCE_LARGE = 0.40;
    private static final double DEFAULT_CHANCE_MASSIVE = 0.10;

    private static final float DEFAULT_POWER_SMALL = 2.0f;
    private static final float DEFAULT_POWER_LARGE = 4.0f;
    private static final float DEFAULT_POWER_MASSIVE = 6.0f;

    private static final boolean DEFAULT_REQUIRE_ADJACENT = true;
    private static final boolean DEFAULT_REQUIRE_SAME_MOB = true;
    private static final int DEFAULT_MAX_CLUSTER = -1;

    private static double dropChance = DEFAULT_DROP_CHANCE;
    private static boolean dropAsItem = DEFAULT_DROP_AS_ITEM;
    private static int durabilityDamage = DEFAULT_DURABILITY_DAMAGE;
    private static double playerDamagePercent = DEFAULT_PLAYER_DAMAGE_PERCENT;

    private static double chanceSmall = DEFAULT_CHANCE_SMALL;
    private static double chanceLarge = DEFAULT_CHANCE_LARGE;
    private static double chanceMassive = DEFAULT_CHANCE_MASSIVE;

    private static float powerSmall = DEFAULT_POWER_SMALL;
    private static float powerLarge = DEFAULT_POWER_LARGE;
    private static float powerMassive = DEFAULT_POWER_MASSIVE;

    private static boolean requireAdjacent = DEFAULT_REQUIRE_ADJACENT;
    private static boolean requireSameMob = DEFAULT_REQUIRE_SAME_MOB;
    private static int maxCluster = DEFAULT_MAX_CLUSTER;

    private ConfigManager() {
    }

    public static void load(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        FileConfiguration config = plugin.getConfig();

        dropChance = clampChance(config.getDouble("break.drop-chance", DEFAULT_DROP_CHANCE), DEFAULT_DROP_CHANCE, plugin, "break.drop-chance");
        dropAsItem = config.getBoolean("break.drop-as-item", DEFAULT_DROP_AS_ITEM);
        durabilityDamage = clampNonNegativeInt(config.getInt("break.durability-damage", DEFAULT_DURABILITY_DAMAGE), DEFAULT_DURABILITY_DAMAGE, plugin, "break.durability-damage");
        playerDamagePercent = clampPercent(config.getDouble("break.player-damage-percent", DEFAULT_PLAYER_DAMAGE_PERCENT), DEFAULT_PLAYER_DAMAGE_PERCENT, plugin, "break.player-damage-percent");

        chanceSmall = clampChance(config.getDouble("break.explosions.chance-small", DEFAULT_CHANCE_SMALL), DEFAULT_CHANCE_SMALL, plugin, "break.explosions.chance-small");
        chanceLarge = clampChance(config.getDouble("break.explosions.chance-large", DEFAULT_CHANCE_LARGE), DEFAULT_CHANCE_LARGE, plugin, "break.explosions.chance-large");
        chanceMassive = clampChance(config.getDouble("break.explosions.chance-massive", DEFAULT_CHANCE_MASSIVE), DEFAULT_CHANCE_MASSIVE, plugin, "break.explosions.chance-massive");

        powerSmall = clampPower(config.getDouble("break.explosions.power-small", DEFAULT_POWER_SMALL), DEFAULT_POWER_SMALL, plugin, "break.explosions.power-small");
        powerLarge = clampPower(config.getDouble("break.explosions.power-large", DEFAULT_POWER_LARGE), DEFAULT_POWER_LARGE, plugin, "break.explosions.power-large");
        powerMassive = clampPower(config.getDouble("break.explosions.power-massive", DEFAULT_POWER_MASSIVE), DEFAULT_POWER_MASSIVE, plugin, "break.explosions.power-massive");

        requireAdjacent = config.getBoolean("place.require-adjacent", DEFAULT_REQUIRE_ADJACENT);
        requireSameMob = config.getBoolean("place.require-same-mob", DEFAULT_REQUIRE_SAME_MOB);
        maxCluster = clampMaxCluster(config.getInt("place.max-cluster", DEFAULT_MAX_CLUSTER), plugin);

        plugin.getLogger().info("Loaded config.yml");
    }

    public static double getDropChance() {
        return dropChance;
    }

    public static boolean isDropAsItem() {
        return dropAsItem;
    }

    public static int getDurabilityDamage() {
        return durabilityDamage;
    }

    public static double getPlayerDamagePercent() {
        return playerDamagePercent;
    }

    public static double getChanceSmall() {
        return chanceSmall;
    }

    public static double getChanceLarge() {
        return chanceLarge;
    }

    public static double getChanceMassive() {
        return chanceMassive;
    }

    public static float getPowerSmall() {
        return powerSmall;
    }

    public static float getPowerLarge() {
        return powerLarge;
    }

    public static float getPowerMassive() {
        return powerMassive;
    }

    public static boolean isRequireAdjacent() {
        return requireAdjacent;
    }

    public static boolean isRequireSameMob() {
        return requireSameMob;
    }

    public static int getMaxCluster() {
        return maxCluster;
    }

    private static double clampChance(double value, double fallback, JavaPlugin plugin, String path) {
        if (value < 0.0 || value > 1.0) {
            plugin.getLogger().warning("Invalid " + path + " (" + value + "); using default " + fallback);
            return fallback;
        }
        return value;
    }

    private static int clampNonNegativeInt(int value, int fallback, JavaPlugin plugin, String path) {
        if (value < 0) {
            plugin.getLogger().warning("Invalid " + path + " (" + value + "); using default " + fallback);
            return fallback;
        }
        return value;
    }

    private static double clampPercent(double value, double fallback, JavaPlugin plugin, String path) {
        if (value < 0.0 || value > 100.0 || Double.isNaN(value) || Double.isInfinite(value)) {
            plugin.getLogger().warning("Invalid " + path + " (" + value + "); using default " + fallback);
            return fallback;
        }
        return value;
    }

    private static int clampMaxCluster(int value, JavaPlugin plugin) {
        if (value < -1) {
            plugin.getLogger().warning("Invalid place.max-cluster (" + value + "); using default " + DEFAULT_MAX_CLUSTER);
            return DEFAULT_MAX_CLUSTER;
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
