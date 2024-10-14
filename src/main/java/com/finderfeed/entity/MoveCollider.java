package com.finderfeed.entity;

import com.finderfeed.util.AABox;

public class MoveCollider {

    public static final int X_COLLIDING = 0;
    public static final int Y_COLLIDING = 1;
    public static final int Z_COLLIDING = 2;

    public int type;
    public AABox box;
    public double wall;

    public MoveCollider(int type,double wallCoord,AABox box){
        this.type = type;
        this.box = box;
        this.wall = wallCoord;
    }

}
