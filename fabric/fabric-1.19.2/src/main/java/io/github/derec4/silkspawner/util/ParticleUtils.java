package io.github.derec4.silkspawner.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public class ParticleUtils {
    public static void playFailedParticles(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel) level;
        serverLevel.playSound(null, pos, SoundEvents.SOUL_SAND_HIT, SoundSource.BLOCKS, 1.0f, 1.0f);
        serverLevel.sendParticles(ParticleTypes.SMALL_FLAME, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5, 0.2, 0.2, 0.2, 0.0);
    }
}

