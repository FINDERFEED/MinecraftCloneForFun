package com.finderfeed.noise_combiner.value_modifier.instances;

import com.finderfeed.noise_combiner.registry.ObjectType;
import com.finderfeed.noise_combiner.value_modifier.FDValueModifier;
import com.finderfeed.noise_combiner.value_modifier.NoiseValueModifierRegistry;

public class DivideValueModifier extends FDValueModifier<DivideValueModifier> {

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

    @Override
    public ObjectType<DivideValueModifier> getObjectType() {
        return NoiseValueModifierRegistry.DIVIDE_VALUE;
    }

    public float getDivideBy() {
        return divideBy;
    }

    public void setDivideBy(float divideBy) {
        this.divideBy = divideBy;
    }

}
