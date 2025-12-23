package com.finderfeed.noise_combiner.noise.instances;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.noise.FDNoise;
import com.finderfeed.noise_combiner.noise.NoiseRegistry;
import com.finderfeed.noise_combiner.registry.ObjectType;
import com.finderfeed.util.MathUtil;
import com.google.gson.JsonObject;
import org.joml.Vector3d;
import org.spongepowered.noise.NoiseQuality;
import org.spongepowered.noise.module.source.Perlin;

public class FDPerlinNoise extends FDNoise<FDPerlinNoise> {

    private Perlin perlin = new Perlin();

    public int octaves = Perlin.DEFAULT_PERLIN_OCTAVE_COUNT;
    public double lacunarity = Perlin.DEFAULT_PERLIN_LACUNARITY;
    public double frequency = Perlin.DEFAULT_PERLIN_FREQUENCY;
    public double persistence = Perlin.DEFAULT_PERLIN_PERSISTENCE;
    public double xOffset = 0;
    public double yOffset = 0;
    public double zOffset = 0;

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
                worldPos.x + xOffset,
                worldPos.y + yOffset,
                worldPos.z + zOffset
        );


        return MathUtil.lerp(-1,1, MathUtil.clamp(value, 0, 1));
    }

    @Override
    public ObjectType<FDPerlinNoise> getObjectType() {
        return NoiseRegistry.PERLIN_NOISE;
    }

    @Override
    public void serializeToJson(JsonObject object) {
        object.addProperty("octaves", this.octaves);
        object.addProperty("lacunarity", this.lacunarity);
        object.addProperty("frequency", this.frequency);
        object.addProperty("persistence", this.persistence);
        object.addProperty("xOffset", this.xOffset);
        object.addProperty("yOffset", this.yOffset);
        object.addProperty("zOffset", this.zOffset);
    }

    @Override
    public void deserializeFromJson(JsonObject jsonObject) {
        this.octaves = jsonObject.get("octaves").getAsInt();
        this.lacunarity = jsonObject.get("lacunarity").getAsDouble();
        this.frequency = jsonObject.get("frequency").getAsDouble();
        this.persistence = jsonObject.get("persistence").getAsDouble();
        this.xOffset = jsonObject.get("xOffset").getAsDouble();
        this.yOffset = jsonObject.get("yOffset").getAsDouble();
        this.zOffset = jsonObject.get("zOffset").getAsDouble();
    }
}
