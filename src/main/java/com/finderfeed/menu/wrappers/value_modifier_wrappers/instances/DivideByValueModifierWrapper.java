package com.finderfeed.menu.wrappers.value_modifier_wrappers.instances;

import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapper;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapperType;
import com.finderfeed.noise_combiner.value_modifier.DivideValueModifier;
import com.finderfeed.noise_combiner.value_modifier.MultiplyValueModifier;
import imgui.ImGui;
import imgui.type.ImFloat;

public class DivideByValueModifierWrapper extends ValueModifierWrapper<DivideByValueModifierWrapper, DivideValueModifier> {

    public ImFloat value;

    public DivideByValueModifierWrapper(DivideValueModifier object) {
        super(object);
        this.value = new ImFloat(object.getDivideBy());
    }

    @Override
    public ValueModifierWrapperType<DivideByValueModifierWrapper, DivideValueModifier> type() {
        return null;
    }

    @Override
    public void renderWrappedObject() {
        if (ImGui.inputFloat("Multiplier", value)){
            this.getObject().setDivideBy(value.get());
            this.changeListener.run();
        }
    }

}
