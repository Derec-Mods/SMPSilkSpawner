package io.github.derec4.silkspawner.mixin;

import io.github.derec4.silkspawner.listener.BlockPlaceHandler;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void restrictPlace(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (context.getItemInHand().getItem() == Items.SPAWNER && BlockPlaceHandler.shouldCancel(context)) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }

    @Inject(method = "place", at = @At("RETURN"))
    private void playPlaceSound(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue() != null && cir.getReturnValue().consumesAction()) {
            BlockPlaceHandler.onPlaced(context);
        }
    }
}

