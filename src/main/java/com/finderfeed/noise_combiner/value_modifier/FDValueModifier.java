package com.finderfeed.noise_combiner.value_modifier;

import com.finderfeed.noise_combiner.registry.ObjectType;

public abstract class FDValueModifier<T extends FDValueModifier<T>> {

    public abstract float transformValue(float value);

    public abstract ObjectType<T> getObjectType();

}
