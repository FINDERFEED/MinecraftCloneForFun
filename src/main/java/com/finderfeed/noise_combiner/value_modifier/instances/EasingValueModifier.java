package com.finderfeed.noise_combiner.value_modifier.instances;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.registry.ObjectType;
import com.finderfeed.noise_combiner.value_modifier.FDValueModifier;
import com.finderfeed.noise_combiner.value_modifier.NoiseValueModifierRegistry;
import com.finderfeed.util.EasingType;
import com.google.gson.JsonObject;

public class EasingValueModifier extends FDValueModifier<EasingValueModifier> {

    private EasingType easingType = EasingType.EASE_IN;

    @Override
    public float transformValue(ComputationContext computationContext, float value) {
        return easingType.transformValue(value);
    }

    @Override
    public ObjectType<EasingValueModifier> getObjectType() {
        return NoiseValueModifierRegistry.APPLY_EASING;
    }

    public EasingType getEasingType() {
        return easingType;
    }

    public void setEasingType(EasingType easingType) {
        this.easingType = easingType;
    }

    @Override
    public void serializeToJson(JsonObject object) {
        object.addProperty("easingType", easingType.name());
    }

    @Override
    public void deserializeFromJson(JsonObject jsonObject) {
        this.easingType = EasingType.valueOf(jsonObject.get("easingType").getAsString());
    }
}
