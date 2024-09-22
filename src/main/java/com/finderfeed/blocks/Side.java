package com.finderfeed.blocks;

import org.joml.Vector2i;
import org.joml.Vector3i;

public enum Side {
    TOP(0,1,0),
    BOTTOM(0,-1,0),
    NORTH(0,0,-1),
    EAST(1,0,0),
    WEST(-1,0,0),
    SOUTH(0,0,1);

    Vector3i normal;

    Side(int x,int y,int z){
        this.normal = new Vector3i(x,y,z);
    }


    public Vector3i getNormal() {
        return new Vector3i(normal);
    }
}
