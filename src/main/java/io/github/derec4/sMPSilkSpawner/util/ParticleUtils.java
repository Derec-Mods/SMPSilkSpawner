package io.github.derec4.sMPSilkSpawner.util;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;

public class ParticleUtils {
    public static void playFailedParticles(World world, Location location) {
        world.playSound(location, Sound.BLOCK_SOUL_SAND_HIT, 1.0f, 1.0f);
        world.spawnParticle(Particle.SMALL_FLAME, location, 5);
    }
}

