package org.example;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL30.*;

public class Texture2DSettings {

    private int format = GL_RGBA;
    private int magFilter = GL_NEAREST;
    private int minFilter = GL_NEAREST;
    private int wrapS = GL_REPEAT;
    private int wrapT = GL_REPEAT;
    private int width;
    private int height;


    public Texture2DSettings(){

    }

    /**
     * Bind the texture before doing it!
     */
    public void applyToTexture(ByteBuffer data){
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,minFilter);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,magFilter);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,wrapS);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,wrapT);
        glTexImage2D(GL_TEXTURE_2D,0,format,width,height,0,format,GL_UNSIGNED_BYTE,data);
    }

    public void applyToTexture(){
        this.applyToTexture(null);
    }

    public Texture2DSettings width(int width){
        this.width = width;
        return this;
    }

    public Texture2DSettings height(int height){
        this.height = height;
        return this;
    }

    public Texture2DSettings wrapT(int wrapT){
        this.wrapT = wrapT;
        return this;
    }

    public Texture2DSettings wrapS(int wrapS){
        this.wrapS = wrapS;
        return this;
    }

    public Texture2DSettings minFilter(int minFilter){
        this.minFilter = minFilter;
        return this;
    }

    public Texture2DSettings magFilter(int magFilter){
        this.magFilter = magFilter;
        return this;
    }

    public Texture2DSettings format(int format){
        this.format = format;
        return this;
    }


}
