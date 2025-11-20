package com.finderfeed.menu.wrappers.value_modifier_wrappers.instances;

import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapper;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapperRegistry;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapperType;
import com.finderfeed.noise_combiner.value_modifier.instances.AddValueModifier;
import com.finderfeed.noise_combiner.value_modifier.instances.PowModifier;
import imgui.ImGui;
import imgui.type.ImFloat;

public class PowModifierWrapper extends ValueModifierWrapper<PowModifierWrapper, PowModifier> {

    public ImFloat power;

    public PowModifierWrapper(PowModifier object) {
        super(object);
        this.power = new ImFloat(object.power);
    }

    @Override
    public ValueModifierWrapperType<PowModifierWrapper, PowModifier> type() {
        return ValueModifierWrapperRegistry.POWER_VALUE_MODIFIER;
    }

    @Override
    public void renderWrappedObject() {
        if (ImGui.inputFloat("Power", power)){
            this.getObject().power = power.get();
            this.changeListener.run();
        }
    }

}
