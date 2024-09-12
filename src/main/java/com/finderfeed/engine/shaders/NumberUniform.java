package com.finderfeed.engine.shaders;

import org.lwjgl.opengl.GL11;

public class NumberUniform extends Uniform<Number> {

    public NumberUniform(String name, Number value) {
        super(name, value);
    }

    @Override
    public void apply(Shader shader) {
        if (this.getValue() instanceof Integer i){
            shader.intUniform(this.getName(),i);
        }else if (this.getValue() instanceof Float f){
            shader.floatUniform(this.getName(),f);
        }else if (this.getValue() instanceof Double d){
            shader.floatUniform(this.getName(),(float)(double)d);
        }else{
            throw new RuntimeException("Uniform " + this.getName() + " is not int,float or double");
        }
    }
}
