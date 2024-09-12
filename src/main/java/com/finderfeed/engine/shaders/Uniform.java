package com.finderfeed.engine.shaders;

public abstract class Uniform<T> {

    private T value;
    private String name;

    public Uniform(String name,T value){
        this.value = value;
        this.name = name;
    }

    public abstract void apply(Shader shader);

    public T getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
