package com.finderfeed.menu.wrappers.layer_combiner_wrappers.instances;

import com.finderfeed.Main;
import com.finderfeed.engine.FDWindow;
import com.finderfeed.menu.MainMenu;
import com.finderfeed.menu.NoiseLayerRedactorMenu;
import com.finderfeed.menu.wrappers.layer_combiner_wrappers.LayerCombinerWrapper;
import com.finderfeed.noise_combiner.layer_combiner.instances.NoiseLerpValuesCombiner;
import imgui.ImGui;

public class NoiseLerpValuesCombinerWrapper extends LayerCombinerWrapper<NoiseLerpValuesCombinerWrapper, NoiseLerpValuesCombiner> {

    private NoiseLayerRedactorMenu openedMenu = null;

    public NoiseLerpValuesCombinerWrapper(NoiseLerpValuesCombiner object) {
        super(object);
    }

    @Override
    public void renderWrappedObject() {

        if (ImGui.button("Open noise redactor")) {
            int id = MainMenu.takeNextFreeMenuId();
            NoiseLayerRedactorMenu noiseLayerRedactorMenu = new NoiseLayerRedactorMenu("Noise lerp layer redactor##" + id, this.getObject().getLayer());
            noiseLayerRedactorMenu.addOnCloseListener(()->{
                this.openedMenu = null;
            });
            noiseLayerRedactorMenu.addOnChangeListener(()->{
                this.changeListener.run();
            });
            Main.window.getMainMenu().openMenu(noiseLayerRedactorMenu);
        }

    }

    @Override
    public void close() {
        super.close();
        if (openedMenu != null){
            openedMenu.setClosed(true);
        }
    }

}
