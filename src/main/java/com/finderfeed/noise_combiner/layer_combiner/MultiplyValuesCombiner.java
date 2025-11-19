package com.finderfeed.noise_combiner.layer_combiner;

import com.finderfeed.noise_combiner.ComputationContext;

public class MultiplyValuesCombiner extends FDNoiseValueCombiner {

    @Override
    public float combine(ComputationContext computationContext, float x, float y) {
        return x * y;
    }

}
