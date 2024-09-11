package org.example.util;

import org.example.VertexBuffer;
import org.joml.Matrix4f;

public class RenderUtil {


    public static void renderBox(Matrix4f transform,VertexBuffer line,AABox box,float r,float g,float b,float a){

        line.position(transform,box.minX,box.minY,box.minZ).color(r,g,b,a);
        line.position(transform,box.maxX,box.minY,box.minZ).color(r,g,b,a);

        line.position(transform,box.minX,box.maxY,box.minZ).color(r,g,b,a);
        line.position(transform,box.maxX,box.maxY,box.minZ).color(r,g,b,a);

        line.position(transform,box.minX,box.minY,box.maxZ).color(r,g,b,a);
        line.position(transform,box.maxX,box.minY,box.maxZ).color(r,g,b,a);

        line.position(transform,box.minX,box.maxY,box.maxZ).color(r,g,b,a);
        line.position(transform,box.maxX,box.maxY,box.maxZ).color(r,g,b,a);


        line.position(transform,box.minX,box.minY,box.minZ).color(r,g,b,a);
        line.position(transform,box.minX,box.minY,box.maxZ).color(r,g,b,a);

        line.position(transform,box.minX,box.maxY,box.minZ).color(r,g,b,a);
        line.position(transform,box.minX,box.maxY,box.maxZ).color(r,g,b,a);

        line.position(transform,box.maxX,box.minY,box.minZ).color(r,g,b,a);
        line.position(transform,box.maxX,box.minY,box.maxZ).color(r,g,b,a);

        line.position(transform,box.maxX,box.maxY,box.minZ).color(r,g,b,a);
        line.position(transform,box.maxX,box.maxY,box.maxZ).color(r,g,b,a);


        line.position(transform,box.minX,box.minY,box.minZ).color(r,g,b,a);
        line.position(transform,box.minX,box.maxY,box.minZ).color(r,g,b,a);

        line.position(transform,box.minX,box.minY,box.maxZ).color(r,g,b,a);
        line.position(transform,box.minX,box.maxY,box.maxZ).color(r,g,b,a);

        line.position(transform,box.maxX,box.minY,box.minZ).color(r,g,b,a);
        line.position(transform,box.maxX,box.maxY,box.minZ).color(r,g,b,a);

        line.position(transform,box.maxX,box.minY,box.maxZ).color(r,g,b,a);
        line.position(transform,box.maxX,box.maxY,box.maxZ).color(r,g,b,a);

    }

}
