package com.finderfeed.noise_combiner.noise;

import com.finderfeed.noise_combiner.registry.ObjectTypeRegistry;
import com.finderfeed.noise_combiner.registry.SimpleFactoryObjectType;

import java.util.function.Supplier;

public class NoiseRegistry {

    public static final ObjectTypeRegistry<SimpleFactoryObjectType<FDNoise>, FDNoise> NOISE_REGISTRY = new ObjectTypeRegistry<>();

    public static final SimpleFactoryObjectType<FDConstantValueNoise> CONSTANT_VALUE = NOISE_REGISTRY.register(new SimpleFactoryObjectType<>("constant_value", FDConstantValueNoise::new));
    public static final SimpleFactoryObjectType<FDPerlinNoise> PERLIN_NOISE = NOISE_REGISTRY.register(new SimpleFactoryObjectType<>("perlin_noise", FDPerlinNoise::new));

}
