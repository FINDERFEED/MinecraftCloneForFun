package com.finderfeed.noise_combiner.noise.instances;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.noise.FDNoise;
import com.finderfeed.noise_combiner.noise.NoiseRegistry;
import com.finderfeed.noise_combiner.registry.ObjectType;
import com.google.gson.JsonObject;

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

    @Override
    public void serializeToJson(JsonObject object) {
        object.addProperty("constantValue", this.constantValue);
    }

    @Override
    public void deserializeFromJson(JsonObject jsonObject) {
        this.constantValue = jsonObject.get("constantValue").getAsInt();
    }
}
