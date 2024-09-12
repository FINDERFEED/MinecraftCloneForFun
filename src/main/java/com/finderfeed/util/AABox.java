package com.finderfeed.util;

import org.joml.Math;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.List;

public class AABox {

    public float minX;
    public float minY;
    public float minZ;

    public float maxX;
    public float maxY;
    public float maxZ;

    public AABox(float bx,float by,float bz,float ex,float ey,float ez){
        this.minX = Math.min(bx,ex);
        this.minY = Math.min(by,ey);
        this.minZ = Math.min(bz,ez);
        this.maxX = Math.max(bx,ex);
        this.maxY = Math.max(by,ey);
        this.maxZ = Math.max(bz,ez);
    }

    public AABox(Vector3f begin,Vector3f end){
        this(begin.x,begin.y,begin.z,end.x,end.y,end.z);
    }


    public AABox offset(float x,float y,float z){
        return new AABox(
                this.minX + x,
                this.minY + y,
                this.minZ + z,
                this.maxX + x,
                this.maxY + y,
                this.maxZ + z
        );
    }

    public List<Face> getFaces(){
        return List.of(
                new Face(
                        new Vector3d(minX,minY,minZ),
                        new Vector3d(minX,maxY,minZ),
                        new Vector3d(maxX,maxY,minZ),
                        new Vector3d(maxX,minY,minZ)
                ),
                new Face(
                        new Vector3d(minX,minY,minZ),
                        new Vector3d(minX,maxY,minZ),
                        new Vector3d(minX,maxY,maxZ),
                        new Vector3d(minX,minY,maxZ)
                ),
                new Face(
                        new Vector3d(minX,minY,maxZ),
                        new Vector3d(minX,maxY,maxZ),
                        new Vector3d(maxX,maxY,maxZ),
                        new Vector3d(maxX,minY,maxZ)
                ),
                new Face(
                        new Vector3d(maxX,minY,minZ),
                        new Vector3d(maxX,maxY,minZ),
                        new Vector3d(maxX,maxY,maxZ),
                        new Vector3d(maxX,minY,maxZ)
                ),
                new Face(
                        new Vector3d(minX,minY,minZ),
                        new Vector3d(maxX,minY,minZ),
                        new Vector3d(maxX,minY,maxZ),
                        new Vector3d(minX,minY,maxZ)
                ),
                new Face(
                        new Vector3d(minX,maxY,minZ),
                        new Vector3d(maxX,maxY,minZ),
                        new Vector3d(maxX,maxY,maxZ),
                        new Vector3d(minX,maxY,maxZ)
                )
        );
    }
}
