package com.finderfeed.noise_combiner;

import com.finderfeed.noise_combiner.noise.FDNoise;
import com.finderfeed.noise_combiner.noise.NoiseRegistry;
import com.finderfeed.noise_combiner.value_modifier.FDValueModifier;

import java.util.ArrayList;
import java.util.List;

public class NoiseLayer {

    private FDNoise noise;
    private List<FDValueModifier> valueModifiers = new ArrayList<>();

    public NoiseLayer(){
        this.noise = NoiseRegistry.NOISE_REGISTRY.getFactories().getFirst().get();
    }

    public float computeValue(ComputationContext computationContext){
        float baseValue = noise.computeNoiseValue(computationContext);
        for (var valueMod : valueModifiers){
            baseValue = valueMod.transformValue(baseValue);
        }
        return baseValue;
    }

}
