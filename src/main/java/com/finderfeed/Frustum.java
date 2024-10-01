package com.finderfeed;

import com.finderfeed.util.AABox;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class Frustum {

    public Matrix4f modelview;

    public Matrix4f projection;

    private FrustumIntersection intersection;

    public Frustum(Matrix4f modelview,Matrix4f projection){
        this.modelview = modelview;
        this.projection = projection;
        this.updateIntersection();
    }

    public Frustum(){
        this(new Matrix4f(),new Matrix4f());
    }

    public void setModelview(Matrix4f modelview) {
        this.modelview = modelview;
        this.updateIntersection();
    }

    public void setProjection(Matrix4f projection) {
        this.projection = projection;
        this.updateIntersection();
    }

    private void updateIntersection(){
        intersection = new FrustumIntersection(this.projection.mul(modelview,new Matrix4f()));
    }

    public boolean isVisible(AABox box){
         return intersection.testAab((float)box.minX,(float)box.minY,(float)box.minZ,(float)box.maxX,(float)box.maxY,(float)box.maxZ);
    }

    public boolean isVisible(float x,float y,float z){
         return intersection.testPoint(x,y,z);
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
