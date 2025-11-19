package com.finderfeed.noise_combiner.value_modifier;

public class AddValueModifier extends FDValueModifier {

    private float addedValue = 0;

    public AddValueModifier(){

    }

    @Override
    public float transformValue(float value) {
        return value + addedValue;
    }

}
