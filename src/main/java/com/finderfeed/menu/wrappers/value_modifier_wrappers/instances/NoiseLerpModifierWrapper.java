package com.finderfeed.menu.wrappers.value_modifier_wrappers.instances;

import com.finderfeed.Main;
import com.finderfeed.menu.MainMenu;
import com.finderfeed.menu.NoiseLayerRedactorMenu;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapper;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapperRegistry;
import com.finderfeed.menu.wrappers.value_modifier_wrappers.ValueModifierWrapperType;
import com.finderfeed.noise_combiner.value_modifier.instances.NoiseLerpValueModifier;
import com.finderfeed.util.Util;
import imgui.ImGui;

public class NoiseLerpModifierWrapper extends ValueModifierWrapper<NoiseLerpModifierWrapper, NoiseLerpValueModifier> {

    private NoiseLayerRedactorMenu lerpNoiseRedactor;
    private NoiseLayerRedactorMenu targetNoiseRedactor;

    public NoiseLerpModifierWrapper(NoiseLerpValueModifier object) {
        super(object);
    }

    @Override
    public void renderWrappedObject() {

        if (ImGui.button("Edit lerp noise")){
            if (lerpNoiseRedactor == null){
                int nextId = MainMenu.takeNextFreeMenuId();
                lerpNoiseRedactor = new NoiseLayerRedactorMenu("Lerp noise redactor##" + nextId, this.getObject().lerpNoise);
                lerpNoiseRedactor.addOnCloseListener(()->{
                    lerpNoiseRedactor = null;
                });
                lerpNoiseRedactor.addOnChangeListener(()->{
                    this.changeListener.run();
                });
                Main.window.getMainMenu().openMenu(lerpNoiseRedactor);
            }
        }

        if (ImGui.button("Edit target noise")){
            if (targetNoiseRedactor == null){
                int nextId = MainMenu.takeNextFreeMenuId();
                targetNoiseRedactor = new NoiseLayerRedactorMenu("Target noise redactor##" + nextId, this.getObject().targetNoise);
                targetNoiseRedactor.addOnCloseListener(()->{
                    lerpNoiseRedactor = null;
                });
                targetNoiseRedactor.addOnChangeListener(()->{
                    this.changeListener.run();
                });
                Main.window.getMainMenu().openMenu(targetNoiseRedactor);
            }
        }

        ImGui.button("?");
        Util.insertSimpleTooltip("Takes noise from this layer, lerps it to target noise.");

    }



    @Override
    public void close() {
        super.close();
        if (lerpNoiseRedactor != null){
            lerpNoiseRedactor.setClosed(true);
        }
        if (targetNoiseRedactor != null){
            targetNoiseRedactor.setClosed(true);
        }
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public ValueModifierWrapperType<NoiseLerpModifierWrapper, NoiseLerpValueModifier> type() {
        return ValueModifierWrapperRegistry.NOISE_LERP_VALUE_MODIFIER;
    }

}
