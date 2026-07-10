package com.doktorthe2nd.tfmg_aihtb;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(AIHTB.MODID)
public class AIHTB {
    public static final String MODID = "tfmg_aihtb";
    public static final String TFMG_MODID = "tfmg";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AIHTB(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("TFMG_AIHTB common setup");
        event.enqueueWork(() -> {

        });
    }
}
