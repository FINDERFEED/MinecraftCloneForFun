package com.finderfeed.menu.wrappers.fdnoise_wrapper.instances;

import com.finderfeed.menu.wrappers.fdnoise_wrapper.NoiseWrapper;
import com.finderfeed.menu.wrappers.fdnoise_wrapper.NoiseWrapperRegistry;
import com.finderfeed.menu.wrappers.fdnoise_wrapper.NoiseWrapperType;
import com.finderfeed.noise_combiner.noise.FDConstantValueNoise;
import imgui.ImGui;
import imgui.type.ImFloat;

public class ConstantValueNoiseWrapper extends NoiseWrapper<ConstantValueNoiseWrapper, FDConstantValueNoise> {

    private ImFloat constantValue;

    public ConstantValueNoiseWrapper(FDConstantValueNoise noise) {
        super(noise);
        this.constantValue = new ImFloat(noise.constantValue);
    }

    @Override
    public void render() {
        var object = this.getObject();
        if (ImGui.inputFloat("Constant value", constantValue)){
            object.constantValue = this.constantValue.get();
            this.changeListener.run();
        }
    }

    @Override
    public NoiseWrapperType<ConstantValueNoiseWrapper, FDConstantValueNoise> type() {
        return NoiseWrapperRegistry.CONSTANT_VALUE_NOISE_WRAPPER;
    }

}
