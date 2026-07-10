package com.doktorthe2nd.tfmg_aihtb;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<Double> FUEL_CONSUMPTION_MULTIPLIER = BUILDER
            .defineInRange("fuelConsumptionMultiplier", 1.0, 0.0, 1000.0);

    public static final ModConfigSpec.BooleanValue USE_RANDOM_CONSUMPTION = BUILDER
            .comment("Smoother way to calculate fuel consumption (tail after rounding value becomes chance to add 1 to value, makes it more fair)")
            .define("useRandomConsumption", false);

    static final ModConfigSpec SPEC = BUILDER.build();
}
