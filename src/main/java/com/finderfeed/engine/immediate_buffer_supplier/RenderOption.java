package com.finderfeed.engine.immediate_buffer_supplier;

import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL30.*;

public class RenderOption {

    public static final RenderOption BLEND = new RenderOption(()->{
        glEnable(GL_BLEND);
    },()->{
        glDisable(GL_BLEND);
    });

    public static final RenderOption CULL = new RenderOption(()->{
        glEnable(GL_CULL_FACE);
    },()->{
        glDisable(GL_CULL_FACE);
    });

    public static final RenderOption DEFAULT_DEPTH_TEST = new RenderOption(()->{
        GL11.glEnable(GL_DEPTH_TEST);
    },()->{
        GL11.glDisable(GL_DEPTH_TEST);
    });



    public Runnable run;
    public Runnable clear;

    public RenderOption(Runnable run,Runnable clear){
        this.run = run;
        this.clear = clear;
    }

    public void run(){
        this.run.run();
    }

    public void clear(){
        this.clear.run();
    }

}
