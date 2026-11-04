package io.github.derec4.silkspawner.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.derec4.silkspawner.SMPSilkSpawner;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigManager {
    public static final String FILE_NAME = "smp_silk_spawner.json";

    private static double dropChance = 0.5;
    private static boolean dropAsItem = true;
    private static int durabilityDamage = 1024;
    private static double playerDamagePercent = 50.0;

    private static double chanceSmall = 0.70;
    private static double chanceLarge = 0.40;
    private static double chanceMassive = 0.10;

    private static float powerSmall = 2.0f;
    private static float powerLarge = 4.0f;
    private static float powerMassive = 6.0f;

    private static boolean requireAdjacent = true;
    private static boolean requireSameMob = true;
    private static int maxCluster = -1;

    public static void load(File configDir) {
        File configFile = new File(configDir, FILE_NAME);
        if (!configFile.exists()) {
            try (InputStream in = ConfigManager.class.getResourceAsStream("/" + FILE_NAME)) {
                if (in != null) {
                    Files.copy(in, configFile.toPath());
                }
            } catch (Exception e) {
                SMPSilkSpawner.LOGGER.warn("Could not copy default config: " + e.getMessage());
            }
        }

        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
                if (root.has("break")) {
                    JsonObject breakObj = root.getAsJsonObject("break");
                    if (breakObj.has("drop-chance")) dropChance = breakObj.get("drop-chance").getAsDouble();
                    if (breakObj.has("drop-as-item")) dropAsItem = breakObj.get("drop-as-item").getAsBoolean();
                    if (breakObj.has("durability-damage")) durabilityDamage = breakObj.get("durability-damage").getAsInt();
                    if (breakObj.has("player-damage-percent")) playerDamagePercent = breakObj.get("player-damage-percent").getAsDouble();

                    if (breakObj.has("explosions")) {
                        JsonObject expObj = breakObj.getAsJsonObject("explosions");
                        if (expObj.has("chance-small")) chanceSmall = expObj.get("chance-small").getAsDouble();
                        if (expObj.has("chance-large")) chanceLarge = expObj.get("chance-large").getAsDouble();
                        if (expObj.has("chance-massive")) chanceMassive = expObj.get("chance-massive").getAsDouble();
                        if (expObj.has("power-small")) powerSmall = expObj.get("power-small").getAsFloat();
                        if (expObj.has("power-large")) powerLarge = expObj.get("power-large").getAsFloat();
                        if (expObj.has("power-massive")) powerMassive = expObj.get("power-massive").getAsFloat();
                    }
                }
                if (root.has("place")) {
                    JsonObject placeObj = root.getAsJsonObject("place");
                    if (placeObj.has("require-adjacent")) requireAdjacent = placeObj.get("require-adjacent").getAsBoolean();
                    if (placeObj.has("require-same-mob")) requireSameMob = placeObj.get("require-same-mob").getAsBoolean();
                    if (placeObj.has("max-cluster")) maxCluster = placeObj.get("max-cluster").getAsInt();
                }
            } catch (Exception e) {
                SMPSilkSpawner.LOGGER.warn("Failed to load config, using defaults: " + e.getMessage());
            }
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
}

