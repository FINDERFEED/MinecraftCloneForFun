package com.finderfeed.util;

import com.finderfeed.blocks.Block;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class BlockRayTraceResult {

    public Block block;
    public Vector3d pos;
    public Vector3i blockPos;

    public BlockRayTraceResult(Block block, Vector3d pos){
        this.block = block;
        this.pos = pos;
        this.blockPos = new Vector3i(
                (int) Math.floor(pos.x),
                (int) Math.floor(pos.y),
                (int) Math.floor(pos.z)
        );
    }

    @Override
    public String toString() {
        return "Block: " + block + " at pos X: " + pos.x + " Y: " + pos.y + " Z: " + pos.z;
    }
}
