package com.finderfeed.noise_combiner.noise.instances;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.noise.FDNoise;
import com.finderfeed.noise_combiner.noise.NoiseRegistry;
import com.finderfeed.noise_combiner.registry.ObjectType;
import com.finderfeed.util.MathUtil;
import org.joml.Vector3d;
import org.spongepowered.noise.NoiseQuality;
import org.spongepowered.noise.module.source.Perlin;

public class FDPerlinNoise extends FDNoise<FDPerlinNoise> {

    private Perlin perlin = new Perlin();

    public int octaves = Perlin.DEFAULT_PERLIN_OCTAVE_COUNT;
    public double lacunarity = Perlin.DEFAULT_PERLIN_LACUNARITY;
    public double frequency = Perlin.DEFAULT_PERLIN_FREQUENCY;
    public double persistence = Perlin.DEFAULT_PERLIN_PERSISTENCE;

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


        float value = (float) perlin.get(
                worldPos.x,
                worldPos.y,
                worldPos.z
        );


        return MathUtil.lerp(-1,1, MathUtil.clamp(value, 0, 1));
    }

    @Override
    public ObjectType<FDPerlinNoise> getObjectType() {
        return NoiseRegistry.PERLIN_NOISE;
    }

}
