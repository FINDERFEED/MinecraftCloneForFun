package com.finderfeed.noise_combiner.value_modifier.instances;

import com.finderfeed.noise_combiner.registry.ObjectType;
import com.finderfeed.noise_combiner.value_modifier.FDValueModifier;
import com.finderfeed.noise_combiner.value_modifier.NoiseValueModifierRegistry;

public class AbsoluteModifier extends FDValueModifier<AbsoluteModifier> {

    @Override
    public float transformValue(float value) {
        return Math.abs(value);
    }

    @Override
    public ObjectType<AbsoluteModifier> getObjectType() {
        return NoiseValueModifierRegistry.ABS_VALUE;
    }

}
