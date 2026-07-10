package com.doktorthe2nd.tfmg_aihtb.mixins;

import com.doktorthe2nd.tfmg_aihtb.AIHTB;
import com.doktorthe2nd.tfmg_aihtb.Config;
import com.drmangotea.tfmg.base.lang.TFMGTexts;
import com.drmangotea.tfmg.content.engines.base.AbstractEngineBlockEntity;
import com.drmangotea.tfmg.content.engines.base.EngineGenerator;
import com.drmangotea.tfmg.content.engines.types.AbstractSmallEngineBlockEntity;
import com.drmangotea.tfmg.registry.TFMGDataComponents;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Mixin(AbstractSmallEngineBlockEntity.class)
public abstract class EngineFixes {
    @Shadow(remap = false) public int oil;
    @Shadow(remap = false) public int coolingFluid;
    @Shadow(remap = false) public abstract int engineLength();

    @Inject(method = "getFuelConsumption()I", at = @At("RETURN"), cancellable = true, remap = false)
    private void patchFuelConsumption(CallbackInfoReturnable<Integer> cir) {
        if (!((Object)this instanceof AbstractEngineBlockEntity parent)) return;
        if (!((Object)this instanceof BlockEntity be)) return;

        if (parent.rpm == 0.0f) cir.setReturnValue(0);
        else {
            float oilModifier = this.oil > 0 ? 0.7F : 1.0F;
            float coolingFluidModifier = this.coolingFluid > 0 ? 0.7F : 1.0F;

            double val = Config.FUEL_CONSUMPTION_MULTIPLIER.get() * (12.5F * (1.0F / parent.efficiencyModifier()) * parent.getSpeedEfficiency() * parent.highestSignal / 15.0F * oilModifier * coolingFluidModifier) * (this.engineLength() + 1);
            int int_val = (int)Math.ceil(val);

            if (Config.USE_RANDOM_CONSUMPTION.isTrue()) {
                if (be.getLevel() != null) {
                    long gameTime = be.getLevel().getGameTime();

                    // Превращаем дробь в интервал тиков (например, 0.2F превратится в каждые 5 тиков)
                    int tickInterval = val > 0 ? Math.round(1.0F / (float)val) : 20;
                    if (tickInterval < 1) tickInterval = 1;

                    if (gameTime % tickInterval == 0) {
                        cir.setReturnValue(int_val);
                    } else {
                        cir.setReturnValue(int_val + 1);
                    }
                }
            }
            else cir.setReturnValue(int_val);
        }
    }

    @ModifyVariable(
            method = "insertItem(Lnet/minecraft/world/item/ItemStack;ZLnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Z",
            at = @At("HEAD"),
            argsOnly = true,
            remap = false
    )
    private ItemStack injectSafeComponentCheck(ItemStack itemStack) {
        var oilCanItem = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(AIHTB.TFMG_MODID, "oil_can"));
        var cooling_fluid_bottle = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(AIHTB.TFMG_MODID, "cooling_fluid_bottle"));
        if (itemStack == null || (!itemStack.is(cooling_fluid_bottle) &&
                !itemStack.is(oilCanItem))) return itemStack;
        try {
            var amountComponent = TFMGDataComponents.AMOUNT;
            if (!itemStack.isEmpty() && !itemStack.has(amountComponent)) {
                itemStack.set(amountComponent, 0);
                AIHTB.LOGGER.warn("Fun fact: Your game would've crashed now, but TFMG: AIHTB saved you!");
            }
        } catch (Exception e) {
            return itemStack;
        }

        return itemStack;
    }
}
