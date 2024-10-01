package com.finderfeed.util;

import com.finderfeed.VertexBuffer;
import org.joml.Matrix4f;

public class RenderUtil {


    public static void renderBox(Matrix4f transform,VertexBuffer line,AABox box,float r,float g,float b,float a){

        float boxMinX = (float) box.minX;
        float boxMinY = (float) box.minY;
        float boxMinZ = (float) box.minZ;

        float boxMaxX = (float) box.maxX;
        float boxMaxY = (float) box.maxY;
        float boxMaxZ = (float) box.maxZ;

        line.position(transform,boxMinX,boxMinY,boxMinZ).color(r,g,b,a);
        line.position(transform,boxMaxX,boxMinY,boxMinZ).color(r,g,b,a);

        line.position(transform,boxMinX,boxMaxY,boxMinZ).color(r,g,b,a);
        line.position(transform,boxMaxX,boxMaxY,boxMinZ).color(r,g,b,a);

        line.position(transform,boxMinX,boxMinY,boxMaxZ).color(r,g,b,a);
        line.position(transform,boxMaxX,boxMinY,boxMaxZ).color(r,g,b,a);

        line.position(transform,boxMinX,boxMaxY,boxMaxZ).color(r,g,b,a);
        line.position(transform,boxMaxX,boxMaxY,boxMaxZ).color(r,g,b,a);


        line.position(transform,boxMinX,boxMinY,boxMinZ).color(r,g,b,a);
        line.position(transform,boxMinX,boxMinY,boxMaxZ).color(r,g,b,a);

        line.position(transform,boxMinX,boxMaxY,boxMinZ).color(r,g,b,a);
        line.position(transform,boxMinX,boxMaxY,boxMaxZ).color(r,g,b,a);

        line.position(transform,boxMaxX,boxMinY,boxMinZ).color(r,g,b,a);
        line.position(transform,boxMaxX,boxMinY,boxMaxZ).color(r,g,b,a);

        line.position(transform,boxMaxX,boxMaxY,boxMinZ).color(r,g,b,a);
        line.position(transform,boxMaxX,boxMaxY,boxMaxZ).color(r,g,b,a);


        line.position(transform,boxMinX,boxMinY,boxMinZ).color(r,g,b,a);
        line.position(transform,boxMinX,boxMaxY,boxMinZ).color(r,g,b,a);

        line.position(transform,boxMinX,boxMinY,boxMaxZ).color(r,g,b,a);
        line.position(transform,boxMinX,boxMaxY,boxMaxZ).color(r,g,b,a);

        line.position(transform,boxMaxX,boxMinY,boxMinZ).color(r,g,b,a);
        line.position(transform,boxMaxX,boxMaxY,boxMinZ).color(r,g,b,a);

        line.position(transform,boxMaxX,boxMinY,boxMaxZ).color(r,g,b,a);
        line.position(transform,boxMaxX,boxMaxY,boxMaxZ).color(r,g,b,a);

    }

}
