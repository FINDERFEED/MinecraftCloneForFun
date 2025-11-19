package com.finderfeed.noise_combiner.noise;

import com.finderfeed.noise_combiner.FactoryRegistry;

import java.util.function.Supplier;

public class NoiseRegistry {

    public static final FactoryRegistry<FDNoise> NOISE_REGISTRY = new FactoryRegistry<>();

    public static final Supplier<FDConstantValueNoise> CONSTANT_VALUE = NOISE_REGISTRY.register("constant_value", FDConstantValueNoise::new);
    public static final Supplier<FDPerlinNoise> PERLIN_NOISE = NOISE_REGISTRY.register("perlin_noise", FDPerlinNoise::new);

}
