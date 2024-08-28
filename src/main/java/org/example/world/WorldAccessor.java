package org.example.world;

import org.example.blocks.Block;
import org.joml.Vector3i;

public interface WorldAccessor {

    Block getBlock(int x,int y,int z);

    default Block getBlock(Vector3i v){
        return this.getBlock(v.x,v.y,v.z);
    }

}
