package com.finderfeed.noise_combiner.value_modifier;

public class DivideValueModifier extends FDValueModifier {

    private float divideBy = 1;

    public DivideValueModifier(){

    }

    @Override
    public float transformValue(float value) {
        if (divideBy == 0){
            return 0;
        }
        return value / divideBy;
    }

}
