package com.finderfeed.noise_combiner;

import org.joml.Vector3d;

public class ComputationContext {

    private final Vector3d worldPos;
    private final int seed;

    public ComputationContext(Vector3d worldPos, int seed){
        this.worldPos = worldPos;
        this.seed = seed;
    }

    public int getSeed() {
        return seed;
    }

    public Vector3d getWorldPos() {
        return worldPos;
    }

}
