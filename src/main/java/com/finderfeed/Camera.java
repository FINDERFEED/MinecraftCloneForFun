package com.finderfeed;

import com.finderfeed.entity.Entity;
import com.finderfeed.periphery.Keyboard;
import com.finderfeed.util.MathUtil;
import com.finderfeed.world.World;
import com.finderfeed.world.chunk.ChunkPos;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.glfw.GLFW;

public class Camera {

    public float yaw = 0;
    public float pitch = 0;
    public Vector3d pos;
    public Vector3d oldPos;
    public boolean movedBetweenChunks = true;



    public Vector3f look;


    public Camera(Vector3d pos){
        this.pos = new Vector3d(pos);
        this.oldPos = new Vector3d(pos);
        this.calculateModelviewMatrix();
    }


    public void update(){

        ChunkPos currentcPos = new ChunkPos(pos);
        ChunkPos oldcPos = new ChunkPos(this.oldPos);
        if (!currentcPos.equals(oldcPos)){
            movedBetweenChunks = true;
        }else{
            movedBetweenChunks = false;
        }
        this.oldPos = new Vector3d(pos);
        this.handleMovement();
    }

    private void handleMovement(){
        Entity entity = Main.controllingEntity;
        if (entity == null) {
            Keyboard keyboard = Main.keyboard;
            float speed = keyboard.hasCtrlDown() ? 5f : 0.5f;

            if (keyboard.isKeyPressed(GLFW.GLFW_KEY_W)) {
                this.moveForward(speed);
            } else if (keyboard.isKeyPressed(GLFW.GLFW_KEY_S)) {
                this.moveForward(-speed);
            }
            if (keyboard.isKeyPressed(GLFW.GLFW_KEY_A)) {
                this.moveSidewards(-speed);
            } else if (keyboard.isKeyPressed(GLFW.GLFW_KEY_D)) {
                this.moveSidewards(speed);
            }

            if (keyboard.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
                this.move(0, speed, 0);
            } else if (keyboard.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                this.move(0, -speed, 0);
            }
        }else{
            this.pos = new Vector3d(entity.position).add(0,entity.getEyeHeight(),0);

            Keyboard keyboard = Main.keyboard;

            float speed = keyboard.hasCtrlDown() ? 1f : 0.5f;


            if (keyboard.isKeyPressed(GLFW.GLFW_KEY_W)) {



            } else if (keyboard.isKeyPressed(GLFW.GLFW_KEY_S)) {

            }
            if (keyboard.isKeyPressed(GLFW.GLFW_KEY_A)) {

            } else if (keyboard.isKeyPressed(GLFW.GLFW_KEY_D)) {

            }

            if (keyboard.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
                if (entity.onGround){
                    entity.addMovement(0,0.5,0);
                }
            }
        }
    }

    public Matrix4f calculateModelviewMatrix(){
        Matrix3f mt = new Matrix3f().rotate(Math.toRadians(yaw),0,1,0).rotate(Math.toRadians(pitch),1,0,0);
        look = mt.transform(0,0,-1,new Vector3f());
        Matrix4f mat = new Matrix4f();

        mat.lookAt(
                new Vector3f(0,(float) 0,0),
                new Vector3f(0,(float) 0,0).add(look,new Vector3f()),
                new Vector3f(0,1,0)
        );
        return mat;
    }

    public Vector3d calculateCameraPos(float pticks){
        Vector3d p = new Vector3d(
                MathUtil.lerp(oldPos.x,pos.x,pticks),
                MathUtil.lerp(oldPos.y,pos.y,pticks),
                MathUtil.lerp(oldPos.z,pos.z,pticks)
        );
        return p;
    }

    public Vector3f getLook() {
        return new Vector3f(look);
    }

    public Vector3f getPos() {
        return new Vector3f(pos);
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public Vector3f getHorizontalLook(){
        return new Vector3f(look).mul(1,0,1).normalize();
    }

    public void moveForward(float amount){
        Vector3f moveVector = this.getHorizontalLook().mul(amount);
        this.move(moveVector);
    }

    public void moveSidewards(float amount){
        Vector3f moveVector = this.getHorizontalLook().rotateY(-(float) Math.PI / 2f).mul(amount);
        this.move(moveVector);
    }

    public void move(Vector3f v){
        this.move(v.x,v.y,v.z);
    }

    public void move(float x,float y,float z){
        this.pos.add(x,y,z);
    }


    public Vector3f coordToLocal(float x,float y,float z,float pticks){
        Vector3d pos = this.calculateCameraPos(pticks);
        return new Vector3f(
          -(float)pos.x + x,
          -(float)pos.y + y,
          -(float)pos.z + z
        );
    }

}
