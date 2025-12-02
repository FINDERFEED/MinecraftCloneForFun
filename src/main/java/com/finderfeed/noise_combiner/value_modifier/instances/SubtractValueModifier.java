package com.finderfeed.noise_combiner.value_modifier.instances;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.registry.ObjectType;
import com.finderfeed.noise_combiner.value_modifier.FDValueModifier;
import com.finderfeed.noise_combiner.value_modifier.NoiseValueModifierRegistry;
import com.google.gson.JsonObject;

public class SubtractValueModifier extends FDValueModifier<SubtractValueModifier> {

    private float subtractedValue = 0;

    public SubtractValueModifier(){

    }

    @Override
    public float transformValue(ComputationContext context, float value) {
        return value - subtractedValue;
    }

    @Override
    public ObjectType<SubtractValueModifier> getObjectType() {
        return NoiseValueModifierRegistry.SUBTRACT_VALUE;
    }

    public float getSubtractedValue() {
        return subtractedValue;
    }

    public void setSubtractedValue(float subtractedValue) {
        this.subtractedValue = subtractedValue;
    }

    @Override
    public void serializeToJson(JsonObject object) {
        object.addProperty("subtractedValue", subtractedValue);
    }

    @Override
    public void deserializeFromJson(JsonObject jsonObject) {
        this.subtractedValue = jsonObject.get("subtractedValue").getAsFloat();
    }

}
