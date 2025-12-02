package com.finderfeed.noise_combiner.value_modifier.instances;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.registry.ObjectType;
import com.finderfeed.noise_combiner.value_modifier.FDValueModifier;
import com.finderfeed.noise_combiner.value_modifier.NoiseValueModifierRegistry;
import com.google.gson.JsonObject;

public class InvertModifier extends FDValueModifier<InvertModifier> {

    @Override
    public float transformValue(ComputationContext context, float value) {
        return 1 - value;
    }

    @Override
    public ObjectType<InvertModifier> getObjectType() {
        return NoiseValueModifierRegistry.INVERT_VALUE;
    }

    @Override
    public void serializeToJson(JsonObject object) {

    }

    @Override
    public void deserializeFromJson(JsonObject jsonObject) {

    }
}
