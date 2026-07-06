package io.github.derec4.sMPSilkSpawner.util;

import org.bukkit.Location;
import org.bukkit.World;

public final class ExplosionUtils {

    private static final double CHANCE_SMALL = 0.70;
    private static final double CHANCE_LARGE = 0.40;
    private static final double CHANCE_MASSIVE = 0.10;

    private static final float POWER_SMALL = 2.0f;
    private static final float POWER_LARGE = 4.0f;
    private static final float POWER_MASSIVE = 6.0f;

    public enum SpawnerExplosionSize {
        NONE,
        SMALL,
        LARGE,
        MASSIVE
    }

    private ExplosionUtils() {
    }

    public static SpawnerExplosionSize rollExplosionSize() {
        double roll = Math.random();
        if (roll < CHANCE_MASSIVE) {
            return SpawnerExplosionSize.MASSIVE;
        }
        if (roll < CHANCE_LARGE) {
            return SpawnerExplosionSize.LARGE;
        }
        if (roll < CHANCE_SMALL) {
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
            case SMALL -> POWER_SMALL;
            case LARGE -> POWER_LARGE;
            case MASSIVE -> POWER_MASSIVE;
            default -> 0.0f;
        };

        world.createExplosion(center, power, false, true);
    }
}
