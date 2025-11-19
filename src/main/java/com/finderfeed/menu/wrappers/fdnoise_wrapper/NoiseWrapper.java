package com.finderfeed.menu.wrappers.fdnoise_wrapper;

import com.finderfeed.menu.wrappers.ObjectWrapper;
import com.finderfeed.noise_combiner.noise.FDNoise;

public abstract class NoiseWrapper<D extends NoiseWrapper<D, T>, T extends FDNoise<T>> extends ObjectWrapper<T> {

    public NoiseWrapper(T noise){
        super(noise);
    }

    @Override
    public void renderWrappedObject() {
        this.render();
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public void close() {
        super.close();
    }


    public abstract void render();

    public abstract NoiseWrapperType<D, T> type();

}
