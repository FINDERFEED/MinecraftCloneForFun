package com.finderfeed.util;

import com.finderfeed.blocks.Block;
import org.joml.Vector3d;
import org.joml.Vector3f;

public record BlockRayTraceResult(Block block, Vector3d pos) {


    @Override
    public String toString() {
        return "Block: " + block + " at pos X: " + pos.x + " Y: " + pos.y + " Z: " + pos.z;
    }
}
