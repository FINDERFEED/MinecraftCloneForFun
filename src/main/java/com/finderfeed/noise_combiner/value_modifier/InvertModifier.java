package com.finderfeed.noise_combiner.value_modifier;

import com.finderfeed.noise_combiner.registry.ObjectType;

public class InvertModifier extends FDValueModifier<InvertModifier> {

    @Override
    public float transformValue(float value) {
        return 1 - value;
    }

    @Override
    public ObjectType<InvertModifier> getObjectType() {
        return NoiseValueModifierRegistry.INVERT_VALUE;
    }

}
