package com.finderfeed.engine;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public class RenderEngine {

    public static Matrix4f projectionMatrix = new Matrix4f().identity();
    private static Matrix4f modelviewMatrix = new Matrix4f().identity();
    public static Matrix4fStack modelviewStack = new Matrix4fStack(16);

    public static Matrix4fStack getModelviewStack(){
        return modelviewStack;
    }

    public static void applyModelviewMatrix(){
        modelviewMatrix = modelviewStack.get(new Matrix4f());
    }

    public static Matrix4f getModelviewMatrix(){
        return modelviewMatrix;
    }

    public static void setProjectionPerspectiveMatrix(float fov,float width,float height,float znear,float zfar){
        projectionMatrix = new Matrix4f().perspective(Math.toRadians(fov),width / (float) height,znear,zfar,false);
    }

    public static void setProjectionOrthoMatrix(float width,float height,float znear,float zfar){
        projectionMatrix = new Matrix4f().ortho(
                0,width,0,height,znear,zfar
        );
    }

}
