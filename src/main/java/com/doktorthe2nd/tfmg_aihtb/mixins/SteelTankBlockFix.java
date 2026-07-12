package com.doktorthe2nd.tfmg_aihtb.mixins;

import com.doktorthe2nd.tfmg_aihtb.AIHTB;
import com.drmangotea.tfmg.content.decoration.tanks.steel.SteelTankBlock;
import com.drmangotea.tfmg.content.decoration.tanks.steel.SteelTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// MAYBE this bug does not even exist, I couldn't replicate it

@Mixin(SteelTankBlock.class)
public class SteelTankBlockFix {
    @Inject(method = "updateTowerState", at=@At("HEAD"), cancellable = true, remap = false)
    private static void patchNoCheckCrash(Level pLevel, BlockPos tankPos, boolean assemble, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        BlockState tankState = pLevel.getBlockState(tankPos);
        if (!(tankState.getBlock() instanceof SteelTankBlock tank))
            return;
        SteelTankBlockEntity tankBE = tank.getBlockEntity(pLevel, tankPos);
        if (tankBE == null)
            return;
        if (tankBE.getControllerBE() == null) {
            AIHTB.LOGGER.warn("TFMG: As It Has To Be prevented crash!");
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
