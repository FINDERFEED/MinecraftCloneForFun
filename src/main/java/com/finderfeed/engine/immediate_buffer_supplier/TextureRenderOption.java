package com.finderfeed.engine.immediate_buffer_supplier;

import com.finderfeed.engine.textures.Texture;

public class TextureRenderOption extends RenderOption{

    public TextureRenderOption(Texture texture) {
        super(()->{
            texture.bind(0);
        },()->{

        });
    }
}
