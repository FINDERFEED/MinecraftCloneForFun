package com.finderfeed.util.noises;

import org.joml.Random;
import org.joml.Vector3f;

public class Perlin3D implements Noise {

    public static final int[][] GRADIENTS3D = new int[][]{
            {1,0,0},
            {-1,0,0},
            {0,0,1},
            {0,0,-1},
            {0,1,0},
            {0,-1,0},
            {1,1,1},
            {-1,1,1},
            {-1,1,-1},
            {-1,1,-1},
            {1,-1,1},
            {-1,-1,1},
            {-1,-1,-1},
            {-1,-1,-1},
    };

    public int[] p;


    public Perlin3D(int seed){
        p = new int[512];
        Random random = new Random(seed);
        for (int i = 0; i < 256;i++){
            p[i] = i;
        }
        for (int i = 0; i < 256;i++){
            int ri = random.nextInt(256);
            int c = p[ri];
            p[ri] = p[i];
            p[i] = c;
        }
        for (int i = 256; i < 512;i++){
            p[i] = p[i - 256];
        }
    }



    public float get(float x,float y,float z){
        int xi = (int)Math.floor(x);
        int yi = (int)Math.floor(y);
        int zi = (int)Math.floor(z);
        float lx = x - xi;
        float ly = y - yi;
        float lz = z - zi;
        xi = xi & 255;
        yi = yi & 255;
        zi = zi & 255;
        var lllv = vec(xi,yi,zi,0,0,0);
        var tllv = vec(xi,yi,zi,1,0,0);
        var lltv = vec(xi,yi,zi,0,0,1);
        var tltv = vec(xi,yi,zi,1,0,1);
        var ltlv = vec(xi,yi,zi,0,1,0);
        var ttlv = vec(xi,yi,zi,1,1,0);
        var lttv = vec(xi,yi,zi,0,1,1);
        var tttv = vec(xi,yi,zi,1,1,1);

        var llld = dot(lx,ly,lz,lllv);
        var tlld = dot(lx - 1,ly,lz,tllv);
        var lltd = dot(lx,ly,lz - 1,lltv);
        var tltd = dot(lx - 1,ly,lz - 1,tltv);
        var ltld = dot(lx,ly - 1,lz,ltlv);
        var ttld = dot(lx - 1,ly - 1,lz,ttlv);
        var lttd = dot(lx,ly - 1,lz - 1,lttv);
        var tttd = dot(lx - 1,ly - 1,lz - 1,tttv);

        lx = fade(lx);
        ly = fade(ly);
        lz = fade(lz);

        float f1 = lerp(lx,llld,tlld);
        float f2 = lerp(lx,lltd,tltd);
        float f3 = lerp(lx,ltld,ttld);
        float f4 = lerp(lx,lttd,tttd);

        float f5 = lerp(lz,f1,f2);
        float f6 = lerp(lz,f3,f4);

        float f7 = lerp(ly,f5,f6);

        return f7;
    }

    private float lerp(float p,float v1,float v2){
        return v1 + (v2 - v1) * p;
    }

    private float dot(float lx,float ly,float lz,int[] vec){
        Vector3f v = new Vector3f(vec[0],vec[1],vec[2]).normalize();
        return v.dot(new Vector3f(lx,ly,lz).normalize());
    }

    private float dot(float lx,float ly,float lz,Vector3f vec){
        Vector3f v = vec.normalize(new Vector3f());
        return v.dot(new Vector3f(lx,ly,lz));
    }

    private int[] vec(int x,int y,int z,int xa,int ya,int za){
        int f1 = p[x + xa];
        int f2 = p[f1 + y + ya];
        int f3 = p[f2 + z + za];
        return GRADIENTS3D[f3 & (GRADIENTS3D.length-1)];
    }

    private static float fade(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

}
