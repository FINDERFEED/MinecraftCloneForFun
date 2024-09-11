package org.example;

import org.example.util.AABox;
import org.example.util.MathUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Frustum {

    public Matrix4f modelview;

    public Matrix4f projection;

    public Frustum(Matrix4f modelview,Matrix4f projection){
        this.modelview = modelview;
        this.projection = projection;
    }

    public void setModelview(Matrix4f modelview) {
        this.modelview = modelview;
    }

    public void setProjection(Matrix4f projection) {
        this.projection = projection;
    }

    public boolean isVisible(AABox box){

        var p1 = performTransform(box.minX,box.minY,box.minZ);
        var p2 = performTransform(box.maxX,box.minY,box.minZ);
        boolean res1 = isEdgeVisible(p1,p2);
        if (res1){
            return true;
        }
        var p3 = performTransform(box.minX,box.maxY,box.minZ);
        var p4 = performTransform(box.maxX,box.maxY,box.minZ);
        boolean res2 = isEdgeVisible(p3,p4);
        boolean res3 = isEdgeVisible(p1,p3);
        boolean res4 = isEdgeVisible(p2,p4);
        if (res2 || res3 || res4){
            return true;
        }
        var p5 = performTransform(box.minX,box.minY,box.maxZ);
        var p6 = performTransform(box.maxX,box.minY,box.maxZ);
        boolean res5 = isEdgeVisible(p2,p6);
        boolean res6 = isEdgeVisible(p1,p5);
        boolean res7 = isEdgeVisible(p5,p6);
        if (res5 || res6 || res7){
            return true;
        }
        var p7 = performTransform(box.minX,box.maxY,box.maxZ);
        var p8 = performTransform(box.maxX,box.maxY,box.maxZ);
        if (
                isEdgeVisible(p4,p8) || isEdgeVisible(p3,p7) ||
                        isEdgeVisible(p7,p8) || isEdgeVisible(p5,p7) || isEdgeVisible(p6,p8)
        ){
            return true;
        }

        return false;
    }

    private boolean isEdgeVisible(Vector4f v1, Vector4f v2){
        float w1 = Math.abs(v1.w);
        float w2 = Math.abs(v2.w);

        boolean i1 = !(v1.x < w1 && v2.x < w2 || v1.x > w1 && v2.x > w2);
        boolean i2 = !(v1.y < w1 && v2.y < w2 || v1.y > w1 && v2.y > w2);
        boolean i3 = !(v1.z < w1 && v2.z < w2 || v1.z > w1 && v2.z > w2);

        return this.isPointVisible(v1) || this.isPointVisible(v2) ||
                i1 &&
                i2 &&
                i3;
    }

    private boolean isPointVisible(Vector4f point){
        float w = Math.abs(point.w);
        return
                (point.x <= w && point.x >= -w) &&
                (point.y <= w && point.y >= -w) &&
                (point.z <= w && point.z >= -w);
    }

    private Vector4f performTransform(float x, float y, float z){
        Vector4f v = new Vector4f(x,y,z,1f);
        modelview.transform(v);
        projection.transform(v);
        return v;
    }



}
