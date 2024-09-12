package com.finderfeed.engine.immediate_buffer_supplier;

import com.finderfeed.engine.shaders.Shader;

public class ShaderRenderOption extends RenderOption {

    public ShaderRenderOption(Shader shader) {
        super(()->{
            shader.run();
        },()->{
            shader.clear();
        });
    }
}
