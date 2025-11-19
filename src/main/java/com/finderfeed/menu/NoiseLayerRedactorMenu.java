package com.finderfeed.menu;

import com.finderfeed.menu.wrappers.fdnoise_wrapper.FDNoiseWrapper;
import com.finderfeed.menu.wrappers.fdnoise_wrapper.FDNoiseWrapperRegistry;
import com.finderfeed.menu.wrappers.fdnoise_wrapper.NoiseWrapperType;
import com.finderfeed.noise_combiner.NoiseLayer;
import com.finderfeed.noise_combiner.noise.FDNoise;

public class NoiseLayerRedactorMenu extends Menu {

    private NoiseLayer noiseLayer;

    private FDNoiseWrapper<?,?> noiseWrapper;

    public NoiseLayerRedactorMenu(String menuTitle, NoiseLayer noiseLayer) {
        super(menuTitle);
        this.noiseLayer = noiseLayer;
        var noise = this.noiseLayer.getNoise();
        this.initNoiseWrapper(noise);
    }

    @Override
    public void renderMenuContents() {
        noiseWrapper.renderWrappedObject();
    }

    @Override
    public void onOpen() {

    }

    @Override
    public void onClose() {
        noiseWrapper.close();
    }

    private void initNoiseWrapper(FDNoise<?> fdNoise){
        var type = fdNoise.getObjectType();

        FDNoiseWrapper<?,?> noiseWrapper = null;

        for (var wrapperType : FDNoiseWrapperRegistry.NOISE_WRAPPERS.getObjectTypes()){
            if (type == wrapperType.getNoiseObjectType()){
                noiseWrapper = this.useWrapperType(wrapperType, fdNoise);
                break;
            }
        }

        if (noiseWrapper == null) throw new RuntimeException("No wrapper is registered for noise type: " + type);

        if (this.noiseWrapper != null){
            this.noiseWrapper.close();
        }

        this.noiseWrapper = noiseWrapper;
        noiseWrapper.initialize();

    }

    private <T extends FDNoiseWrapper<T, D>, D extends FDNoise<D>> FDNoiseWrapper<T, D> useWrapperType(NoiseWrapperType<T, D> noiseWrapperType, FDNoise<?> fdNoise){
        return noiseWrapperType.generateWrapper((D) fdNoise);
    }

}
