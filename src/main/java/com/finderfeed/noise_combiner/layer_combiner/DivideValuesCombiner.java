package com.finderfeed.noise_combiner.layer_combiner;

import com.finderfeed.noise_combiner.ComputationContext;

public class DivideValuesCombiner extends FDNoiseValueCombiner {

    @Override
    public float combine(ComputationContext computationContext, float x, float y) {
        if (y == 0){
            return 0;
        }
        return x / y;
    }

}
