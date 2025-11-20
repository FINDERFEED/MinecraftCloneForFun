package com.finderfeed.noise_combiner;

import com.finderfeed.noise_combiner.noise.instances.FDConstantValueNoise;

import java.util.ArrayList;
import java.util.List;

public class NoiseCombination {

    private List<NoiseCombinationLayer> noiseCombinationLayers = new ArrayList<>();

    public NoiseCombination(){
        this.noiseCombinationLayers.add(this.createDefault());
    }

    public float compute(ComputationContext computationContext){

        float value = noiseCombinationLayers.getFirst().getNoiseLayer().computeValue(computationContext);

        for (int i = 0; i < noiseCombinationLayers.size() - 1; i++){

            NoiseCombinationLayer layer = noiseCombinationLayers.get(i);
            NoiseCombinationLayer nextLayer = noiseCombinationLayers.get(i);

            var combiner = layer.getCombiner();
            float nextLayerValue = nextLayer.getNoiseLayer().computeValue(computationContext);

            value = combiner.combine(computationContext, value, nextLayerValue);

        }

        return value;
    }

    public List<NoiseCombinationLayer> getNoiseCombinationLayers() {
        return noiseCombinationLayers;
    }

    private NoiseCombinationLayer createDefault(){
        NoiseCombinationLayer noiseCombinationLayer = new NoiseCombinationLayer();
        noiseCombinationLayer.getNoiseLayer().setNoise(new FDConstantValueNoise(0));
        noiseCombinationLayers.add(noiseCombinationLayer);
        return noiseCombinationLayer;
    }

}
