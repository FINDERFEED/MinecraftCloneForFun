package com.finderfeed.noise_combiner.value_modifier;

import com.finderfeed.noise_combiner.registry.ObjectType;

public class SubtractValueModifier extends FDValueModifier<SubtractValueModifier> {

    private float subtractedValue = 0;

    public SubtractValueModifier(){

    }

    @Override
    public float transformValue(float value) {
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

}
