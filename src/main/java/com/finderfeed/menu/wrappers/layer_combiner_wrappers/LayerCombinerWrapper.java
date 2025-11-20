package com.finderfeed.menu.wrappers.layer_combiner_wrappers;

import com.finderfeed.menu.wrappers.ObjectWrapper;
import com.finderfeed.noise_combiner.layer_combiner.FDNoiseValueCombiner;

public abstract class LayerCombinerWrapper<D extends LayerCombinerWrapper<D, T>, T extends FDNoiseValueCombiner<T>> extends ObjectWrapper<T> {

    public LayerCombinerWrapper(T object) {
        super(object);
    }

}
