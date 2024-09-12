package com.finderfeed.engine.immediate_buffer_supplier;

import com.finderfeed.Main;
import com.finderfeed.VertexFormat;
import com.finderfeed.engine.shaders.Shaders;
import com.finderfeed.engine.textures.Texture;
import com.finderfeed.engine.textures.TextureManager;
import com.finderfeed.util.Util;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RenderOptions {

    private static final Function<Texture, RenderOptions> TEXT = Util.cache((texture)->{
        return new RenderOptions(DrawMode.QUADS,VertexFormat.POSITION_COLOR_UV)
                .add(new ShaderRenderOption(Shaders.POSITION_COLOR_UV))
                .add(new TextureRenderOption(texture))
                .add(RenderOption.CULL)
                .add(RenderOption.BLEND)
                .add(RenderOption.DEFAULT_DEPTH_TEST);
    });

    public static RenderOptions DEFAULT_LINES = new RenderOptions(DrawMode.LINES,VertexFormat.POSITION_COLOR)
            .add(new ShaderRenderOption(Shaders.POSITION_COLOR))
            .add(new RenderOption(()->{GL11.glLineWidth(4);},()->{}));

    public static RenderOptions texture(String texture){
        TextureManager manager = Main.textureManager;
        Texture t = manager.getTexture(texture);
        return TEXT.apply(t);
    }



    private List<RenderOption> renderOptions = new ArrayList<>();

    private DrawMode mode;
    private VertexFormat format;

    public RenderOptions(DrawMode mode,VertexFormat format){
        this.format = format;
        this.mode = mode;
    }

    public RenderOptions add(RenderOption option){
        this.renderOptions.add(option);
        return this;
    }

    public VertexFormat getFormat() {
        return format;
    }

    public DrawMode getDrawMode() {
        return mode;
    }

    public void run(){
        for (RenderOption option : renderOptions){
            option.run();
        }
    }

    public void clear(){
        for (RenderOption option : renderOptions){
            option.clear();
        }
    }



}
