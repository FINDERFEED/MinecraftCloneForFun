package com.finderfeed.noise_combiner.layer_combiner;

import com.finderfeed.noise_combiner.ComputationContext;

public abstract class FDNoiseValueCombiner {

    public abstract float combine(ComputationContext computationContext, float x, float y);

}
