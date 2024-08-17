package org.example;

import org.example.blocks.Block;
import org.example.util.MathUtil;
import org.example.world.World;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {

    public static Mouse mouse;
    public static Camera camera;
    public static Matrix4f projectionMatrix;
    public static int width = 1920;
    public static int height = 1080;
    public static final float Z_NEAR = 0.05f;
    public static final float Z_FAR = 100000f;
    public static Texture atlas = null;
    public static World world;



    private static void loop() {

        createCapabilities();

        mouse = new Mouse();
        camera = new Camera(new Vector3f());
        camera.calculateModelviewMatrix();

        int texturesAmount = 4;
        int square = (int) Math.sqrt(texturesAmount);
        atlas = new Texture("atlas",
                new Texture2DSettings()
                        .width(square * 16)
                        .height(square * 16)
                ,true);


        world = new World();
        for (int x = -2; x <= 2;x++){
            for (int z = -2; z <= 2;z++){
                if (x == 1 && z == 1){
                    continue;
                }
                world.getBlock(x * 16,0,z * 16,true);
            }
        }

        Shader shader = new Shader("block",VertexFormat.POSITION_COLOR_UV_NORMAL);


        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


            camera.calculateModelviewMatrix();
            updateProjectionMatrix();


            GL11.glEnable(GL_DEPTH_TEST);




            shader.run();
            shader.mat4Uniform("projection",projectionMatrix);
            shader.mat4Uniform("modelview",camera.getModelviewMatrix());
            atlas.bind(0);
            shader.samplerUniform(0);


            world.render();



            shader.clear();


            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }


    public static void main(String[] args) {
        run();
    }

    public static void updateProjectionMatrix(){
        projectionMatrix = new Matrix4f().perspective(Math.toRadians(60),width / (float) height,Z_NEAR,Z_FAR,false);
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
        if (action != GLFW_PRESS && action != GLFW_REPEAT) return;

        if (key == GLFW_KEY_W){
            camera.moveForward(0.5f);
        }else if (key == GLFW_KEY_S){
            camera.moveForward(-0.5f);
        }else if (key == GLFW_KEY_A){
            camera.moveSidewards(-0.5f);
        }else if (key == GLFW_KEY_D){
            camera.moveSidewards(0.5f);
        }else if (key == GLFW_KEY_SPACE){
            camera.move(0,1,0);
        }else if (key == GLFW_KEY_LEFT_SHIFT){
            camera.move(0,-1,0);
        }else if (key == GLFW_KEY_ESCAPE){
            int mode = glfwGetInputMode(window,GLFW_CURSOR);
            if (mode == GLFW_CURSOR_NORMAL) {
                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            }else{
                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            }
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
        window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
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