package com.finderfeed.noise_combiner.value_modifier;

public class InvertModifier extends FDValueModifier {

    @Override
    public float transformValue(float value) {
        return 1 - value;
    }

}
