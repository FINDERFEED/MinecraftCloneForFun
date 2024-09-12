package com.finderfeed.world;

import com.finderfeed.blocks.Block;
import org.joml.Vector3i;

public interface WorldAccessor {

    Block getBlock(int x, int y, int z);

    int getHeight(int x,int z);

    default Block getBlock(Vector3i v){
        return this.getBlock(v.x,v.y,v.z);
    }

}
