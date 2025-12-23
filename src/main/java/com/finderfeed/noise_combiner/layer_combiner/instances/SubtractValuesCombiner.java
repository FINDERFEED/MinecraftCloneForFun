package com.finderfeed.noise_combiner.layer_combiner.instances;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.layer_combiner.FDNoiseValueCombiner;
import com.finderfeed.noise_combiner.layer_combiner.NoiseValueCombinerRegistry;
import com.finderfeed.noise_combiner.registry.ObjectType;
import com.google.gson.JsonObject;

public class SubtractValuesCombiner extends FDNoiseValueCombiner<SubtractValuesCombiner> {

    @Override
    public float combine(ComputationContext computationContext, float x, float y) {
        return x - y;
    }

    @Override
    public ObjectType<SubtractValuesCombiner> getType() {
        return NoiseValueCombinerRegistry.SUBTRACT;
    }

    @Override
    public void serializeToJson(JsonObject object) {

    }

    @Override
    public void deserializeFromJson(JsonObject jsonObject) {

    }
}
