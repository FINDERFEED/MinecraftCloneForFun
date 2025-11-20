package com.finderfeed.menu.wrappers.layer_combiner_wrappers;

import com.finderfeed.noise_combiner.layer_combiner.FDNoiseValueCombiner;
import com.finderfeed.noise_combiner.registry.ObjectType;

import java.util.function.Function;

public class LayerCombinerWrapperType<D extends LayerCombinerWrapper<D,T>, T extends FDNoiseValueCombiner<T>> extends ObjectType<D> {

    private ObjectType<T> objectType;

    private Function<T,D> factory;

    public LayerCombinerWrapperType(String registryId, ObjectType<T> noiseCombinerType, Function<T,D> factory) {
        super(registryId);
        this.objectType = noiseCombinerType;
        this.factory = factory;
    }

    public Function<T, D> getFactory() {
        return factory;
    }

    public ObjectType<T> getValueCombinerType() {
        return objectType;
    }

}
