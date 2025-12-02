package com.finderfeed.noise_combiner.value_modifier;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.JsonSerializable;
import com.finderfeed.noise_combiner.registry.ObjectType;

public abstract class FDValueModifier<T extends FDValueModifier<T>> implements JsonSerializable<T> {

    public abstract float transformValue(ComputationContext computationContext, float value);

    public abstract ObjectType<T> getObjectType();

}
