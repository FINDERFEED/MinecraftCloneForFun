package com.finderfeed.util;


import org.joml.Vector3d;

public class RaytraceUtil {

    public static Vector3d traceBox(AABox box, Vector3d begin,Vector3d end){
        Vector3d closest = null;
        double distance = Double.MAX_VALUE;
        for (Face face : box.getFaces()){
            Vector3d p = traceSquare(face.p1(),face.p2(),face.p3(),face.p4(),begin,end);
            if (p != null){
                double d = p.distance(begin);
                if (d < distance){
                    distance = d;
                    closest = p;
                }
            }
        }
        return closest;
    }

    public static Vector3d traceSquare(Vector3d p1,Vector3d p2,Vector3d p3,Vector3d p4,Vector3d begin,Vector3d end) {
        Vector3d b1 = p1.sub(p2,new Vector3d());
        Vector3d b2 = p1.sub(p4,new Vector3d());
        Vector3d normal = b1.cross(b2,new Vector3d());
        Vector3d traced = traceInfinitePlane(p1,normal,begin,end);
        if (isPointOnLine(begin,end,traced,0.005f)){
            if (isPointInSquare(p1,p2,p3,p4,traced)){
                return traced;
            }
        }
        return null;
    }

    public static Vector3d traceInfinitePlane(Vector3d point,Vector3d normal,Vector3d lineP1,Vector3d lineP2){
        var c = getPlaneCoefficients(point,normal);
        return traceInfinitePlane(c[0],c[1],c[2],c[3],lineP1,lineP2);
    }

    public static Vector3d traceInfinitePlane(double A,double B,double C,double D,Vector3d lineP1,Vector3d lineP2){
        double x1 = lineP1.x;
        double x2 = lineP2.x;
        double y1 = lineP1.y;
        double y2 = lineP2.y;
        double z1 = lineP1.z;
        double z2 = lineP2.z;

        double y =
                ((-D - A * x1 - C * z1) * (y2 - y1) + A * y1 * (x2 - x1) + C * y1 * (z2 - z1)) /
                        (A * (x2 - x1) + B * (y2 - y1) + C * (z2 - z1));
        double x = (y - y1) / (y2 - y1) * (x2 - x1) + x1;
        double z = (y - y1) / (y2 - y1) * (z2 - z1) + z1;
        return new Vector3d(x,y,z);
    }



    //points should be like
    /*
    p2---->p3
    ↑      |
    |      |
    |      ↓
    p1<----p4
     */
    public static boolean isPointInSquare(Vector3d p1,Vector3d p2,Vector3d p3,Vector3d p4,Vector3d point){
        var pl1 = getPlaneCoefficients(p1,p2.sub(p1,new Vector3d()));
        var pl2 = getPlaneCoefficients(p2,p3.sub(p2,new Vector3d()));
        var pl3 = getPlaneCoefficients(p3,p4.sub(p3,new Vector3d()));
        var pl4 = getPlaneCoefficients(p4,p1.sub(p4,new Vector3d()));
        if (planeEquation(pl1,point) < 0){
            return false;
        }else if (planeEquation(pl2,point) < 0){
            return false;
        }else if (planeEquation(pl3,point) < 0){
            return false;
        }else if (planeEquation(pl4,point) < 0){
            return false;
        }else{
            return true;
        }
    }

    public static boolean isPointOnLine(Vector3d lineP1,Vector3d lineP2,Vector3d point,double accuracy){

        double length = lineP1.sub(lineP2,new Vector3d()).length();
        double l1 = point.sub(lineP1,new Vector3d()).length();
        double l2 = point.sub(lineP2,new Vector3d()).length();

        return (l1 + l2) <= (length + accuracy);
    }



    public static double[] getPlaneCoefficients(Vector3d point,Vector3d normal){
        double x1 = point.x;
        double y1 = point.y;
        double z1 = point.z;
        double A = normal.x;
        double B = normal.y;
        double C = normal.z;
        double D = (-normal.x * x1 - normal.y * y1 - normal.z * z1);
        return new double[]{A,B,C,D};
    }

    public static double planeEquation(double[] c,Vector3d point){
        return planeEquation(c[0],c[1],c[2],c[3],point);
    }
    public static double planeEquation(double A,double B,double C,double D,Vector3d point){
        return A * point.x + B * point.y + C * point.z + D;
    }

}
