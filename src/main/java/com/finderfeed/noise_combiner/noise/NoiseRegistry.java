package com.finderfeed.noise_combiner.noise;

import com.finderfeed.noise_combiner.noise.instances.FDConstantValueNoise;
import com.finderfeed.noise_combiner.noise.instances.FDPerlinNoise;
import com.finderfeed.noise_combiner.noise.instances.FDRidgedNoise;
import com.finderfeed.noise_combiner.registry.ObjectTypeRegistry;
import com.finderfeed.noise_combiner.registry.SimpleFactoryObjectType;

public class NoiseRegistry {

    public static final ObjectTypeRegistry<SimpleFactoryObjectType<FDNoise>, FDNoise> NOISE_REGISTRY = new ObjectTypeRegistry<>();

    public static final SimpleFactoryObjectType<FDConstantValueNoise> CONSTANT_VALUE = NOISE_REGISTRY.register(new SimpleFactoryObjectType<>("constant_value", FDConstantValueNoise::new));
    public static final SimpleFactoryObjectType<FDPerlinNoise> PERLIN_NOISE = NOISE_REGISTRY.register(new SimpleFactoryObjectType<>("perlin_noise", FDPerlinNoise::new));
    public static final SimpleFactoryObjectType<FDRidgedNoise> RIDGED_NOISE = NOISE_REGISTRY.register(new SimpleFactoryObjectType<>("ridged_noise", FDRidgedNoise::new));

}
