package com.finderfeed.noise_combiner;

import com.finderfeed.noise_combiner.layer_combiner.FDNoiseValueCombiner;
import com.finderfeed.noise_combiner.layer_combiner.instances.SumValuesCombiner;

public class NoiseCombinationLayer {

    private NoiseLayer noiseLayer;
    private FDNoiseValueCombiner<?> combiner;

    public NoiseCombinationLayer(){
        this.combiner = new SumValuesCombiner();
        this.noiseLayer = new NoiseLayer();
    }

    public FDNoiseValueCombiner<?> getCombiner() {
        return combiner;
    }

    public NoiseLayer getNoiseLayer() {
        return noiseLayer;
    }

    public void setCombiner(FDNoiseValueCombiner<?> combiner) {
        this.combiner = combiner;
    }
}
