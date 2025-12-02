package com.finderfeed;

import com.finderfeed.blocks.Block;
import com.finderfeed.engine.FDWindow;
import com.finderfeed.engine.GameRenderer;
import com.finderfeed.engine.RenderEngine;
import com.finderfeed.engine.immediate_buffer_supplier.ImmediateBufferSupplier;
import com.finderfeed.engine.immediate_buffer_supplier.RenderOptions;
import com.finderfeed.engine.shaders.Matrix4fUniform;
import com.finderfeed.engine.shaders.Shaders;
import com.finderfeed.engine.textures.TextureManager;
import com.finderfeed.engine.shaders.Shader;
import com.finderfeed.entity.Entity;
import com.finderfeed.periphery.Keyboard;
import com.finderfeed.periphery.Mouse;
import com.finderfeed.engine.textures.atlases.AtlasTexture;
import com.finderfeed.util.AABox;
import com.finderfeed.util.BlockRayTraceResult;
import com.finderfeed.util.EasingFunction;
import com.finderfeed.util.MathUtil;
import com.finderfeed.world.World;
import com.finderfeed.world.chunk.WorldChunk;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import sun.misc.Unsafe;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static com.finderfeed.engine.shaders.Shaders.*;


public class Main {

    public static ExecutorService utilExecutor;

    public static ExecutorService renderExecutor;

    public static FDWindow window;

    public static int chunkRenderDistance = 10;
    public static Mouse mouse;
    public static Keyboard keyboard;
    public static Camera camera;
    public static final float Z_NEAR = 0.05f;
    public static final float Z_FAR = 10000f;
    public static final float FOV = 70;
    public static AtlasTexture atlasTexture = null;
    public static Frustum frustum;
    public static TextureManager textureManager;

    public static World world;

    public static boolean debugRendering = false;

    public static Timer timer = new Timer();

    public static Entity mainEntity = null;
    public static Entity controllingEntity = null;

    public static int ticks = 1;
    public static int framesRendered = 0;

    public static volatile boolean close = false;

    private static void loop() {

        mouse = new Mouse();
        keyboard = new Keyboard();
        camera = new Camera(new Vector3d(0, WorldChunk.HEIGHT / 2,0));

        atlasTexture = new AtlasTexture(AtlasTexture.BLOCK_ATLAS_LOCATION,"textures/block",32);

        textureManager = new TextureManager();
        textureManager.putTexture(atlasTexture.atlas.getName(),atlasTexture.atlas);

        world = new World();

        Shaders.init();

        frustum = new Frustum();

        GameRenderer gameRenderer = new GameRenderer(camera);

        renderExecutor = Executors.newFixedThreadPool(5);
        utilExecutor = Executors.newFixedThreadPool(10);

        Entity entity = new Entity(world);
        entity.position = new Vector3d(0.5,110,0.5);
        world.addEntity(entity);
        controllingEntity = entity;
        mainEntity = entity;


        while ( !glfwWindowShouldClose(window.getWindowId()) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


            for (int i = 0; i < Math.min(timer.advanceTime(),20);i++){
                ticks++;
                tick(world);
                if (ticks % Timer.TICKS_PER_SECOND == 0){
                    System.out.println("FPS: " + framesRendered);
                    framesRendered = 0;
                }
            }

            framesRendered++;


            gameRenderer.render();

            window.renderImGui();

            glfwSwapBuffers(window.getWindowId());
            glfwPollEvents();
        }


        renderExecutor.shutdown();
        utilExecutor.shutdown();
        close = true;

    }


    public static void tick(World world){
        world.tick();
        camera.update();
    }


    public static void main(String[] args) {
        run();
    }




    public static void run() {
        init();
        loop();

        window.destroy();

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private static void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        FDWindow fdWindow = new FDWindow();
        window = fdWindow;
        window.initializeWindow(1800,1000);

    }



}