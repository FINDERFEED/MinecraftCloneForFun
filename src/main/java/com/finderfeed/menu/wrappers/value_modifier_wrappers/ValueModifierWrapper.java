package com.finderfeed.menu.wrappers.value_modifier_wrappers;

import com.finderfeed.menu.wrappers.ObjectWrapper;
import com.finderfeed.noise_combiner.registry.ObjectType;
import com.finderfeed.noise_combiner.value_modifier.FDValueModifier;

public abstract class ValueModifierWrapper<D extends ValueModifierWrapper<D,T>, T extends FDValueModifier<T>> extends ObjectWrapper<T> {

    public ValueModifierWrapper(T object) {
        super(object);
    }

    public abstract ValueModifierWrapperType<D,T> type();

}
