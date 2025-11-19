package com.finderfeed.noise_combiner.noise;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.registry.ObjectType;

public class FDConstantValueNoise extends FDNoise<FDConstantValueNoise> {

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

    @Override
    public ObjectType<FDConstantValueNoise> getObjectType() {
        return NoiseRegistry.CONSTANT_VALUE;
    }

}
