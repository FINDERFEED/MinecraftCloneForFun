package com.finderfeed.noise_combiner.value_modifier.instances;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.registry.ObjectType;
import com.finderfeed.noise_combiner.value_modifier.FDValueModifier;
import com.finderfeed.noise_combiner.value_modifier.NoiseValueModifierRegistry;
import com.google.gson.JsonObject;

public class AbsoluteModifier extends FDValueModifier<AbsoluteModifier> {

    @Override
    public float transformValue(ComputationContext context, float value) {
        return Math.abs(value);
    }

    @Override
    public ObjectType<AbsoluteModifier> getObjectType() {
        return NoiseValueModifierRegistry.ABS_VALUE;
    }

    @Override
    public void serializeToJson(JsonObject object) {

    }

    @Override
    public void deserializeFromJson(JsonObject jsonObject) {

    }
}
