package com.finderfeed.menu;

import com.finderfeed.noise_combiner.NoiseLayer;
import imgui.ImFontConfig;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public class MainMenu {

    private List<Menu> openedMenus = new ArrayList<>();

    public MainMenu(){

    }

    public void renderGui(){
        if (ImGui.beginMainMenuBar()){
            this.renderMainMenuButtons();
            this.renderMenus();
            ImGui.endMainMenuBar();
        }
    }

    private void renderMainMenuButtons(){
        if (ImGui.button("Noise combiner menu") && openedMenus.stream().noneMatch(menu -> menu instanceof NoiseCombinerMenu)){
            this.openMenu(new NoiseCombinerMenu("Noise combiner"));
        }

        if (ImGui.button("Test noise layer redactor")){
            NoiseLayer noiseLayer = new NoiseLayer();
            this.openMenu(new NoiseLayerRedactorMenu("Noise Layer", noiseLayer));
        }

    }

    private void renderMenus(){
        var iterator = openedMenus.iterator();
        while (iterator.hasNext()){

            var menu = iterator.next();
            menu.renderMenu();
            if (menu.shouldClose()){
                menu.onClose();
                iterator.remove();
            }

        }
    }

    public void openMenu(Menu menu){
        menu.onOpen();
        this.openedMenus.add(menu);
    }


}
