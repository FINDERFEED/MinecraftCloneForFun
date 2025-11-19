package com.finderfeed.noise_combiner.noise;

import com.finderfeed.noise_combiner.ComputationContext;

public class FDConstantValueNoise extends FDNoise{

    public float constantValue;

    public FDConstantValueNoise(){
        this.constantValue = 1;
    }

    public FDConstantValueNoise(float constantValue){
        this.constantValue = constantValue;
    }

    @Override
    public float computeNoiseValue(ComputationContext computationContext) {
        return constantValue;
    }

}
