package com.finderfeed.menu.wrappers;

//Used to connect an object and its values to gui elements
public abstract class ObjectWrapper<T> {

    private T object;

    protected Runnable changeListener;

    public ObjectWrapper(T object){
        this.object = object;
    }

    public void setChangeListener(Runnable changeListener) {
        this.changeListener = changeListener;
    }

    public abstract void renderWrappedObject();

    public void initialize(){}

    public void close(){}

    public T getObject() {
        return object;
    }

}
