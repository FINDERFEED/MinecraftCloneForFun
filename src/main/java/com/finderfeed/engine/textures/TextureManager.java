package com.finderfeed.engine.textures;

import java.util.HashMap;

public class TextureManager {

    private HashMap<String, Texture> textures = new HashMap<>();

    public Texture getTexture(String location){
        Texture texture = textures.get(location);
        if (texture == null){
            texture = new Texture(location);
            textures.put(location,texture);
        }
        return texture;
    }

    public void putTexture(String name,Texture texture){
        this.textures.put(name,texture);
    }

}
