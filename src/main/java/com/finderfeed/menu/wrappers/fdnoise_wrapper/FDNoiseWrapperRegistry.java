package com.finderfeed.menu.wrappers.fdnoise_wrapper;

import com.finderfeed.noise_combiner.noise.FDConstantValueNoise;
import com.finderfeed.noise_combiner.noise.FDNoise;
import com.finderfeed.noise_combiner.noise.NoiseRegistry;
import com.finderfeed.noise_combiner.registry.ObjectTypeRegistry;

public class FDNoiseWrapperRegistry {

    public static final ObjectTypeRegistry<NoiseWrapperType<?,?>, FDNoiseWrapper<?,?>> NOISE_WRAPPERS = new ObjectTypeRegistry<>();

    public static final NoiseWrapperType<FDConstantValueNoiseWrapper, FDConstantValueNoise> CONSTANT_VALUE_NOISE_WRAPPER = NOISE_WRAPPERS.register(new NoiseWrapperType<>(
            "constant_value_noise_wrapper", NoiseRegistry.CONSTANT_VALUE, FDConstantValueNoiseWrapper::new
    ));

}
