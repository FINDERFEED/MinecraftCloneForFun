package org.example;

import org.example.util.AABox;
import org.joml.Matrix4f;
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
        if (isEdgeVisible(p1,p2)){
            return true;
        }
        var p3 = performTransform(box.minX,box.maxY,box.minZ);
        var p4 = performTransform(box.maxX,box.maxY,box.minZ);
        if (isEdgeVisible(p3,p4) || isEdgeVisible(p1,p3) || isEdgeVisible(p2,p4)){
            return true;
        }
        var p5 = performTransform(box.minX,box.minY,box.maxZ);
        var p6 = performTransform(box.maxX,box.minY,box.maxZ);
        if (isEdgeVisible(p2,p6) || isEdgeVisible(p1,p5) || isEdgeVisible(p5,p6)){
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
        return
                !(
                        (v1.x < -1 && v2.x < -1 || v1.x > 1 && v2.x > 1) ||
                                (v1.y < -1 && v2.y < -1 || v1.y > 1 && v2.y > 1) ||
                                (v1.z < 0 && v2.z < 0 || v1.z > 1 && v2.z > 1)

                );
    }

    private Vector4f performTransform(float x, float y, float z){
        Vector4f v = new Vector4f(x,y,z,1f);
        modelview.transform(v);
        projection.transform(v);
        v.x /= v.w;
        v.y /= v.w;
        v.z /= v.w;
        return v;
    }



}
