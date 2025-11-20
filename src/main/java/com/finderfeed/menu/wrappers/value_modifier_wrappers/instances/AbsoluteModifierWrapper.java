package com.finderfeed.menu.wrappers.value_modifier_wrappers.instances;

import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapper;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapperRegistry;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapperType;
import com.finderfeed.noise_combiner.value_modifier.instances.AbsoluteModifier;

public class AbsoluteModifierWrapper extends ValueModifierWrapper<AbsoluteModifierWrapper, AbsoluteModifier> {

    public AbsoluteModifierWrapper(AbsoluteModifier object) {
        super(object);
    }

    @Override
    public ValueModifierWrapperType<AbsoluteModifierWrapper, AbsoluteModifier> type() {
        return ValueModifierWrapperRegistry.ABSOLUTE_MODIFIER_WRAPPER;
    }

    @Override
    public void renderWrappedObject() {

    }
}
