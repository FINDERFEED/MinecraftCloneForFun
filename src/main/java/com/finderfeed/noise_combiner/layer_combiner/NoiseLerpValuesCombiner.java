package com.finderfeed.noise_combiner.layer_combiner;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.NoiseLayer;
import com.finderfeed.util.MathUtil;
import org.joml.Vector3d;

public class NoiseLerpValuesCombiner extends FDNoiseValueCombiner {

    private NoiseLayer layer;

    public NoiseLerpValuesCombiner(){
        this.layer = new NoiseLayer();
    }

    @Override
    public float combine(ComputationContext computationContext, float x, float y) {

        float layerValue = layer.computeValue(computationContext);

        layerValue = Math.clamp(layerValue,-1,1);
        layerValue = (layerValue + 1) / 2f;

        return MathUtil.lerp(x,y,layerValue);
    }

}
