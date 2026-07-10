package com.doktorthe2nd.tfmg_aihtb;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<Double> FUEL_CONSUMPTION_MULTIPLIER = BUILDER
            .comment("Increases fuel consumption but ensures smoother transitions from the analog signal.")
            .define("fuelConsumptionMultiplier", 2.0);

    public static final ModConfigSpec.BooleanValue USE_RANDOM_CONSUMPTION = BUILDER
            .comment("Smoother way to calculate fuel consumption, won't show up properly in engine stats")
            .define("useRandomConsumption", false);

    static final ModConfigSpec SPEC = BUILDER.build();
}
