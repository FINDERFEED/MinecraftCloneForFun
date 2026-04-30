package com.finderfeed.menu.wrappers.value_modifier_wrappers.instances;

import com.finderfeed.Main;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapper;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapperRegistry;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapperType;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.instances.curve_modifier_wrapper.CurveModifierMenu;
import com.finderfeed.noise_combiner.value_modifier.instances.curve_modifier.CurveModifier;
import imgui.ImGui;

public class CurveModifierWrapper extends ValueModifierWrapper<CurveModifierWrapper, CurveModifier> {

    public CurveModifierWrapper(CurveModifier object) {
        super(object);
    }

    @Override
    public ValueModifierWrapperType<CurveModifierWrapper, CurveModifier> type() {
        return ValueModifierWrapperRegistry.CURVE_MODIFIER;
    }

    @Override
    public void renderWrappedObject() {

        CurveModifier curveModifier = this.getObject();


        //here a button that opens CurveModifierMenu, create CurveModifierMenu yourself

        if (ImGui.button("Open Curve Editor")) {
            CurveModifierMenu menu = new CurveModifierMenu("Curve Modifier", curveModifier);
            menu.addOnChangeListener(this.changeListener);
            Main.window.getMainMenu().openMenu(menu);
        }

    }

}
