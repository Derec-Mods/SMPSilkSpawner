package io.github.derec4.sMPSilkSpawner.util;

import org.bukkit.Location;
import org.bukkit.World;

public final class ExplosionUtils {

    private static double chanceSmall = 0.70;
    private static double chanceLarge = 0.40;
    private static double chanceMassive = 0.10;

    private static float powerSmall = 2.0f;
    private static float powerLarge = 4.0f;
    private static float powerMassive = 6.0f;

    public enum SpawnerExplosionSize {
        NONE,
        SMALL,
        LARGE,
        MASSIVE
    }

    private ExplosionUtils() {
    }

    public static void setExplosionSettings(
            double smallChance,
            double largeChance,
            double massiveChance,
            float smallPower,
            float largePower,
            float massivePower
    ) {
        chanceSmall = smallChance;
        chanceLarge = largeChance;
        chanceMassive = massiveChance;
        powerSmall = smallPower;
        powerLarge = largePower;
        powerMassive = massivePower;
    }

    public static SpawnerExplosionSize rollExplosionSize() {
        double roll = Math.random();
        if (roll < chanceMassive) {
            return SpawnerExplosionSize.MASSIVE;
        }
        if (roll < chanceLarge) {
            return SpawnerExplosionSize.LARGE;
        }
        if (roll < chanceSmall) {
            return SpawnerExplosionSize.SMALL;
        }
        return SpawnerExplosionSize.NONE;
    }

    public static void playExplosion(World world, Location blockLocation, SpawnerExplosionSize size) {
        if (size == SpawnerExplosionSize.NONE) {
            return;
        }

        Location center = blockLocation.clone().add(0.5, 0.5, 0.5);
        float power = switch (size) {
            case SMALL -> powerSmall;
            case LARGE -> powerLarge;
            case MASSIVE -> powerMassive;
            default -> 0.0f;
        };

        world.createExplosion(center, power, false, true);
    }
}
