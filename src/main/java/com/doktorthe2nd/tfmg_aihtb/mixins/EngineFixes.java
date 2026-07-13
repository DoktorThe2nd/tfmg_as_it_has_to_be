package com.doktorthe2nd.tfmg_aihtb.mixins;

import com.doktorthe2nd.tfmg_aihtb.AIHTB;
import com.doktorthe2nd.tfmg_aihtb.Config;
import com.doktorthe2nd.tfmg_aihtb.IFuelConsumptionFormula;
import com.doktorthe2nd.tfmg_aihtb.items.ModItems;
import com.drmangotea.tfmg.content.engines.base.AbstractEngineBlockEntity;
import com.drmangotea.tfmg.content.engines.base.EngineComponentsInventory;
import com.drmangotea.tfmg.content.engines.types.AbstractSmallEngineBlockEntity;
import com.drmangotea.tfmg.registry.TFMGDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSmallEngineBlockEntity.class)
@Implements(@Interface(iface = IFuelConsumptionFormula.class, prefix = "fcf$"))
public abstract class EngineFixes {
    @Shadow(remap = false) public int oil;
    @Shadow(remap = false) public int coolingFluid;
    @Shadow(remap = false) public abstract int engineLength();
    @Shadow(remap = false) public abstract AbstractSmallEngineBlockEntity getControllerBE();

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

    @Inject(
            method = "insertItem(Lnet/minecraft/world/item/ItemStack;ZLnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void patchInsertItem(ItemStack itemStack, boolean shifting, Player player, InteractionHand hand, CallbackInfoReturnable<Boolean> cir) {
        if (itemStack == null) return;

        if (itemStack.is(ModItems.ENGINEER_SET)) {
            var control = this.getControllerBE();
            if (control == null) return;
            if (!control.componentsInventory.isEmpty()) {
                player.displayClientMessage(Component.translatable("tfmg_aihtb.engine_is_not_empty"), true);
                return;
            }
            while (!control.hasAllComponents()) {
                if (control.nextComponent().isEmpty() || control.nextComponent().getItems().length == 0) return;
                control.componentsInventory.insertItem(control.nextComponent().getItems()[0]);
            }
            itemStack.shrink(1);
        }

        if (itemStack.is(ModItems.get(AIHTB.TFMG_MODID, "industrial_pipe")) && Config.DISABLE_ENGINE_PIPE_UPGRADE.isTrue()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
        if (itemStack.is(ModItems.get(AIHTB.TFMG_MODID, "cooling_fluid_bottle"))
                || itemStack.is(ModItems.get(AIHTB.TFMG_MODID, "oil_can"))) {
            try {
                var amountComponent = TFMGDataComponents.AMOUNT;
                if (!itemStack.isEmpty() && !itemStack.has(amountComponent)) {
                    itemStack.set(amountComponent, 0);
                    AIHTB.LOGGER.warn("TFMG: As It Has To Be prevented crash!");
                }
            } catch (Exception e) {
                AIHTB.LOGGER.error("Exception: {}", e.getMessage());
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }
}
