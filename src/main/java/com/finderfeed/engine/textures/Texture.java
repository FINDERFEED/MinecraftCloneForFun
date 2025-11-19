package com.finderfeed.engine.textures;

import com.finderfeed.util.Util;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class Texture {
    private static final int[] textureids = {
            GL_TEXTURE0,
            GL_TEXTURE1,
            GL_TEXTURE2,
            GL_TEXTURE3,
            GL_TEXTURE4,
            GL_TEXTURE5,
            GL_TEXTURE6,
            GL_TEXTURE7,
            GL_TEXTURE8,
            GL_TEXTURE9
    };

    static {

    }

    private Texture2DSettings settings;
    private int textureId;
    private int texWidth = -1;
    private int texHeight = -1;
    private int channels;
    private String name;


    public Texture(String name,ByteBuffer textureData,Texture2DSettings settings){
        this.name = name;
        this.setupTexture(textureData,settings);
        this.texWidth = settings.getWidth();
        this.texHeight = settings.getHeight();
        this.settings = settings;
    }

    public Texture(String name, Texture2DSettings settings, boolean load){
        STBImage.stbi_set_flip_vertically_on_load(true);
        this.name = name;
        ByteBuffer buffer = null;
        ByteBuffer texture = null;
        if (load) {
            buffer = this.loadTextureBuffer();
            texture = this.loadTexture(buffer);
        }
        this.setupTexture(texture,settings);
        if (texture != null) {
            STBImage.stbi_image_free(texture);
        }
        if (buffer != null) {
            MemoryUtil.memFree(buffer);
        }
        this.settings = settings;
    }

    public Texture(String name,Texture2DSettings settings){
        this(name,settings,true);
    }

    public Texture(String name){
        this(name,new Texture2DSettings(),true);
    }

    private ByteBuffer loadTextureBuffer(){
        InputStream stream = getClass().getClassLoader().getResourceAsStream("textures/" + name + ".png");
        byte[] bytes;
        try {
            bytes = stream.readAllBytes();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        return buffer;
    }

    private ByteBuffer loadTexture(ByteBuffer buffer){
        IntBuffer w = MemoryUtil.memAllocInt(1);
        IntBuffer h = MemoryUtil.memAllocInt(1);
        IntBuffer c = MemoryUtil.memAllocInt(1);
        ByteBuffer texture = STBImage.stbi_load_from_memory(buffer,w,h,c,4);
        if (texture == null){
            throw new RuntimeException("Failed to load texture: " + name);
        }
        this.texWidth = w.get(0);
        this.texHeight = h.get(0);
        this.channels = c.get(0);
        MemoryUtil.memFree(w);
        MemoryUtil.memFree(c);
        MemoryUtil.memFree(h);
        return texture;
    }

    private void setupTexture(ByteBuffer buffer,Texture2DSettings settings){
        this.textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D,this.textureId);
        if (texWidth != -1){
            settings.width(texWidth).height(texHeight);
        }
        settings.applyToTexture(buffer);
    }


    public void bind(int id){
        glActiveTexture(textureids[id]);
        glBindTexture(GL_TEXTURE_2D,this.getTextureId());
    }

    public void updateTextureWithBufferedImage(BufferedImage image){

        if (image.getWidth() != this.getTexWidth() || image.getHeight() != this.getTexHeight() || image.getType() != BufferedImage.TYPE_INT_ARGB)
            throw new RuntimeException("Cannot update the texture with this buffered image! " + image);

        var buffer = Util.bufferedImageToBuffer(image);
        glBindTexture(GL_TEXTURE_2D, this.textureId);
        settings.applyToTexture(buffer);

        MemoryUtil.memFree(buffer);

    }

    public int getTexHeight() {
        return texHeight;
    }

    public int getTexWidth() {
        return texWidth;
    }

    public String getName() {
        return name;
    }

    public int getTextureId() {
        return textureId;
    }

    public void destroyTexture(){
        GL11.glDeleteTextures(textureId);
    }

}
