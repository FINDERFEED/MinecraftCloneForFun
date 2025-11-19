package com.finderfeed.noise_combiner.noise;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.registry.ObjectType;
import org.joml.Vector3d;
import org.spongepowered.noise.Noise;
import org.spongepowered.noise.NoiseQuality;
import org.spongepowered.noise.module.source.Perlin;

public class FDPerlinNoise extends FDNoise<FDPerlinNoise> {

    private Perlin perlin = new Perlin();

    private int octaves = Perlin.DEFAULT_PERLIN_OCTAVE_COUNT;
    private double lacunarity = Perlin.DEFAULT_PERLIN_LACUNARITY;
    private double frequency = Perlin.DEFAULT_PERLIN_FREQUENCY;
    private double persistence = Perlin.DEFAULT_PERLIN_PERSISTENCE;

    public FDPerlinNoise(){

    }

    @Override
    public float computeNoiseValue(ComputationContext computationContext) {

        perlin.setNoiseQuality(NoiseQuality.STANDARD);
        perlin.setOctaveCount(octaves);
        perlin.setSeed(computationContext.getSeed());
        perlin.setLacunarity(lacunarity);
        perlin.setFrequency(frequency);
        perlin.setPersistence(persistence);

        Vector3d worldPos = computationContext.getWorldPos();

        return (float) perlin.get(
                worldPos.x,
                worldPos.y,
                worldPos.z
        );

    }

    @Override
    public ObjectType<FDPerlinNoise> getObjectType() {
        return NoiseRegistry.PERLIN_NOISE;
    }

}
