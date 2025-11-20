package com.finderfeed.menu;

import com.finderfeed.GlobalWorldParameters;
import com.finderfeed.noise_combiner.NoiseCombination;
import com.finderfeed.noise_combiner.NoiseLayer;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public class MainMenu {

    private static int MENU_ID = 0;

    private List<Menu> openedMenus = new ArrayList<>();
    private List<Runnable> delayedAfterMenuActions = new ArrayList<>();

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
            NoiseCombination noiseCombination = GlobalWorldParameters.getCurrentNoiseCombination();
            this.openMenu(new NoiseCombinerMenu("Noise combiner",noiseCombination));
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

        delayedAfterMenuActions.forEach(Runnable::run);
        delayedAfterMenuActions.clear();

    }

    public void openMenu(Menu menu){
        delayedAfterMenuActions.add(()->{
            menu.onOpen();
            this.openedMenus.add(menu);
        });
    }

    public static int takeNextFreeMenuId(){
        int ret = MENU_ID;
        MENU_ID++;
        return ret;
    }


}
