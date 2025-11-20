package com.finderfeed.noise_combiner.value_modifier.instances;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.registry.ObjectType;
import com.finderfeed.noise_combiner.value_modifier.FDValueModifier;
import com.finderfeed.noise_combiner.value_modifier.NoiseValueModifierRegistry;

public class MultiplyValueModifier extends FDValueModifier<MultiplyValueModifier> {

    private float multiplyBy = 1;

    public MultiplyValueModifier(){

    }

    @Override
    public float transformValue(ComputationContext context, float value) {
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
