package com.finderfeed.noise_combiner.value_modifier.instances;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.registry.ObjectType;
import com.finderfeed.noise_combiner.value_modifier.FDValueModifier;
import com.finderfeed.noise_combiner.value_modifier.NoiseValueModifierRegistry;
import com.google.gson.JsonObject;
import org.lwjgl.system.MemoryUtil;

public class ClampModifier extends FDValueModifier<ClampModifier> {

    public float min = -1;
    public float max = 1;


    @Override
    public float transformValue(ComputationContext context, float value) {
        return Math.clamp(
                value,
                min,
                Math.clamp(max, min + 0.001f, Float.MAX_VALUE)
        );
    }

    @Override
    public ObjectType<ClampModifier> getObjectType() {
        return NoiseValueModifierRegistry.CLAMP_VALUE;
    }

    @Override
    public void serializeToJson(JsonObject object) {
        object.addProperty("min", min);
        object.addProperty("max", max);
    }

    @Override
    public void deserializeFromJson(JsonObject jsonObject) {
        this.min = jsonObject.get("min").getAsFloat();
        this.max = jsonObject.get("max").getAsFloat();
    }

}
