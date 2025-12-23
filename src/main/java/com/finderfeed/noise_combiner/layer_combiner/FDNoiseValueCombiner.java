package com.finderfeed.noise_combiner.layer_combiner;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.JsonSerializable;
import com.finderfeed.noise_combiner.registry.ObjectType;

public abstract class FDNoiseValueCombiner<T extends FDNoiseValueCombiner<T>> implements JsonSerializable<T> {

    public abstract float combine(ComputationContext computationContext, float x, float y);

    public abstract ObjectType<T> getType();

}
