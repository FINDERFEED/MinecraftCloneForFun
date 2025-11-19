package com.finderfeed.menu.wrappers.fdnoise_wrapper.instances;

import com.finderfeed.menu.wrappers.fdnoise_wrapper.NoiseWrapper;
import com.finderfeed.menu.wrappers.fdnoise_wrapper.NoiseWrapperRegistry;
import com.finderfeed.menu.wrappers.fdnoise_wrapper.NoiseWrapperType;
import com.finderfeed.noise_combiner.noise.FDPerlinNoise;
import com.finderfeed.util.Util;
import imgui.ImGui;
import org.spongepowered.noise.module.source.Perlin;

public class PerlinNoiseWrapper extends NoiseWrapper<PerlinNoiseWrapper, FDPerlinNoise> {

    public int[] octaves;
    public float[] lacunarity;
    public float[] frequency;
    public float[] persistence;

    public PerlinNoiseWrapper(FDPerlinNoise noise) {
        super(noise);
        octaves = new int[]{noise.octaves};
        lacunarity = new float[]{(float)noise.lacunarity};
        frequency = new float[]{(float)noise.frequency};
        persistence = new float[]{(float)noise.persistence};
    }

    @Override
    public void render() {

        boolean hasChanged = false;

        var object = this.getObject();

        if (ImGui.sliderInt("Octaves", octaves, 1, Perlin.PERLIN_MAX_OCTAVE)){
            object.octaves = octaves[0];
            hasChanged = true;
        }

        if (ImGui.sliderFloat("Lacunarity", lacunarity, 0.1f, 10)){
            object.lacunarity = lacunarity[0];
            hasChanged = true;
        }
        Util.insertSimpleTooltip("Frequency multiplier between successive octaves");

        if (ImGui.sliderFloat("Frequency", frequency, 0.01f, 10)){
            object.frequency = frequency[0];
            hasChanged = true;
        }

        if (ImGui.sliderFloat("Persistence", this.persistence, 0.01f, 2)){
            object.persistence = this.persistence[0];
            hasChanged = true;
        }
        Util.insertSimpleTooltip("Roughness of noise, best results between 0 and 1");

        if (hasChanged){
            this.changeListener.run();
        }

    }

    @Override
    public NoiseWrapperType<PerlinNoiseWrapper, FDPerlinNoise> type() {
        return NoiseWrapperRegistry.PERLIN_NOISE_WRAPPER;
    }

}
