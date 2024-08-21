package org.example.util.noises;

import org.joml.Random;
import org.joml.SimplexNoise;

public class Simplex3D implements Noise{


    public float offsetX;
    public float offsetY;
    public float offsetZ;

    public Simplex3D(int seed){
        Random random = new Random(seed);
        offsetX = random.nextFloat() * 1000 - 500;
        offsetY = random.nextFloat() * 1000 - 500;
        offsetZ = random.nextFloat() * 1000 - 500;

    }

    @Override
    public float get(float x, float y, float z) {
        return SimplexNoise.noise(
                x + offsetX,
                y + offsetY,
                z + offsetZ
        );
    }
}
