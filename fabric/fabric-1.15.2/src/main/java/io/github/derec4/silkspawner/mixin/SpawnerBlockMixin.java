package io.github.derec4.silkspawner.mixin;

import io.github.derec4.silkspawner.listener.BlockBreakHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpawnerBlock.class)
public class SpawnerBlockMixin {
    @Inject(method = "spawnAfterBreak", at = @At("HEAD"), cancellable = true)
    private void skipXp(BlockState state, Level level, BlockPos pos, ItemStack tool, CallbackInfo ci) {
        if (BlockBreakHandler.skipXp) {
            BlockBreakHandler.skipXp = false;
            ci.cancel();
        }
    }
}
