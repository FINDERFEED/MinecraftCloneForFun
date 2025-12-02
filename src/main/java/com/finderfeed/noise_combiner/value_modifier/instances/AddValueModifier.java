package com.finderfeed.noise_combiner.value_modifier.instances;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.registry.ObjectType;
import com.finderfeed.noise_combiner.value_modifier.FDValueModifier;
import com.finderfeed.noise_combiner.value_modifier.NoiseValueModifierRegistry;
import com.google.gson.JsonObject;

public class AddValueModifier extends FDValueModifier<AddValueModifier> {

    private float addedValue = 0;

    public AddValueModifier(){

    }

    @Override
    public float transformValue(ComputationContext context, float value) {
        return value + addedValue;
    }

    @Override
    public ObjectType<AddValueModifier> getObjectType() {
        return NoiseValueModifierRegistry.ADD_VALUE;
    }

    public float getAddedValue() {
        return addedValue;
    }

    public void setAddedValue(float addedValue) {
        this.addedValue = addedValue;
    }

    @Override
    public void serializeToJson(JsonObject object) {
        object.addProperty("addedValue", addedValue);
    }

    @Override
    public void deserializeFromJson(JsonObject jsonObject) {
        this.addedValue = jsonObject.get("addedValue").getAsFloat();
    }

}
