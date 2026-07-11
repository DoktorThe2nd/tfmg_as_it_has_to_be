package com.doktorthe2nd.tfmg_aihtb.mixins;

import com.doktorthe2nd.tfmg_aihtb.AIHTB;
import com.doktorthe2nd.tfmg_aihtb.Config;
import com.doktorthe2nd.tfmg_aihtb.IFuelConsumptionFormula;
import com.drmangotea.tfmg.content.engines.base.AbstractEngineBlockEntity;
import com.drmangotea.tfmg.content.engines.types.AbstractSmallEngineBlockEntity;
import com.drmangotea.tfmg.registry.TFMGDataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSmallEngineBlockEntity.class)
@Implements(@Interface(iface = IFuelConsumptionFormula.class, prefix = "fcf$"))
public abstract class EngineFixes {
    @Shadow(remap = false) public int oil;
    @Shadow(remap = false) public int coolingFluid;
    @Shadow(remap = false) public abstract int engineLength();

    // original formula * FUEL_CONSUMPTION_MULTIPLIER
    @Unique
    public float fcf$fuelConsumptionFormula() {
        if (!((Object)this instanceof AbstractEngineBlockEntity parent)) return 0.0F;
        if (parent.rpm == 0.0f) return 0.0F;
        float oilModifier = this.oil > 0 ? 0.7F : 1.0F;
        float coolingFluidModifier = this.coolingFluid > 0 ? 0.7F : 1.0F;
        return Config.FUEL_CONSUMPTION_MULTIPLIER.get().floatValue() * (12.5F * (1.0F / parent.efficiencyModifier()) * parent.getSpeedEfficiency() * parent.highestSignal / 15.0F * oilModifier * coolingFluidModifier) * (this.engineLength() + 1);
    }

    @Inject(method = "getFuelConsumption()I", at = @At("RETURN"), cancellable = true, remap = false)
    private void patchFuelConsumption(CallbackInfoReturnable<Integer> cir) {
        if (!((Object)this instanceof BlockEntity be)) return;

        float val = fcf$fuelConsumptionFormula();

        if (Config.SMART_FUEL_CONSUMPTION.isTrue() && val > 0.0f && be.getLevel() != null) {
            long gameTime = be.getLevel().getGameTime();
            float tail = val - (int)val;
            if (tail > 0.0f && gameTime % (1.f/tail) < 1.f) cir.setReturnValue((int)val + 1);
            else cir.setReturnValue((int)val);
        }
        else cir.setReturnValue((int)Math.ceil(val));
    }

    @ModifyVariable(
            method = "insertItem(Lnet/minecraft/world/item/ItemStack;ZLnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Z",
            at = @At("HEAD"),
            argsOnly = true,
            remap = false
    )
    private ItemStack patchComponentCheck(ItemStack itemStack) {
        var oilCanItem = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(AIHTB.TFMG_MODID, "oil_can"));
        var cooling_fluid_bottle = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(AIHTB.TFMG_MODID, "cooling_fluid_bottle"));
        if (itemStack == null || (!itemStack.is(cooling_fluid_bottle) &&
                !itemStack.is(oilCanItem))) return itemStack;
        try {
            var amountComponent = TFMGDataComponents.AMOUNT;
            if (!itemStack.isEmpty() && !itemStack.has(amountComponent)) {
                itemStack.set(amountComponent, 0);
                // game would have crashed, if not this!
            }
        } catch (Exception e) {
            return itemStack;
        }

        return itemStack;
    }
}
