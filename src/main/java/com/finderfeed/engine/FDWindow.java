package com.finderfeed.engine;

import com.finderfeed.Main;
import com.finderfeed.engine.shaders.Shader;
import com.finderfeed.engine.textures.atlases.AtlasTexture;
import com.finderfeed.menu.MainMenu;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiChildFlags;
import imgui.flag.ImGuiHoveredFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImDouble;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class FDWindow {

    private final ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiImplGl3 = new ImGuiImplGl3();

    private int width;
    private int height;

    private boolean isGameFocused = false;

    private String glslVersion;
    private long windowId;

    private MainMenu mainMenu;

    public FDWindow(){
        this.mainMenu = new MainMenu();
    }

    public ImGuiImplGlfw getImGuiImplGlfw() {
        return imGuiImplGlfw;
    }


    ImDouble imDouble = new ImDouble(1);

    public void renderImGui(){
        this.prepareFrame();

        mainMenu.renderGui();

        this.endFrame();
    }

    public MainMenu getMainMenu() {
        return mainMenu;
    }

    public void initializeWindow(int width, int height){
        this.width = width;
        this.height = height;
        this.lwjglInit();
    }

    private void prepareFrame(){
        imGuiImplGlfw.newFrame();
        imGuiImplGl3.newFrame();
        ImGui.newFrame();
    }

    private void endFrame(){
        ImGui.render();
        imGuiImplGl3.renderDrawData(ImGui.getDrawData());
    }

    public void onResize(int newWidth, int newHeight){
        this.width = newWidth;
        this.height = newHeight;
        GL30.glViewport(0,0,newWidth,newHeight);
    }


    public void setGameFocused(boolean isGameFocused){
        this.isGameFocused = isGameFocused;
    }

    public boolean isGameFocused() {
        return isGameFocused;
    }

    private void lwjglInit(){
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        this.glslVersion = "#version 330";

        // Create the window
        windowId = glfwCreateWindow(width, height, "Noise based terrain generator", NULL, NULL);
        if ( windowId == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(windowId, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    windowId,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }


        glfwMakeContextCurrent(windowId);

        GL.createCapabilities();

        glfwSwapInterval(1);



        ImGui.createContext();
        imGuiImplGlfw.init(this.getWindowId(), false);
        imGuiImplGl3.init(glslVersion);

        this.initCallbacks();

        glfwShowWindow(windowId);
    }

    private void initCallbacks(){
        glfwSetKeyCallback(windowId, GLFWBindings::keyCallback);
        glfwSetMouseButtonCallback(windowId,GLFWBindings::mouseCallback);
        glfwSetCursorPosCallback(windowId,GLFWBindings::mouseCursorCallback);
        glfwSetFramebufferSizeCallback(windowId,GLFWBindings::onWindowResize);
        glfwSetCharCallback(windowId, GLFWBindings::charCallback);
        glfwSetCursorEnterCallback(windowId, GLFWBindings::cursorEnterCallback);
        glfwSetMonitorCallback(GLFWBindings::monitorCallback);
        glfwSetScrollCallback(windowId, GLFWBindings::scrollCallback);
        glfwSetWindowFocusCallback(windowId, GLFWBindings::windowFocusCallback);
    }

    public void destroy(){
        imGuiImplGl3.shutdown();
        imGuiImplGlfw.shutdown();
        glfwFreeCallbacks(windowId);
        glfwDestroyWindow(windowId);
    }

    public long getWindowId() {
        return windowId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
