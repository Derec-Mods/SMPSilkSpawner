package io.github.derec4.silkspawner.util;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ParticleUtils {
    public static void playFailedParticles(World world, BlockPos pos) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }
        serverWorld.playSound(null, pos, SoundEvents.BLOCK_SOUL_SAND_HIT, SoundCategory.BLOCKS, 1.0f, 1.0f);
        serverWorld.spawnParticles(ParticleTypes.SMALL_FLAME, pos.getX(), pos.getY(), pos.getZ(), 5, 0.0, 0.0, 0.0, 0.0);
    }
}
