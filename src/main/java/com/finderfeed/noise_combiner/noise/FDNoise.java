package com.finderfeed.noise_combiner.noise;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.registry.ObjectType;

public abstract class FDNoise<T extends FDNoise<T>> {

    public abstract float computeNoiseValue(ComputationContext computationContext);

    public abstract ObjectType<T> getObjectType();

}
