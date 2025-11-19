package com.finderfeed.menu.wrappers.value_modifier_wrappers;

import com.finderfeed.noise_combiner.registry.ObjectType;
import com.finderfeed.noise_combiner.value_modifier.FDValueModifier;

import java.util.function.Function;

public class ValueModifierWrapperType<D extends ValueModifierWrapper<D, T>, T extends FDValueModifier<T>> extends ObjectType<D> {

    private Function<T, D> wrapperFactory;
    private ObjectType<T> valueModifierType;

    public ValueModifierWrapperType(String registryId, ObjectType<T> valueModifierType, Function<T, D> wrapperFactory) {
        super(registryId);
        this.valueModifierType = valueModifierType;
        this.wrapperFactory = wrapperFactory;
    }

    public Function<T, D> getWrapperFactory() {
        return wrapperFactory;
    }

    public ObjectType<T> getValueModifierType() {
        return valueModifierType;
    }

}
