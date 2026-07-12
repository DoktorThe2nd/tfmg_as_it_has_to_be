package com.doktorthe2nd.tfmg_aihtb.mixins;

import com.doktorthe2nd.tfmg_aihtb.Config;
import com.doktorthe2nd.tfmg_aihtb.IFuelConsumptionFormula;
import com.drmangotea.tfmg.base.lang.TFMGTexts;
import com.drmangotea.tfmg.content.engines.types.AbstractSmallEngineBlockEntity;
import com.drmangotea.tfmg.content.engines.types.regular_engine.RegularEngineBlockEntity;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RegularEngineBlockEntity.class)
public class RegularEngineFixes {
    @Inject(method="addToGoggleTooltip(Ljava/util/List;Z)Z", at=@At("RETURN"))
    private void patchFuelConsumption(List<Component> tooltip, boolean isPlayerSneaking, CallbackInfoReturnable<Boolean> cir) {
        if (Config.SMART_FUEL_CONSUMPTION.isFalse()) return;
        if (!((Object)this instanceof AbstractSmallEngineBlockEntity smallEngineBlockEntity)) return;
        if (!((Object)smallEngineBlockEntity.getControllerBE() instanceof IFuelConsumptionFormula fcf)) return;
        for (int i = 0; i < tooltip.size(); i++) {
            Component component = tooltip.get(i);
            String text = component.getString();
            if (!text.contains("mB/s")) continue;
            tooltip.set(i, Component.literal("     ").append(TFMGTexts.Engine.fuelConsumption(fcf.fuelConsumptionFormula()).component()));
        }
    }
}
