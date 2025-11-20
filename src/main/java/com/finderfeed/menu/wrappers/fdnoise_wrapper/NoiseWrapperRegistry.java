package com.finderfeed.menu.wrappers.fdnoise_wrapper;

import com.finderfeed.menu.wrappers.fdnoise_wrapper.instances.ConstantValueNoiseWrapper;
import com.finderfeed.menu.wrappers.fdnoise_wrapper.instances.PerlinNoiseWrapper;
import com.finderfeed.noise_combiner.noise.instances.FDConstantValueNoise;
import com.finderfeed.noise_combiner.noise.instances.FDPerlinNoise;
import com.finderfeed.noise_combiner.noise.NoiseRegistry;
import com.finderfeed.noise_combiner.registry.ObjectTypeRegistry;

public class NoiseWrapperRegistry {

    public static final ObjectTypeRegistry<NoiseWrapperType<?,?>, NoiseWrapper<?,?>> NOISE_WRAPPERS = new ObjectTypeRegistry<>();

    public static final NoiseWrapperType<ConstantValueNoiseWrapper, FDConstantValueNoise> CONSTANT_VALUE_NOISE_WRAPPER = NOISE_WRAPPERS.register(new NoiseWrapperType<>(
            "constant_value_noise_wrapper", NoiseRegistry.CONSTANT_VALUE, ConstantValueNoiseWrapper::new
    ));

    public static final NoiseWrapperType<PerlinNoiseWrapper, FDPerlinNoise> PERLIN_NOISE_WRAPPER = NOISE_WRAPPERS.register(new NoiseWrapperType<>(
            "perlin_noise_wrapper", NoiseRegistry.PERLIN_NOISE, PerlinNoiseWrapper::new
    ));

}
