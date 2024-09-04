package org.example;

import org.example.util.MathUtil;
import org.example.world.chunk.ChunkPos;
import org.joml.*;
import org.joml.Math;

public class Camera {

    private Matrix4fStack modelviewMatrix;
    public float yaw = 0;
    public float pitch = 0;
    public Vector3d pos;
    public Vector3d oldPos;
    public boolean movedBetweenChunks = true;



    public Vector3f look;


    public Camera(Vector3d pos){
        this.pos = new Vector3d(pos);
        this.oldPos = new Vector3d(pos);
        this.calculateModelviewMatrix(0);
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
    }


    public void calculateModelviewMatrix(float pticks){
        Matrix3f mt = new Matrix3f().rotate(Math.toRadians(yaw),0,1,0).rotate(Math.toRadians(pitch),1,0,0);
        look = mt.transform(0,0,-1,new Vector3f());

        Vector3d p = new Vector3d(
                MathUtil.lerp(oldPos.x,pos.x,pticks),
                MathUtil.lerp(oldPos.y,pos.y,pticks),
                MathUtil.lerp(oldPos.z,pos.z,pticks)
        );

        modelviewMatrix = new Matrix4fStack(16);

        modelviewMatrix.lookAt(
                new Vector3f(0,(float) p.y,0),
                new Vector3f(0,(float) p.y,0).add(look,new Vector3f()),
                new Vector3f(0,1,0)
        );
    }

    public Vector3d calculateCameraPos(float pticks){
        Vector3d p = new Vector3d(
                MathUtil.lerp(oldPos.x,pos.x,pticks),
                MathUtil.lerp(oldPos.y,pos.y,pticks),
                MathUtil.lerp(oldPos.z,pos.z,pticks)
        );
        return p;
    }

    public Matrix4fStack getModelviewMatrix() {
        return modelviewMatrix;
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


}
