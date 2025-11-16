package com.finderfeed.engine;

import com.finderfeed.Camera;
import com.finderfeed.Main;
import com.finderfeed.blocks.Block;
import com.finderfeed.entity.Entity;
import com.finderfeed.periphery.Keyboard;
import com.finderfeed.periphery.Mouse;
import com.finderfeed.util.BlockRayTraceResult;
import com.finderfeed.util.MathUtil;
import com.finderfeed.world.World;
import imgui.ImGui;
import org.joml.Math;
import org.joml.Vector3d;

import static org.lwjgl.glfw.GLFW.*;

public class GLFWBindings {




    public static void onWindowResize(long window, int width, int height){
        Main.window.onResize(width, height);
    }


    public static void charCallback(long window, final int c) {
        FDWindow w = Main.window;
        if (!w.isGameFocused()) {
            w.getImGuiImplGlfw().charCallback(window, c);
        }
    }

    public static void cursorEnterCallback(final long window, final boolean entered) {
        FDWindow w = Main.window;
        w.getImGuiImplGlfw().cursorEnterCallback(window, entered);
    }

    public static void monitorCallback(long window, final int event) {
        FDWindow w = Main.window;
        if (!w.isGameFocused()) {
            w.getImGuiImplGlfw().monitorCallback(window, event);
        }
    }

    public static void scrollCallback(final long window, final double xOffset, final double yOffset) {
        FDWindow w = Main.window;
        if (!w.isGameFocused()) {
            w.getImGuiImplGlfw().scrollCallback(window, xOffset, yOffset);
        }
    }

    public static void windowFocusCallback(final long window, final boolean focused) {
        FDWindow w = Main.window;
        w.getImGuiImplGlfw().windowFocusCallback(window, focused);
    }


    public static void mouseCursorCallback(long window, double xpos, double ypos){


        FDWindow w = Main.window;

        if (w.isGameFocused()) {

            Mouse mouse = Main.mouse;
            Camera camera = Main.camera;
            mouse.update((float) xpos, (float) ypos);
            if (glfwGetInputMode(window, GLFW_CURSOR) != GLFW_CURSOR_NORMAL) {
                float sensivity = 10;
                camera.yaw -= mouse.dx / sensivity;
                camera.pitch = MathUtil.clamp(camera.pitch - mouse.dy / sensivity, -89.9f, 89.9f);
            }
        }else{
            w.getImGuiImplGlfw().cursorPosCallback(window, xpos, ypos);
        }
    }

    public static void mouseCallback(long window, int button, int action, int mods){

        FDWindow w = Main.window;

        if (w.isGameFocused()) {
            Camera camera = Main.camera;
            World world = Main.world;

            if (action == GLFW_PRESS) {
                Vector3d begin = camera.pos;
                Vector3d end = new Vector3d(camera.pos).add(new Vector3d(camera.look).mul(100));
                BlockRayTraceResult result = world.traceBlock(begin, end);
                if (result != null) {
                    var blockpos = result.blockPos;
                    var side = result.side;
                    if (button == GLFW_MOUSE_BUTTON_RIGHT) {
                        var n = side.getNormal();
                        world.setBlock(Block.STONE, blockpos.x + n.x, blockpos.y + n.y, blockpos.z + n.z);
                    } else if (button == GLFW_MOUSE_BUTTON_LEFT) {
                        world.setBlock(Block.AIR, blockpos.x, blockpos.y, blockpos.z);
                    }

                }
            }
        }else{
            w.getImGuiImplGlfw().mouseButtonCallback(window, button, action, mods);
        }
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods){

        FDWindow w = Main.window;

        if (w.isGameFocused()) {
            Keyboard keyboard = Main.keyboard;

            keyboard.keyCallback(window, key, scancode, action, mods);
            if (action == GLFW_PRESS && key == GLFW_KEY_L) {
                Main.controllingEntity.addMovement(0.1, 0, 0.1);
            }

            if (action != GLFW_PRESS && action != GLFW_REPEAT) return;

            if (key == GLFW_KEY_N) {
                Main.chunkRenderDistance++;
                System.out.println(Main.chunkRenderDistance);
            } else if (key == GLFW_KEY_M) {
                Main.chunkRenderDistance = Math.clamp(Main.chunkRenderDistance - 1, 1, Integer.MAX_VALUE);
                System.out.println(Main.chunkRenderDistance);
            } else if (key == GLFW_KEY_F3) {
                Main.debugRendering = !Main.debugRendering;
            } else if (key == GLFW_KEY_F4) {
                if (Main.controllingEntity == null) {
                    Main.controllingEntity = Main.mainEntity;
                } else {
                    Main.controllingEntity = null;
                }
            }
        }else{
            w.getImGuiImplGlfw().keyCallback(window, key, scancode, action, mods);
        }

        if (action == GLFW_PRESS) {
            if (key == GLFW_KEY_ESCAPE) {
                int mode = glfwGetInputMode(window, GLFW_CURSOR);
                if (mode == GLFW_CURSOR_NORMAL) {
                    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                    w.setGameFocused(true);
                } else {
                    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                    w.setGameFocused(false);
                }
            }
        }

    }


}
