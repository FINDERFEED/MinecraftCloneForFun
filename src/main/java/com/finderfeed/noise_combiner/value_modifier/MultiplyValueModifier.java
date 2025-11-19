package com.finderfeed.noise_combiner.value_modifier;

public class MultiplyValueModifier extends FDValueModifier {

    private float multiplyBy = 1;

    public MultiplyValueModifier(){

    }

    @Override
    public float transformValue(float value) {
        return value * multiplyBy;
    }

}
