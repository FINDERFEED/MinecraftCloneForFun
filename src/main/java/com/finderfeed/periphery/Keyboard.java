package com.finderfeed.periphery;

import static org.lwjgl.glfw.GLFW.*;

public class Keyboard {

    private boolean[] keys = new boolean[512];

    public void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
            keys[key] = true;
        } else if (action == GLFW_RELEASE) {
            keys[key] = false;
        }
    }

    public boolean isKeyPressed(int id) {
        return this.keys[id];
    }

    public boolean hasShiftDown() {
        return this.isKeyPressed(GLFW_KEY_LEFT_SHIFT) || this.isKeyPressed(GLFW_KEY_RIGHT_SHIFT);
    }

    public boolean hasCtrlDown() {
        return this.isKeyPressed(GLFW_KEY_LEFT_CONTROL) || this.isKeyPressed(GLFW_KEY_RIGHT_CONTROL);
    }

}
