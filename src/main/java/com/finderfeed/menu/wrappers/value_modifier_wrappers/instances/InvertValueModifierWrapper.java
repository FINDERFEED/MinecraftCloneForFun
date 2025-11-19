package com.finderfeed.menu.wrappers.value_modifier_wrappers.instances;

import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapper;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapperType;
import com.finderfeed.noise_combiner.value_modifier.AddValueModifier;
import com.finderfeed.noise_combiner.value_modifier.InvertModifier;
import imgui.ImGui;

public class InvertValueModifierWrapper extends ValueModifierWrapper<InvertValueModifierWrapper, InvertModifier> {


    public InvertValueModifierWrapper(InvertModifier object) {
        super(object);
    }

    @Override
    public ValueModifierWrapperType<InvertValueModifierWrapper, InvertModifier> type() {
        return null;
    }

    @Override
    public void renderWrappedObject() {
    }

}
