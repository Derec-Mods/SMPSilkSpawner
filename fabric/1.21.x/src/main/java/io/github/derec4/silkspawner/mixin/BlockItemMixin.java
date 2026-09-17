package io.github.derec4.silkspawner.mixin;

import io.github.derec4.silkspawner.listener.BlockPlaceHandler;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At("HEAD"), cancellable = true)
    private void smpSilkSpawner$restrictSpawnerPlacement(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (!context.getStack().isOf(Items.SPAWNER)) {
            return;
        }
        if (BlockPlaceHandler.shouldCancel(context)) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At("RETURN"))
    private void smpSilkSpawner$playSpawnerPlaceSound(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
        ActionResult result = cir.getReturnValue();
        if (result != null && result.isAccepted()) {
            BlockPlaceHandler.onPlaced(context);
        }
    }
}
