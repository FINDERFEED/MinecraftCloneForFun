package com.finderfeed.engine;

import com.finderfeed.Camera;
import com.finderfeed.Main;
import com.finderfeed.VertexBuffer;
import com.finderfeed.engine.immediate_buffer_supplier.ImmediateBufferSupplier;
import com.finderfeed.engine.immediate_buffer_supplier.RenderOptions;
import com.finderfeed.world.World;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL11;

public class GameRenderer {

    public Camera camera;


    public GameRenderer(Camera camera){
        this.camera = camera;
    }

    public void render(){

        Matrix4f cameraMat = camera.calculateModelviewMatrix();
        RenderEngine.setProjectionPerspectiveMatrix(Main.FOV, Main.window.getWidth(),Main.window.getHeight(),Main.Z_NEAR,Main.Z_FAR);

        var stack = RenderEngine.getModelviewStack();

        stack.pushMatrix();
        stack.set(cameraMat);
        RenderEngine.applyModelviewMatrix();
        Main.frustum.setModelview(RenderEngine.getModelviewMatrix());
        Main.frustum.setProjection(RenderEngine.projectionMatrix);

        World world = Main.world;
        world.render(camera,Main.timer.partialTick);


        stack.popMatrix();
        RenderEngine.applyModelviewMatrix();

        RenderEngine.setProjectionOrthoMatrix(Main.window.getWidth(),Main.window.getHeight(),Main.Z_NEAR,Main.Z_FAR);

        stack.pushMatrix();
        stack.set(new Matrix4f().identity());
        RenderEngine.applyModelviewMatrix();
        this.renderCrosshair();
        this.renderWorldSidesDebug();
        stack.popMatrix();
        RenderEngine.applyModelviewMatrix();

    }

    private void renderCrosshair(){
        if (!Main.debugRendering) {
            VertexBuffer lines = ImmediateBufferSupplier.get(RenderOptions.DEFAULT_LINES);
            float w = Main.window.getWidth();
            float h = Main.window.getHeight();
            lines.position(w/2,h/2 - 10,-100).color(1f,1f,1f,1f);
            lines.position(w/2,h/2 + 10,-100).color(1f,1f,1f,1f);

            lines.position(w/2 - 10,h/2,-100).color(1f,1f,1f,1f);
            lines.position(w/2 + 10,h/2,-100).color(1f,1f,1f,1f);

            GL11.glLineWidth(2);
            ImmediateBufferSupplier.drawCurrent();

        }

    }

    private void renderWorldSidesDebug(){
        if (Main.debugRendering){

            VertexBuffer lines = ImmediateBufferSupplier.get(RenderOptions.DEFAULT_LINES);

            float len = 60;


            GL11.glLineWidth(4);
            Matrix4fStack rot = new Matrix4fStack(3);
            rot.pushMatrix();
            rot.translate(
                    Main.window.getWidth()/2f,Main.window.getHeight()/2f,-100
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

}
