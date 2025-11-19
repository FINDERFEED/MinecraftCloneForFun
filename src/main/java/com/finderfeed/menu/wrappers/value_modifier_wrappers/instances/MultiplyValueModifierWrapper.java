package com.finderfeed.menu.wrappers.value_modifier_wrappers.instances;

import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapper;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapperType;
import com.finderfeed.noise_combiner.value_modifier.MultiplyValueModifier;
import com.finderfeed.noise_combiner.value_modifier.SubtractValueModifier;
import imgui.ImGui;
import imgui.type.ImFloat;

public class MultiplyValueModifierWrapper extends ValueModifierWrapper<MultiplyValueModifierWrapper, MultiplyValueModifier> {

    public ImFloat value;

    public MultiplyValueModifierWrapper(MultiplyValueModifier object) {
        super(object);
        this.value = new ImFloat(object.getMultiplyBy());
    }

    @Override
    public ValueModifierWrapperType<MultiplyValueModifierWrapper, MultiplyValueModifier> type() {
        return null;
    }

    @Override
    public void renderWrappedObject() {
        if (ImGui.inputFloat("Multiplier", value)){
            this.getObject().setMultiplyBy(value.get());
            this.changeListener.run();
        }
    }

}
