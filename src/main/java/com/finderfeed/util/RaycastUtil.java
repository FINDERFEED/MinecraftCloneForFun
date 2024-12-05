/*
 *
 * Copyright (C) 2024 FINDERFEED
 *
 *  All rights reserved.
 *
 */
package com.finderfeed.util;


import com.finderfeed.blocks.Side;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.LinkedHashSet;
import java.util.Set;

public class RaycastUtil {


    public static Set<Vector3i> voxelRaycast(Vector3d begin, Vector3d end){
        Set<Vector3i> tracePath = new LinkedHashSet<>();
        Vector3d between = end.sub(begin,new Vector3d());
        Vector3d nb = between.normalize(new Vector3d());
        Vector3d current = begin;
        for (int i = 0; i <= between.length();i++){
            Vector3d next = current.add(nb,new Vector3d());

            Vector3i b = new Vector3i(
                    (int)Math.floor(current.x),
                    (int)Math.floor(current.y),
                    (int)Math.floor(current.z)
            );
            Vector3i e = new Vector3i(
                    (int)Math.floor(next.x),
                    (int)Math.floor(next.y),
                    (int)Math.floor(next.z)
            );
            Vector3i diff = e.sub(b,new Vector3i());
            //add beginning
            tracePath.add(b);
            //if positions are equal to each other just continue
            if (b.equals(e)) {
                current = next;
                continue;
            }else if (Math.abs(diff.x) + Math.abs(diff.y) + Math.abs(diff.z) == 1){
                // if we moved only in 1 direction just continue
                current = next;
                continue;
            }

            if (diff.y == 0){
                //movement on x and z axis

                double fx = Math.floor(current.x);
                double fz = Math.floor(current.z);

                double cx = diff.x > 0 ? fx : fx + 1;
                double cz = diff.z > 0 ? fz : fz + 1;

                double x1 = Math.abs(current.x - cx);
                double z1 = Math.abs(current.z - cz);

                double x2 = Math.abs(next.x - cx);
                double z2 = Math.abs(next.z - cz);

                double v = (1 - z1) * (x2 - x1) - (1 - x1) * (z2 - z1);
                if (v > 0){
                    tracePath.add(new Vector3i(
                            b.x + diff.x,
                            b.y,
                            b.z
                    ));
                }else{
                    tracePath.add(new Vector3i(
                            b.x,
                            b.y,
                            b.z + diff.z
                    ));
                }

                current = next;
            }else{
                if (diff.x == 0){
                    double fz = Math.floor(current.z);
                    double fy = Math.floor(current.y);

                    double cz = diff.z > 0 ? fz : fz + 1;
                    double cy = diff.y > 0 ? fy : fy + 1;

                    double z1 = Math.abs(current.z - cz);
                    double y1 = Math.abs(current.y - cy);

                    double z2 = Math.abs(next.z - cz);
                    double y2 = Math.abs(next.y - cy);

                    double v = (1 - y1) * (z2 - z1) - (1 - z1) * (y2 - y1);
                    if (v > 0){
                        tracePath.add(new Vector3i(
                                b.x,
                                b.y,
                                b.z + diff.z
                        ));
                    }else{
                        tracePath.add(new Vector3i(
                                b.x,
                                b.y + diff.y,
                                b.z
                        ));
                    }
                }else if (diff.z == 0){
                    double fx = Math.floor(current.x);
                    double fy = Math.floor(current.y);

                    double cx = diff.x > 0 ? fx : fx + 1;
                    double cy = diff.y > 0 ? fy : fy + 1;

                    double x1 = Math.abs(current.x - cx);
                    double y1 = Math.abs(current.y - cy);

                    double x2 = Math.abs(next.x - cx);
                    double y2 = Math.abs(next.y - cy);

                    double v = (1 - y1) * (x2 - x1) - (1 - x1) * (y2 - y1);
                    if (v < 0){
                        tracePath.add(new Vector3i(
                                b.x,
                                b.y + diff.y,
                                b.z
                        ));
                    }else{
                        tracePath.add(new Vector3i(
                                b.x + diff.x,
                                b.y,
                                b.z
                        ));
                    }

                }else{
                    //pizdets

                    double fx = Math.floor(current.x);
                    double fy = Math.floor(current.y);
                    double fz = Math.floor(current.z);

                    double cx = diff.x > 0 ? fx : fx + 1;
                    double cy = diff.y > 0 ? fy : fy + 1;
                    double cz = diff.z > 0 ? fz : fz + 1;

                    double x1 = Math.abs(current.x - cx);
                    double y1 = Math.abs(current.y - cy);
                    double z1 = Math.abs(current.z - cz);

                    double x2 = Math.abs(next.x - cx);
                    double y2 = Math.abs(next.y - cy);
                    double z2 = Math.abs(next.z - cz);

                    double vyx = (1 - y1) * (x2 - x1) - (1 - x1) * (y2 - y1);
                    double vyz = (1 - y1) * (z2 - z1) - (1 - z1) * (y2 - y1);


                    double vxz = (1 - z1) * (x2 - x1) - (1 - x1) * (z2 - z1);

                    if (vxz > 0){
                        //x

                        if (vyx < 0) {

                            tracePath.add(new Vector3i(
                                    b.x,
                                    b.y + diff.y,
                                    b.z
                            ));

                            tracePath.add(new Vector3i(
                                    b.x + diff.x,
                                    b.y + diff.y,
                                    b.z
                            ));

                        }else{
                            tracePath.add(new Vector3i(
                                    b.x + diff.x,
                                    b.y,
                                    b.z
                            ));
                            if (vyz > 0){
                                tracePath.add(new Vector3i(
                                        b.x + diff.x,
                                        b.y,
                                        b.z + diff.z
                                ));
                            }else{
                                tracePath.add(new Vector3i(
                                        b.x + diff.x,
                                        b.y + diff.y,
                                        b.z
                                ));
                            }

                        }


                    }else{
                        //z

                        if (vyz < 0) {

                            tracePath.add(new Vector3i(
                                    b.x,
                                    b.y + diff.y,
                                    b.z
                            ));

                            tracePath.add(new Vector3i(
                                    b.x,
                                    b.y + diff.y,
                                    b.z + diff.z
                            ));

                        }else{
                            tracePath.add(new Vector3i(
                                    b.x,
                                    b.y,
                                    b.z + diff.z
                            ));
                            if (vyx > 0){
                                tracePath.add(new Vector3i(
                                        b.x + diff.x,
                                        b.y,
                                        b.z + diff.z
                                ));
                            }else{
                                tracePath.add(new Vector3i(
                                        b.x,
                                        b.y + diff.y,
                                        b.z + diff.z
                                ));
                            }

                        }
                    }

                }
                current = next;

            }
        }
        tracePath.add(new Vector3i(
                (int)Math.floor(end.x),
                (int)Math.floor(end.y),
                (int)Math.floor(end.z)
        ));
        return tracePath;
    }



    public static Pair<Side,Vector3d> traceBox(AABox box, Vector3d begin, Vector3d end){
        Vector3d closest = null;
        Side side = null;
        double distance = Double.MAX_VALUE;
        for (Face face : box.getFaces()){
            Vector3d p = traceSquare(face.p1(),face.p2(),face.p3(),face.p4(),begin,end);
            if (p != null){
                double d = p.distance(begin);
                if (d < distance){
                    distance = d;
                    closest = p;
                    side = face.normal();
                }
            }
        }
        if (closest != null){
            return new ObjectObjectImmutablePair<>(side,closest);
        }else{
            return null;
        }
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
                ((-D - A * x1 - C * z1) * (y2 - y1 + 0.0001f) + A * y1 * (x2 - x1) + C * y1 * (z2 - z1)) /
                        (A * (x2 - x1) + B * (y2 - y1 + 0.0001f) + C * (z2 - z1));
        double x = (y - y1) / (y2 - y1 + 0.0001f) * (x2 - x1) + x1;
        double z = (y - y1) / (y2 - y1 + 0.0001f) * (z2 - z1) + z1;
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
