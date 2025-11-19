package com.finderfeed.menu.wrappers.fdnoise_wrapper;

import com.finderfeed.noise_combiner.noise.FDConstantValueNoise;
import com.finderfeed.noise_combiner.noise.NoiseRegistry;
import com.finderfeed.noise_combiner.registry.ObjectType;
import imgui.ImGui;
import imgui.type.ImFloat;

public class FDConstantValueNoiseWrapper extends FDNoiseWrapper<FDConstantValueNoiseWrapper, FDConstantValueNoise> {

    private ImFloat constantValue;

    public FDConstantValueNoiseWrapper(FDConstantValueNoise noise) {
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
    public NoiseWrapperType<FDConstantValueNoiseWrapper, FDConstantValueNoise> type() {
        return FDNoiseWrapperRegistry.CONSTANT_VALUE_NOISE_WRAPPER;
    }

}
