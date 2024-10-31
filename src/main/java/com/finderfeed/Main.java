package com.finderfeed;

import com.finderfeed.blocks.Block;
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

import java.nio.IntBuffer;
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

    public static int chunkRenderDistance = 10;
    public static Mouse mouse;
    public static Keyboard keyboard;
    public static Camera camera;
    public static int width = 1920/2;
    public static int height = 1080/2;
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



        createCapabilities();

        mouse = new Mouse();
        keyboard = new Keyboard();
        camera = new Camera(new Vector3d(0, WorldChunk.HEIGHT / 2,0));

        atlasTexture = new AtlasTexture(AtlasTexture.BLOCK_ATLAS_LOCATION,"textures/block",32);

        textureManager = new TextureManager();
        textureManager.putTexture(atlasTexture.atlas.getName(),atlasTexture.atlas);

        world = new World(5434544);

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


        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


            for (int i = 0; i < Math.min(timer.advanceTime(),20);i++){
                ticks++;
                tick(world);
                if (ticks % Timer.TICKS_PER_SECOND == 0){
//                    System.out.println("Camera X: " + camera.pos.x);
//                    System.out.println("Camera Y: " + camera.pos.y);
//                    System.out.println("Camera Z: " + camera.pos.z);
//                    System.out.println("FPS: " + framesRendered);
                    framesRendered = 0;
                }
            }

            framesRendered++;


            gameRenderer.render();


            glfwSwapBuffers(window);
            glfwPollEvents();
        }


        renderExecutor.shutdown();
        utilExecutor.shutdown();
        close = true;

    }

    private static void renderWorld(World world){
        world.render(camera,timer.partialTick);
    }




    public static void tick(World world){
        world.tick();
        camera.update();
    }


    public static void main(String[] args) {
        run();
    }



    public static void onWindowResize(long window, int width, int height){
        Main.width = width;
        Main.height = height;
        GL30.glViewport(0,0,width,height);
    }


    public static void mouseCursorCallback(long window, double xpos, double ypos){
        mouse.update((float) xpos, (float) ypos);
        if (glfwGetInputMode(window,GLFW_CURSOR) != GLFW_CURSOR_NORMAL) {
            float sensivity = 10;
            camera.yaw -= mouse.dx / sensivity;
            camera.pitch = MathUtil.clamp(camera.pitch - mouse.dy / sensivity,-89.9f,89.9f);
        }
    }

    public static void mouseCallback(long window, int button, int action, int mods){
        if (action == GLFW_PRESS){
            Vector3d begin = camera.pos;
            Vector3d end = new Vector3d(camera.pos).add(new Vector3d(camera.look).mul(100));
            BlockRayTraceResult result = world.traceBlock(begin,end);
            if (result != null && glfwGetInputMode(window,GLFW_CURSOR) != GLFW_CURSOR_NORMAL){
                var blockpos = result.blockPos;
                var side = result.side;
                if (button == GLFW_MOUSE_BUTTON_RIGHT) {
                    var n = side.getNormal();
                    world.setBlock(Block.STONE, blockpos.x + n.x, blockpos.y + n.y, blockpos.z + n.z);
                }else if (button == GLFW_MOUSE_BUTTON_LEFT){
                    world.setBlock(Block.AIR, blockpos.x, blockpos.y, blockpos.z);
                }
            }
        }
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods){

        keyboard.keyCallback(window,key,scancode,action,mods);
        if (action == GLFW_PRESS && key == GLFW_KEY_L){
            controllingEntity.addMovement(0.1,0,0.1);
        }

        if (action != GLFW_PRESS && action != GLFW_REPEAT) return;

        if (key == GLFW_KEY_ESCAPE){
            int mode = glfwGetInputMode(window,GLFW_CURSOR);
            if (mode == GLFW_CURSOR_NORMAL) {
                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            }else{
                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            }
        }else if (key == GLFW_KEY_N){
            chunkRenderDistance++;
            System.out.println(chunkRenderDistance);
        }else if (key == GLFW_KEY_M){
            chunkRenderDistance = Math.clamp(chunkRenderDistance - 1,1,Integer.MAX_VALUE);
            System.out.println(chunkRenderDistance);
        }else if (key == GLFW_KEY_F3){
            debugRendering = !debugRendering;
        }else if (key == GLFW_KEY_F4){
            if (controllingEntity == null){
                controllingEntity = mainEntity;
            }else{
                controllingEntity = null;
            }
        }
    }




    public static long window;

    public static void run() {
        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
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

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(width, height, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, Main::keyCallback);
        glfwSetMouseButtonCallback(window,Main::mouseCallback);
        glfwSetCursorPosCallback(window,Main::mouseCursorCallback);
        glfwSetFramebufferSizeCallback(window,Main::onWindowResize);


        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }



}