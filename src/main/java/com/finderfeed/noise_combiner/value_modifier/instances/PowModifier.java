package com.finderfeed.noise_combiner.value_modifier.instances;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.registry.ObjectType;
import com.finderfeed.noise_combiner.value_modifier.FDValueModifier;
import com.finderfeed.noise_combiner.value_modifier.NoiseValueModifierRegistry;
import com.google.gson.JsonObject;

public class PowModifier extends FDValueModifier<PowModifier> {

    public float power = 1;

    public PowModifier(){

    }

    @Override
    public float transformValue(ComputationContext context, float value) {
        return (float) Math.pow(value, power);
    }

    @Override
    public ObjectType<PowModifier> getObjectType() {
        return NoiseValueModifierRegistry.POW_VALUE;
    }


    @Override
    public void serializeToJson(JsonObject object) {
        object.addProperty("power", this.power);
    }

    @Override
    public void deserializeFromJson(JsonObject jsonObject) {
        this.power = jsonObject.get("power").getAsFloat();
    }

}
