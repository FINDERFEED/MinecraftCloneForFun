package org.example;

import org.example.periphery.Keyboard;
import org.example.periphery.Mouse;
import org.example.textures.Texture;
import org.example.textures.atlases.AtlasTexture;
import org.example.util.MathUtil;
import org.example.world.chunk.WorldChunk;
import org.example.world.World;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3d;
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

public class Main {


    public static ExecutorService utilExecutor;

    public static ExecutorService renderExecutor;

    public static int chunkRenderDistance = 10;
    public static Mouse mouse;
    public static Keyboard keyboard;
    public static Camera camera;
    public static Matrix4f projectionMatrix;
    public static int width = 1920/2;
    public static int height = 1080/2;
    public static final float Z_NEAR = 0.05f;
    public static final float Z_FAR = 100000f;
    public static Texture atlas = null;
    public static AtlasTexture atlasTexture = null;

    public static boolean debugRendering = false;

    public static Timer timer = new Timer();

    public static Shader POSITION_COLOR;
    public static Shader BLOCK;

    public static int ticks = 1;
    public static int framesRendered = 0;

    public static volatile boolean close = false;

    private static void loop() {

        renderExecutor = Executors.newFixedThreadPool(5);
        utilExecutor = Executors.newFixedThreadPool(10);


        createCapabilities();

        mouse = new Mouse();
        keyboard = new Keyboard();
        camera = new Camera(new Vector3d(0, WorldChunk.HEIGHT / 2,0));

        int texturesAmount = 4;
        int square = (int) Math.sqrt(texturesAmount);
//        atlas = new Texture("atlas",
//                new Texture2DSettings()
//                        .width(square * 16)
//                        .height(square * 16)
//                ,true);

        atlasTexture = new AtlasTexture("textures/block",32);


        World world = new World(5434544);

        BLOCK = new Shader("block",VertexFormat.POSITION_COLOR_UV_NORMAL);
        POSITION_COLOR = new Shader("position_color",VertexFormat.POSITION_COLOR);

        VertexBuffer lines = new VertexBuffer(1024,VertexFormat.POSITION_COLOR);


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

            camera.calculateModelviewMatrix(timer.partialTick);
            setProjectionPerspectiveMatrix();
            renderWorld(world);


            renderWorldSidesDebug(lines);



            glfwSwapBuffers(window);
            glfwPollEvents();
        }


        renderExecutor.shutdown();
        utilExecutor.shutdown();
        close = true;

    }

    private static void renderWorld(World world){
        GL11.glEnable(GL_DEPTH_TEST);
        GL11.glEnable(GL_CULL_FACE);
        BLOCK.run();
        BLOCK.mat4Uniform("projection",projectionMatrix);
        BLOCK.mat4Uniform("modelview",camera.getModelviewMatrix());
//        atlas.bind(0);
        atlasTexture.atlas.bind(0);


        BLOCK.samplerUniform(0);
        world.render();
        BLOCK.clear();
        GL11.glDisable(GL_CULL_FACE);
    }

    private static void renderWorldSidesDebug(VertexBuffer lines){
        if (debugRendering){
            GL11.glDisable(GL_DEPTH_TEST);
            float len = 60;
            setProjectionOrthoMatrix();
            POSITION_COLOR.run();
            POSITION_COLOR.mat4Uniform("projection",projectionMatrix);
            POSITION_COLOR.mat4Uniform("modelview",new Matrix4f().identity());
            GL11.glLineWidth(4);
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



            lines.drawLines(true);

            rot.popMatrix();
            POSITION_COLOR.clear();
        }
    }

    public static void tick(World world){
        world.tick();
        camera.update();
    }


    public static void main(String[] args) {
        run();
    }

    public static void setProjectionPerspectiveMatrix(){
        projectionMatrix = new Matrix4f().perspective(Math.toRadians(60),width / (float) height,Z_NEAR,Z_FAR,false);
    }

    public static void setProjectionOrthoMatrix(){
        projectionMatrix = new Matrix4f().ortho(
                0,width,0,height,Z_NEAR,Z_FAR
        );
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