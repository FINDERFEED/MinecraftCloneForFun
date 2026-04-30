package com.finderfeed.menu;

import imgui.ImGui;
import imgui.ImVec2;

public class ProjectCrashedMenu extends Menu{

    public ProjectCrashedMenu() {
        super("Program has crashed!", new ImVec2(300,200));
    }

    @Override
    public void renderMenuContents() {
        ImGui.textWrapped("Program has crashed and cannot operate further. Save current project to the desired location and then edit problematic field manually.");

        if (ImGui.button("Save Project")){

        }
    }

    @Override
    public void onOpen() {

    }

    @Override
    public void onClose() {

    }
}
