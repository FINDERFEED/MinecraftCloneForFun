package com.finderfeed.menu.wrappers.fdnoise_wrapper.instances;

import com.finderfeed.menu.wrappers.fdnoise_wrapper.NoiseWrapper;
import com.finderfeed.menu.wrappers.fdnoise_wrapper.NoiseWrapperRegistry;
import com.finderfeed.menu.wrappers.fdnoise_wrapper.NoiseWrapperType;
import com.finderfeed.noise_combiner.noise.instances.FDPerlinNoise;
import com.finderfeed.noise_combiner.noise.instances.FDRidgedNoise;
import com.finderfeed.util.Util;
import imgui.ImGui;
import imgui.type.ImDouble;
import org.spongepowered.noise.module.source.Perlin;

public class RidgedNoiseWrapper extends NoiseWrapper<RidgedNoiseWrapper, FDRidgedNoise> {

    public int[] octaves;
    public float[] lacunarity;
    public float[] frequency;
    public float[] offset;
    public float[] gain;

    public ImDouble xOffset;
    public ImDouble yOffset;
    public ImDouble zOffset;

    public RidgedNoiseWrapper(FDRidgedNoise noise) {
        super(noise);
        octaves = new int[]{noise.octaves};
        lacunarity = new float[]{(float)noise.lacunarity};
        frequency = new float[]{(float)noise.frequency};
        offset = new float[]{(float)noise.offset};
        gain = new float[]{(float)noise.gain};

        xOffset = new ImDouble(noise.xOffset);
        yOffset = new ImDouble(noise.yOffset);
        zOffset = new ImDouble(noise.zOffset);
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

        if (ImGui.sliderFloat("Gain", gain, 0.01f, 10)){
            object.gain = gain[0];
            hasChanged = true;
        }

        if (ImGui.sliderFloat("Noise offset", offset, 0.01f, 5)){
            object.offset = offset[0];
            hasChanged = true;
        }


        if (ImGui.inputDouble("X Offset", xOffset)){
            object.xOffset = xOffset.get();
            hasChanged = true;
        }
        if (ImGui.inputDouble("Y Offset", yOffset)){
            object.yOffset = yOffset.get();
            hasChanged = true;
        }
        if (ImGui.inputDouble("Z Offset", zOffset)){
            object.zOffset = zOffset.get();
            hasChanged = true;
        }

        if (hasChanged){
            this.changeListener.run();
        }

    }

    @Override
    public NoiseWrapperType<RidgedNoiseWrapper, FDRidgedNoise> type() {
        return NoiseWrapperRegistry.RIDGED_NOISE_WRAPPER;
    }

}