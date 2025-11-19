package com.finderfeed.menu;

import com.finderfeed.Main;
import imgui.ImGui;

public abstract class Menu {

    private String title;

    private boolean closed = false;

    public Menu(String menuTitle){
        this.title = menuTitle;
    }

    public void renderMenu(){
        ImGui.begin(title);

        if (ImGui.button("Close")){
            closed = true;
        }

        ImGui.separator();

        ImGui.beginChild(title + "_menu_contents");
        this.renderMenuContents();
        ImGui.endChild();

        ImGui.end();
    }

    public abstract void renderMenuContents();

    public abstract void onOpen();

    public abstract void onClose();

    public boolean shouldClose(){
        return closed;
    }

}
