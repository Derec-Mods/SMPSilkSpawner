package io.github.derec4.silkspawner.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.derec4.silkspawner.SMPSilkSpawner;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {

    public static final String FILE_NAME = "smp_silk_spawner.json";

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

    public static void load() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        copyDefaultIfMissing(configPath);

        try (Reader reader = Files.newBufferedReader(configPath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            apply(root);
            SMPSilkSpawner.LOGGER.info("Loaded {}", FILE_NAME);
        } catch (Exception exception) {
            SMPSilkSpawner.LOGGER.warn("Failed to load {}; using defaults", FILE_NAME, exception);
            apply(new JsonObject());
        }
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

    private static void copyDefaultIfMissing(Path configPath) {
        if (Files.exists(configPath)) {
            return;
        }

        try (InputStream input = ConfigManager.class.getResourceAsStream("/" + FILE_NAME)) {
            if (input == null) {
                SMPSilkSpawner.LOGGER.warn("Missing default {}", FILE_NAME);
                return;
            }
            Files.createDirectories(configPath.getParent());
            Files.copy(input, configPath);
        } catch (IOException exception) {
            SMPSilkSpawner.LOGGER.warn("Could not write default {}", FILE_NAME, exception);
        }
    }

    private static void apply(JsonObject root) {
        JsonObject breakConfig = object(root, "break");
        dropChance = clampChance(getDouble(breakConfig, "drop-chance", DEFAULT_DROP_CHANCE), DEFAULT_DROP_CHANCE, "break.drop-chance");
        dropAsItem = getBoolean(breakConfig, "drop-as-item", DEFAULT_DROP_AS_ITEM);
        durabilityDamage = clampNonNegativeInt(getInt(breakConfig, "durability-damage", DEFAULT_DURABILITY_DAMAGE), DEFAULT_DURABILITY_DAMAGE, "break.durability-damage");
        playerDamagePercent = clampPercent(getDouble(breakConfig, "player-damage-percent", DEFAULT_PLAYER_DAMAGE_PERCENT), DEFAULT_PLAYER_DAMAGE_PERCENT, "break.player-damage-percent");

        JsonObject explosions = object(breakConfig, "explosions");
        chanceSmall = clampChance(getDouble(explosions, "chance-small", DEFAULT_CHANCE_SMALL), DEFAULT_CHANCE_SMALL, "break.explosions.chance-small");
        chanceLarge = clampChance(getDouble(explosions, "chance-large", DEFAULT_CHANCE_LARGE), DEFAULT_CHANCE_LARGE, "break.explosions.chance-large");
        chanceMassive = clampChance(getDouble(explosions, "chance-massive", DEFAULT_CHANCE_MASSIVE), DEFAULT_CHANCE_MASSIVE, "break.explosions.chance-massive");
        powerSmall = clampPower(getDouble(explosions, "power-small", DEFAULT_POWER_SMALL), DEFAULT_POWER_SMALL, "break.explosions.power-small");
        powerLarge = clampPower(getDouble(explosions, "power-large", DEFAULT_POWER_LARGE), DEFAULT_POWER_LARGE, "break.explosions.power-large");
        powerMassive = clampPower(getDouble(explosions, "power-massive", DEFAULT_POWER_MASSIVE), DEFAULT_POWER_MASSIVE, "break.explosions.power-massive");

        JsonObject placeConfig = object(root, "place");
        requireAdjacent = getBoolean(placeConfig, "require-adjacent", DEFAULT_REQUIRE_ADJACENT);
        requireSameMob = getBoolean(placeConfig, "require-same-mob", DEFAULT_REQUIRE_SAME_MOB);
        maxCluster = clampMaxCluster(getInt(placeConfig, "max-cluster", DEFAULT_MAX_CLUSTER));
    }

    private static JsonObject object(JsonObject parent, String key) {
        if (parent == null || !parent.has(key) || !parent.get(key).isJsonObject()) {
            return new JsonObject();
        }
        return parent.getAsJsonObject(key);
    }

    private static double getDouble(JsonObject object, String key, double fallback) {
        try {
            if (object != null && object.has(key) && object.get(key).isJsonPrimitive()) {
                return object.get(key).getAsDouble();
            }
        } catch (Exception ignored) {
            // fall through to default
        }
        return fallback;
    }

    private static int getInt(JsonObject object, String key, int fallback) {
        try {
            if (object != null && object.has(key) && object.get(key).isJsonPrimitive()) {
                return object.get(key).getAsInt();
            }
        } catch (Exception ignored) {
            // fall through to default
        }
        return fallback;
    }

    private static boolean getBoolean(JsonObject object, String key, boolean fallback) {
        try {
            if (object != null && object.has(key) && object.get(key).isJsonPrimitive()) {
                return object.get(key).getAsBoolean();
            }
        } catch (Exception ignored) {
            // fall through to default
        }
        return fallback;
    }

    private static double clampChance(double value, double fallback, String path) {
        if (value < 0.0 || value > 1.0) {
            SMPSilkSpawner.LOGGER.warn("Invalid {} ({}); using default {}", path, value, fallback);
            return fallback;
        }
        return value;
    }

    private static int clampNonNegativeInt(int value, int fallback, String path) {
        if (value < 0) {
            SMPSilkSpawner.LOGGER.warn("Invalid {} ({}); using default {}", path, value, fallback);
            return fallback;
        }
        return value;
    }

    private static double clampPercent(double value, double fallback, String path) {
        if (value < 0.0 || value > 100.0 || Double.isNaN(value) || Double.isInfinite(value)) {
            SMPSilkSpawner.LOGGER.warn("Invalid {} ({}); using default {}", path, value, fallback);
            return fallback;
        }
        return value;
    }

    private static int clampMaxCluster(int value) {
        if (value < -1) {
            SMPSilkSpawner.LOGGER.warn("Invalid place.max-cluster ({}); using default {}", value, DEFAULT_MAX_CLUSTER);
            return DEFAULT_MAX_CLUSTER;
        }
        return value;
    }

    private static float clampPower(double value, float fallback, String path) {
        if (value <= 0.0 || Double.isNaN(value) || Double.isInfinite(value)) {
            SMPSilkSpawner.LOGGER.warn("Invalid {} ({}); using default {}", path, value, fallback);
            return fallback;
        }
        return (float) value;
    }
}
