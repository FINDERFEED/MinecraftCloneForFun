package com.finderfeed.noise_combiner.layer_combiner.instances;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.NoiseLayer;
import com.finderfeed.noise_combiner.layer_combiner.FDNoiseValueCombiner;
import com.finderfeed.noise_combiner.layer_combiner.NoiseValueCombinerRegistry;
import com.finderfeed.noise_combiner.registry.ObjectType;
import com.finderfeed.util.MathUtil;
import com.google.gson.JsonObject;

public class NoiseLerpValuesCombiner extends FDNoiseValueCombiner<NoiseLerpValuesCombiner> {

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

    public NoiseLayer getLayer() {
        return layer;
    }

    @Override
    public ObjectType<NoiseLerpValuesCombiner> getType() {
        return NoiseValueCombinerRegistry.NOISE_LERP;
    }

    @Override
    public void serializeToJson(JsonObject object) {
        JsonObject lerpByLayer = new JsonObject();
        this.layer.serializeToJson(lerpByLayer);
        object.add("lerpByLayer", lerpByLayer);
    }

    @Override
    public void deserializeFromJson(JsonObject jsonObject) {
        this.layer.deserializeFromJson(jsonObject.get("lerpByLayer").getAsJsonObject());
    }
}
