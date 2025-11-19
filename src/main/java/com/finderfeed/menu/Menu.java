package com.finderfeed.menu;

import com.finderfeed.Main;
import imgui.ImGui;
import imgui.ImVec2;

public abstract class Menu {

    private String title;

    private boolean closed = false;
    private boolean initialized = false;
    private ImVec2 initSize;

    public Menu(String menuTitle, ImVec2 initSize){
        this.title = menuTitle;
        this.initSize = initSize;
    }

    public void renderMenu(){
        if (!initialized){
            ImGui.setNextWindowSize(initSize);
            initialized = true;
        }
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

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

}
