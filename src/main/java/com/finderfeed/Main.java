package com.finderfeed;

import com.finderfeed.engine.RenderEngine;
import com.finderfeed.engine.immediate_buffer_supplier.ImmediateBufferSupplier;
import com.finderfeed.engine.immediate_buffer_supplier.RenderOptions;
import com.finderfeed.engine.shaders.Matrix4fUniform;
import com.finderfeed.engine.shaders.Shaders;
import com.finderfeed.engine.textures.TextureManager;
import com.finderfeed.engine.shaders.Shader;
import com.finderfeed.periphery.Keyboard;
import com.finderfeed.periphery.Mouse;
import com.finderfeed.engine.textures.atlases.AtlasTexture;
import com.finderfeed.util.AABox;
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

    public static boolean debugRendering = false;

    public static Timer timer = new Timer();


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

        World world = new World(5434544);

        Shaders.init();

        VertexBuffer lines = new VertexBuffer(1024,VertexFormat.POSITION_COLOR);
        frustum = new Frustum();

        AABox box = new AABox(0,-10,0,1,10,1);
        float y = 100;

        renderExecutor = Executors.newFixedThreadPool(5);
        utilExecutor = Executors.newFixedThreadPool(10);


        while ( !glfwWindowShouldClose(window) ) {
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

            Matrix4f cameraMat = camera.calculateModelviewMatrix();
            RenderEngine.setProjectionPerspectiveMatrix(FOV,width,height,Z_NEAR,Z_FAR);

            var stack = RenderEngine.getModelviewStack();

            stack.pushMatrix();
            stack.set(cameraMat);
            RenderEngine.applyModelviewMatrix();
            frustum.setModelview(RenderEngine.getModelviewMatrix());
            frustum.setProjection(RenderEngine.projectionMatrix);
            renderWorld(world);
            stack.popMatrix();
            RenderEngine.applyModelviewMatrix();


            renderWorldSidesDebug();



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


    private static void renderWorldSidesDebug(){
        if (debugRendering){

            VertexBuffer lines = ImmediateBufferSupplier.get(RenderOptions.DEFAULT_LINES);

            float len = 60;
            RenderEngine.setProjectionOrthoMatrix(width,height,Z_NEAR,Z_FAR);


            Matrix4fStack rot = new Matrix4fStack(3);
            rot.pushMatrix();
            rot.translate(
                    width/2f,height/2f,-100
            );
            rot.rotateX(Math.toRadians(camera.pitch));

            rot.rotateY(Math.toRadians(camera.yaw));

            lines.position(rot,0,0,0).color(1f,0,0,1f);
            lines.position(rot,len,0,0).color(1f,0,0,1f);

            lines.position(rot,0,0,0).color(0f,0,1f,1f);
            lines.position(rot,0,0,-len).color(0f,0,1f,1f);

            lines.position(rot,0,0,0).color(0f,1f,0,1f);
            lines.position(rot,0,len,0).color(0f,1f,0,1f);


            rot.popMatrix();
            ImmediateBufferSupplier.drawCurrent();
        }
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
            camera.yaw -= mouse.dx / 4;
            camera.pitch = MathUtil.clamp(camera.pitch - mouse.dy / 4,-89.9f,89.9f);
        }
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods){

        keyboard.keyCallback(window,key,scancode,action,mods);

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
        }
    }

    public static void mouseCallback(long window, int button, int action, int mods){

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