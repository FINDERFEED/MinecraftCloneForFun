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

    public void moveValueModifier(int id, boolean up){
        if (valueModifiers.size() < 2) return;

        if (up){
            if (id > 0 && id < valueModifiers.size()){
                FDValueModifier currentModifier = valueModifiers.get(id);
                FDValueModifier upperModifier = valueModifiers.get(id - 1);
                valueModifiers.set(id - 1, currentModifier);
                valueModifiers.set(id, upperModifier);
            }
        }else{
            if (id >= 0 && id < valueModifiers.size() - 1){
                FDValueModifier currentModifier = valueModifiers.get(id);
                FDValueModifier downModifier = valueModifiers.get(id + 1);
                valueModifiers.set(id + 1, currentModifier);
                valueModifiers.set(id, downModifier);
            }
        }

    }

    public float computeValue(ComputationContext computationContext){
        float baseValue = noise.computeNoiseValue(computationContext);
        for (var valueMod : valueModifiers){
            baseValue = valueMod.transformValue(baseValue);
        }
        return baseValue;
    }

    public void setNoise(FDNoise noise) {
        this.noise = noise;
    }

}
