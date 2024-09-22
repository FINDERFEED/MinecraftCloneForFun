package com.finderfeed.util;

import com.finderfeed.blocks.Block;
import com.finderfeed.blocks.Side;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class BlockRayTraceResult {

    public Side side;
    public Block block;
    public Vector3d pos;
    public Vector3i blockPos;

    public BlockRayTraceResult(Side side,Vector3i blockPos, Block block, Vector3d pos){
        this.block = block;
        this.pos = pos;
        this.blockPos = blockPos;
        this.side = side;
    }

    @Override
    public String toString() {
        return "Block: " + block + " at pos X: " + pos.x + " Y: " + pos.y + " Z: " + pos.z;
    }
}
