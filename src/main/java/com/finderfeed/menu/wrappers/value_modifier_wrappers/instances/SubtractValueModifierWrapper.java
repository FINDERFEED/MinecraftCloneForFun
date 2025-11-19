package com.finderfeed.menu.wrappers.value_modifier_wrappers.instances;

import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapper;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapperType;
import com.finderfeed.noise_combiner.value_modifier.AddValueModifier;
import com.finderfeed.noise_combiner.value_modifier.SubtractValueModifier;
import imgui.ImGui;
import imgui.type.ImFloat;

public class SubtractValueModifierWrapper extends ValueModifierWrapper<SubtractValueModifierWrapper, SubtractValueModifier> {

    public ImFloat value;

    public SubtractValueModifierWrapper(SubtractValueModifier object) {
        super(object);
        this.value = new ImFloat(object.getSubtractedValue());
    }

    @Override
    public ValueModifierWrapperType<SubtractValueModifierWrapper, SubtractValueModifier> type() {
        return null;
    }

    @Override
    public void renderWrappedObject() {
        if (ImGui.inputFloat("Subtracted value", value)){
            this.getObject().setSubtractedValue(value.get());
            this.changeListener.run();
        }
    }

}
