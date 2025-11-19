package com.finderfeed.noise_combiner.noise;

import com.finderfeed.noise_combiner.ComputationContext;

public class FDConstantValueNoise extends FDNoise{

    public float constantValue;

    public FDConstantValueNoise(){
        this.constantValue = 1;
    }

    @Override
    public float computeNoiseValue(ComputationContext computationContext) {
        return constantValue;
    }

}
