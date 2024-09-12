package com.finderfeed.engine.shaders;

import com.finderfeed.VertexFormat;

public class Shaders {


    public static final Shader BLOCK = new Shader("block", VertexFormat.POSITION_COLOR_UV_NORMAL);
    public static final Shader POSITION_COLOR = new Shader("position_color", VertexFormat.POSITION_COLOR);
    public static final Shader POSITION_COLOR_UV = new Shader("position_color_uv", VertexFormat.POSITION_COLOR_UV);

    public static void init(){}

}
