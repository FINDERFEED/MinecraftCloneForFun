package com.finderfeed.noise_combiner.value_modifier;

public class AbsoluteModifier extends FDValueModifier {

    @Override
    public float transformValue(float value) {
        return Math.abs(value);
    }

}
