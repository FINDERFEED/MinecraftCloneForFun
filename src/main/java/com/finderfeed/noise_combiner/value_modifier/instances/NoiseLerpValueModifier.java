package com.finderfeed.noise_combiner.value_modifier.instances;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.NoiseLayer;
import com.finderfeed.noise_combiner.registry.ObjectType;
import com.finderfeed.noise_combiner.value_modifier.FDValueModifier;
import com.finderfeed.noise_combiner.value_modifier.NoiseValueModifierRegistry;
import com.finderfeed.util.MathUtil;

public class NoiseLerpValueModifier extends FDValueModifier<NoiseLerpValueModifier> {

    public NoiseLayer lerpNoise = new NoiseLayer();
    public NoiseLayer targetNoise = new NoiseLayer();

    @Override
    public float transformValue(ComputationContext computationContext, float value) {

        var lerpValue = lerpNoise.computeValue(computationContext);
        var targetValue = targetNoise.computeValue(computationContext);

        lerpValue = Math.clamp(lerpValue, -1, 1);
        lerpValue = (lerpValue + 1) / 2;

        return MathUtil.lerp(value, targetValue, lerpValue);
    }

    @Override
    public ObjectType<NoiseLerpValueModifier> getObjectType() {
        return NoiseValueModifierRegistry.NOISE_LERP_VALUE;
    }
}
