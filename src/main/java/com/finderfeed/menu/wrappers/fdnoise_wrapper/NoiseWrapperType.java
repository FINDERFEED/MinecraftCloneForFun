package com.finderfeed.menu.wrappers.fdnoise_wrapper;

import com.finderfeed.noise_combiner.noise.FDNoise;
import com.finderfeed.noise_combiner.registry.ObjectType;

import java.util.function.Function;

public class NoiseWrapperType<T extends NoiseWrapper<T, D>, D extends FDNoise<D>> extends ObjectType<T> {

    private Function<D, NoiseWrapper<T, D>> factory;
    private ObjectType<D> noiseObjectType;

    public NoiseWrapperType(String registryId, ObjectType<D> noiseObjectType, Function<D, NoiseWrapper<T, D>> noiseWrapperFactory) {
        super(registryId);
        this.factory = noiseWrapperFactory;
        this.noiseObjectType = noiseObjectType;
    }

    public NoiseWrapper<T, D> generateWrapper(D noise){
        return factory.apply(noise);
    }

    public ObjectType<D> getNoiseObjectType() {
        return noiseObjectType;
    }

}
