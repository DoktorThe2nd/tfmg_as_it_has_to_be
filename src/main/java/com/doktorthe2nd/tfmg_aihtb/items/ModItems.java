package com.doktorthe2nd.tfmg_aihtb.items;

import com.doktorthe2nd.tfmg_aihtb.AIHTB;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static void init(IEventBus modEventBus) {
        REGISTRY.register(modEventBus);
    }

    public static Item get(String ns, String path) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ns, path));
    }

    private static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(AIHTB.MODID);

    public static final DeferredItem<Item> ENGINEER_SET = REGISTRY.registerSimpleItem("engineer_set");
}
