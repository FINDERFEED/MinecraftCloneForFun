package com.finderfeed.noise_combiner.noise.instances;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.noise.FDNoise;
import com.finderfeed.noise_combiner.noise.NoiseRegistry;
import com.finderfeed.noise_combiner.registry.ObjectType;
import com.finderfeed.util.MathUtil;
import org.joml.Vector3d;
import org.spongepowered.noise.Noise;
import org.spongepowered.noise.NoiseQuality;
import org.spongepowered.noise.Utils;
import org.spongepowered.noise.module.source.Perlin;
import org.spongepowered.noise.module.source.RidgedMulti;

public class FDRidgedNoise extends FDNoise<FDRidgedNoise> {

    public int octaves = RidgedMulti.DEFAULT_RIDGED_OCTAVE_COUNT;
    public double lacunarity = RidgedMulti.DEFAULT_RIDGED_LACUNARITY;
    private double previousLacunarity = lacunarity;
    public double frequency = RidgedMulti.DEFAULT_RIDGED_FREQUENCY;
    public double offset = 1.0;
    public double gain = 2.0;

    public double xOffset = 0;
    public double yOffset = 0;
    public double zOffset = 0;

    private double[] spectralWeights;

    public FDRidgedNoise(){
        this.calcSpectralWeights();
    }

    @Override
    public float computeNoiseValue(ComputationContext computationContext) {

        if (previousLacunarity != lacunarity){
            this.calcSpectralWeights();
            previousLacunarity = lacunarity;
        }

        Vector3d worldPos = computationContext.getWorldPos();

        float value = (float) this.get(
                worldPos.x + xOffset,
                worldPos.y + yOffset,
                worldPos.z + zOffset,
                computationContext.getSeed()
        );


        return MathUtil.lerp(-1,1, MathUtil.clamp(value, 0, 1));
    }

    private void calcSpectralWeights() {
        final double h = 1.0;
        double frequency = 1.0;
        this.spectralWeights = new double[RidgedMulti.RIDGED_MAX_OCTAVE];
        for (int i = 0; i < RidgedMulti.RIDGED_MAX_OCTAVE; i++) {
            this.spectralWeights[i] = Math.pow(frequency, -h);
            frequency *= this.lacunarity;
        }
    }

    private double get(final double x, final double y, final double z, int noiseSeed) {
        double x1 = x;
        double y1 = y;
        double z1 = z;
        x1 *= this.frequency;
        y1 *= this.frequency;
        z1 *= this.frequency;

        double signal;
        double value = 0.0;
        double weight = 1.0;

        for (int curOctave = 0; curOctave < octaves; curOctave++) {


            final double nx;
            final double ny;
            final double nz;
            nx = Utils.makeInt32Range(x1);
            ny = Utils.makeInt32Range(y1);
            nz = Utils.makeInt32Range(z1);

            final int seed = (noiseSeed + curOctave) & 0x7fffffff;
            signal = Noise.gradientCoherentNoise3D(nx, ny, nz, seed, NoiseQuality.STANDARD) * 2 - 1;

            signal = Math.abs(signal);
            signal = offset - signal;

            signal *= signal;

            signal *= weight;

            weight = signal * gain;
            if (weight > 1.0) {
                weight = 1.0;
            }
            if (weight < 0.0) {
                weight = 0.0;
            }

            value += (signal * this.spectralWeights[curOctave]);

            x1 *= this.lacunarity;
            y1 *= this.lacunarity;
            z1 *= this.lacunarity;
        }

        return value / 1.6;
    }

    @Override
    public ObjectType<FDRidgedNoise> getObjectType() {
        return NoiseRegistry.RIDGED_NOISE;
    }

}
