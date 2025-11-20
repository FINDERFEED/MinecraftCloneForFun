package com.finderfeed.noise_combiner.value_modifier.instances;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.registry.ObjectType;
import com.finderfeed.noise_combiner.value_modifier.FDValueModifier;
import com.finderfeed.noise_combiner.value_modifier.NoiseValueModifierRegistry;

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

}
