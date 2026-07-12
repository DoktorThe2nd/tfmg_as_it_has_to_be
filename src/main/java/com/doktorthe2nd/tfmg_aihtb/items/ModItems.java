package com.doktorthe2nd.tfmg_aihtb.items;

import com.doktorthe2nd.tfmg_aihtb.AIHTB;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static void init(IEventBus modEventBus) {
        REGISTRY.register(modEventBus);
    }

    private static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(AIHTB.MODID);

    public static final DeferredItem<Item> ENGINEER_SET = REGISTRY.registerSimpleItem("engineer_set");
}
