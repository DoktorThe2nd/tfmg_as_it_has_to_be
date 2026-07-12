package com.doktorthe2nd.tfmg_aihtb;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue FUEL_CONSUMPTION_MULTIPLIER = BUILDER
            .defineInRange("fuelConsumptionMultiplier", 1.0, 0.0, 1000.0);

    public static final ModConfigSpec.BooleanValue SMART_FUEL_CONSUMPTION = BUILDER
            .comment("Smoother way to calculate fuel consumption")
            .define("smartFuelConsumption", true);

    public static final ModConfigSpec.BooleanValue DISABLE_ENGINE_PIPE_UPGRADE = BUILDER
            .comment("Somewhy this feature is not working when tank near engine is full, and i couldn't find solution, so i disabled it\n(Does not change its functionality, just disables ability to place industrial pipe in engine)")
            .define("disableEnginePipeUpgrade", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}
