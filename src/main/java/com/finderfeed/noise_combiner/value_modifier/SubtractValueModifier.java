package com.finderfeed.noise_combiner.value_modifier;

public class SubtractValueModifier extends FDValueModifier {

    private float subtractedValue = 0;

    public SubtractValueModifier(){

    }

    @Override
    public float transformValue(float value) {
        return value - subtractedValue;
    }

}
