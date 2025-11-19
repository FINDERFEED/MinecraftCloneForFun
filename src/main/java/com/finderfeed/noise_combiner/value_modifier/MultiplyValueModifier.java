package com.finderfeed.noise_combiner.value_modifier;

import com.finderfeed.noise_combiner.registry.ObjectType;

public class MultiplyValueModifier extends FDValueModifier<MultiplyValueModifier> {

    private float multiplyBy = 1;

    public MultiplyValueModifier(){

    }

    @Override
    public float transformValue(float value) {
        return value * multiplyBy;
    }

    @Override
    public ObjectType<MultiplyValueModifier> getObjectType() {
        return NoiseValueModifierRegistry.MULTIPLY_VALUE;
    }

    public float getMultiplyBy() {
        return multiplyBy;
    }

    public void setMultiplyBy(float multiplyBy) {
        this.multiplyBy = multiplyBy;
    }

}
