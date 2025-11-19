package com.finderfeed.menu.wrappers.value_modifier_wrappers.instances;

import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapper;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapperRegistry;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapperType;
import com.finderfeed.noise_combiner.value_modifier.AbsoluteModifier;
import com.finderfeed.noise_combiner.value_modifier.AddValueModifier;
import imgui.ImGui;
import imgui.type.ImFloat;

public class AddValueModifierWrapper extends ValueModifierWrapper<AddValueModifierWrapper, AddValueModifier> {

    public ImFloat value;

    public AddValueModifierWrapper(AddValueModifier object) {
        super(object);
        this.value = new ImFloat(object.getAddedValue());
    }

    @Override
    public ValueModifierWrapperType<AddValueModifierWrapper, AddValueModifier> type() {
        return null;
    }

    @Override
    public void renderWrappedObject() {
        if (ImGui.inputFloat("Added value", value)){
            this.getObject().setAddedValue(value.get());
            this.changeListener.run();
        }
    }

}
