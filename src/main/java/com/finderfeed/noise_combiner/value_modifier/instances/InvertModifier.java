package com.finderfeed.noise_combiner.value_modifier.instances;

import com.finderfeed.noise_combiner.registry.ObjectType;
import com.finderfeed.noise_combiner.value_modifier.FDValueModifier;
import com.finderfeed.noise_combiner.value_modifier.NoiseValueModifierRegistry;

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
