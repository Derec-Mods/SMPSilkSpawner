package com.moddername.modname.items;

import com.moddername.modname.modname;
import com.moddername.modname.ModCreativeTab;
import net.minecraft.world.item.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = modname.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, modname.MODID);

    public static final RegistryObject<Item> NORMALITEM = ITEMS.register("normalitem",
            () -> new Item(new Item.Properties().tab(ModCreativeTab.modname)));

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
