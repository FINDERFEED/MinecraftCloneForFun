package com.finderfeed.menu.wrappers;

public abstract class ObjectWrapper<T> {

    private T object;

    public ObjectWrapper(T object){
        this.object = object;
    }

    public abstract void renderWrappedObject();

    public void initialize(){}

    public void close(){}

    public T getObject() {
        return object;
    }

}
