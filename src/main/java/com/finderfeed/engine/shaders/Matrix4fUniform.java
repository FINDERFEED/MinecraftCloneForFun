package com.finderfeed.engine.shaders;

import org.joml.Matrix4f;

public class Matrix4fUniform extends Uniform<Matrix4f>  {


    public Matrix4fUniform(String name,Matrix4f value) {
        super(name,value);
    }

    @Override
    public void apply(Shader shader) {
        shader.mat4Uniform(this.getName(),this.getValue());
    }
}
