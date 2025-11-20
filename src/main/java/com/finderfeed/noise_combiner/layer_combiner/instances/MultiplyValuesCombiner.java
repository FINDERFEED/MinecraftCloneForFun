package com.finderfeed.noise_combiner.layer_combiner.instances;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.layer_combiner.FDNoiseValueCombiner;
import com.finderfeed.noise_combiner.layer_combiner.NoiseValueCombinerRegistry;
import com.finderfeed.noise_combiner.registry.ObjectType;

public class MultiplyValuesCombiner extends FDNoiseValueCombiner<MultiplyValuesCombiner> {

    @Override
    public float combine(ComputationContext computationContext, float x, float y) {
        return x * y;
    }

    @Override
    public ObjectType<MultiplyValuesCombiner> getType() {
        return NoiseValueCombinerRegistry.MULTIPLY;
    }

}
