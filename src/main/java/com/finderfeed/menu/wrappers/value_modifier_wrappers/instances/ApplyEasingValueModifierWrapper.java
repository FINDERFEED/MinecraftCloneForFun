package com.finderfeed.menu.wrappers.value_modifier_wrappers.instances;

import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapper;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapperRegistry;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapperType;
import com.finderfeed.noise_combiner.value_modifier.NoiseValueModifierRegistry;
import com.finderfeed.noise_combiner.value_modifier.instances.EasingValueModifier;
import com.finderfeed.util.EasingType;
import imgui.ImGui;
import imgui.type.ImString;

public class ApplyEasingValueModifierWrapper extends ValueModifierWrapper<ApplyEasingValueModifierWrapper, EasingValueModifier> {

    private ImString chosenEasingType;

    public ApplyEasingValueModifierWrapper(EasingValueModifier object) {
        super(object);
        this.chosenEasingType = new ImString(object.getEasingType().name());
    }

    @Override
    public void renderWrappedObject() {

        if (ImGui.beginCombo("Easing type", chosenEasingType.get())){

            for (var easingType : EasingType.values()){
                if (ImGui.selectable(easingType.name())){
                    chosenEasingType = new ImString(easingType.name());
                    this.getObject().setEasingType(easingType);
                    this.changeListener.run();
                }
            }

            ImGui.endCombo();
        }

    }

    @Override
    public ValueModifierWrapperType<ApplyEasingValueModifierWrapper, EasingValueModifier> type() {
        return ValueModifierWrapperRegistry.APPLY_EASING_MODIFIER;
    }

}
