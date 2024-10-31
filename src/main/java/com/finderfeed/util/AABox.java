package com.finderfeed.util;

import com.finderfeed.blocks.Side;
import org.joml.Math;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.List;

public class AABox {

    public double minX;
    public double minY;
    public double minZ;

    public double maxX;
    public double maxY;
    public double maxZ;

    public AABox(double bx,double by,double bz,double ex,double ey,double ez){
        this.minX = Math.min(bx,ex);
        this.minY = Math.min(by,ey);
        this.minZ = Math.min(bz,ez);
        this.maxX = Math.max(bx,ex);
        this.maxY = Math.max(by,ey);
        this.maxZ = Math.max(bz,ez);
    }

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
    public AABox(Vector3d begin,Vector3d end){
        this(begin.x,begin.y,begin.z,end.x,end.y,end.z);
    }

    public Vector3d center(){
        return new Vector3d(
                minX + (maxX - minX) / 2,
                minY + (maxY - minY) / 2,
                minZ + (maxZ - minZ) / 2
        );
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
    public AABox offset(double x,double y,double z){
        return new AABox(
                this.minX + x,
                this.minY + y,
                this.minZ + z,
                this.maxX + x,
                this.maxY + y,
                this.maxZ + z
        );
    }

    public AABox inflate(double value){
        return this.inflate(value,value,value);
    }

    public AABox inflate(double x,double y,double z){
        return new AABox(
                this.minX - x,
                this.minY - y,
                this.minZ - z,
                this.maxX + x,
                this.maxY + y,
                this.maxZ + z
        );
    }


    public double getXRadius(){
        return (maxX - minX) / 2;
    }

    public double getYRadius(){
        return (maxY - minY) / 2;
    }

    public double getZRadius(){
        return (maxZ - minZ) / 2;
    }

    public List<Face> getFaces(){
        return List.of(
                new Face(
                        Side.NORTH,
                        new Vector3d(minX,minY,minZ),
                        new Vector3d(minX,maxY,minZ),
                        new Vector3d(maxX,maxY,minZ),
                        new Vector3d(maxX,minY,minZ)
                ),
                new Face(
                        Side.WEST,
                        new Vector3d(minX,minY,minZ),
                        new Vector3d(minX,maxY,minZ),
                        new Vector3d(minX,maxY,maxZ),
                        new Vector3d(minX,minY,maxZ)
                ),
                new Face(
                        Side.SOUTH,
                        new Vector3d(minX,minY,maxZ),
                        new Vector3d(minX,maxY,maxZ),
                        new Vector3d(maxX,maxY,maxZ),
                        new Vector3d(maxX,minY,maxZ)
                ),
                new Face(
                        Side.EAST,
                        new Vector3d(maxX,minY,minZ),
                        new Vector3d(maxX,maxY,minZ),
                        new Vector3d(maxX,maxY,maxZ),
                        new Vector3d(maxX,minY,maxZ)
                ),
                new Face(
                        Side.BOTTOM,
                        new Vector3d(minX,minY,minZ),
                        new Vector3d(maxX,minY,minZ),
                        new Vector3d(maxX,minY,maxZ),
                        new Vector3d(minX,minY,maxZ)
                ),
                new Face(
                        Side.TOP,
                        new Vector3d(minX,maxY,minZ),
                        new Vector3d(maxX,maxY,minZ),
                        new Vector3d(maxX,maxY,maxZ),
                        new Vector3d(minX,maxY,maxZ)
                )
        );
    }
}
