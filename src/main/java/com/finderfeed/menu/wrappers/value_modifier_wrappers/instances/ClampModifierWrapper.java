package com.finderfeed.menu.wrappers.value_modifier_wrappers.instances;

import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapper;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapperRegistry;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapperType;
import com.finderfeed.noise_combiner.value_modifier.instances.ClampModifier;
import com.finderfeed.noise_combiner.value_modifier.instances.PowModifier;
import imgui.ImGui;
import imgui.type.ImFloat;

public class ClampModifierWrapper extends ValueModifierWrapper<ClampModifierWrapper, ClampModifier> {

    public ImFloat min;
    public ImFloat max;

    public ClampModifierWrapper(ClampModifier object) {
        super(object);
        this.min = new ImFloat(object.min);
        this.max = new ImFloat(object.max);
    }

    @Override
    public ValueModifierWrapperType<ClampModifierWrapper, ClampModifier> type() {
        return ValueModifierWrapperRegistry.CLAMP_VALUE_MODIFIER;
    }

    @Override
    public void renderWrappedObject() {
        if (ImGui.inputFloat("Min", min)){
            this.getObject().min = min.get();
            this.changeListener.run();
        }

        if (ImGui.inputFloat("Max", max)){
            max.set(Math.clamp(max.get(), min.get() + 0.001f,Float.MAX_VALUE));
            this.getObject().max = max.get();
            this.changeListener.run();
        }
    }

}
