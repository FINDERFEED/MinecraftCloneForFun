package com.finderfeed.noise_combiner.layer_combiner.instances;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.layer_combiner.FDNoiseValueCombiner;
import com.finderfeed.noise_combiner.layer_combiner.NoiseValueCombinerRegistry;
import com.finderfeed.noise_combiner.registry.ObjectType;

public class DivideValuesCombiner extends FDNoiseValueCombiner<DivideValuesCombiner> {

    @Override
    public float combine(ComputationContext computationContext, float x, float y) {
        if (y == 0){
            return 0;
        }
        return x / y;
    }

    @Override
    public ObjectType<DivideValuesCombiner> getType() {
        return NoiseValueCombinerRegistry.DIVIDE;
    }

}
