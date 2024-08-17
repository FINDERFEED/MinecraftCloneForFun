package org.example;

import org.joml.AxisAngle4f;
import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private Matrix4f modelviewMatrix;
    public float yaw = 0;
    public float pitch = 0;
    public Vector3f pos;
    public Vector3f look;

    public Camera(Vector3f pos){
        this.pos = new Vector3f(pos);
        this.calculateModelviewMatrix();
    }


    public void update(){

    }


    public void calculateModelviewMatrix(){
        Matrix3f mt = new Matrix3f().rotate(Math.toRadians(yaw),0,1,0).rotate(Math.toRadians(pitch),1,0,0);
        look = mt.transform(0,0,-1,new Vector3f());
        modelviewMatrix = new Matrix4f().lookAt(
                pos,
                pos.add(look,new Vector3f()),
                new Vector3f(0,1,0)
        );
    }

    public Matrix4f getModelviewMatrix() {
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
        this.calculateModelviewMatrix();
    }


}
