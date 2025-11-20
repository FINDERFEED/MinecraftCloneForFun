package com.finderfeed.menu.wrappers.layer_combiner_wrappers;

import com.finderfeed.menu.wrappers.layer_combiner_wrappers.instances.*;
import com.finderfeed.noise_combiner.layer_combiner.*;
import com.finderfeed.noise_combiner.layer_combiner.instances.*;
import com.finderfeed.noise_combiner.registry.ObjectTypeRegistry;

public class LayerCombinerWrapperRegistry {

    public static final ObjectTypeRegistry<LayerCombinerWrapperType<?,?>, LayerCombinerWrapper<?,?>> LAYER_COMBINER_WRAPPERS = new ObjectTypeRegistry<>();


    public static final LayerCombinerWrapperType<SumValuesCombinerWrapper, SumValuesCombiner> SUM_VALUES_COMBINER_WRAPPER = LAYER_COMBINER_WRAPPERS.register(new LayerCombinerWrapperType<>(
            "sum_values_combiner_wrapper", NoiseValueCombinerRegistry.SUM, SumValuesCombinerWrapper::new
    ));

    public static final LayerCombinerWrapperType<SubtractValuesCombinerWrapper, SubtractValuesCombiner> SUBTRACT_VALUES_COMBINER_WRAPPER = LAYER_COMBINER_WRAPPERS.register(new LayerCombinerWrapperType<>(
            "subtract_values_combiner_wrapper", NoiseValueCombinerRegistry.SUBTRACT, SubtractValuesCombinerWrapper::new
    ));

    public static final LayerCombinerWrapperType<MultiplyValuesCombinerWrapper, MultiplyValuesCombiner> MULTIPLY_VALUES_COMBINER_WRAPPER = LAYER_COMBINER_WRAPPERS.register(new LayerCombinerWrapperType<>(
            "multiply_values_combiner_wrapper", NoiseValueCombinerRegistry.MULTIPLY, MultiplyValuesCombinerWrapper::new
    ));

    public static final LayerCombinerWrapperType<DivideValuesCombinerWrapper, DivideValuesCombiner> DIVIDE_VALUES_COMBINER_WRAPPER = LAYER_COMBINER_WRAPPERS.register(new LayerCombinerWrapperType<>(
            "divide_values_values_combiner_wrapper", NoiseValueCombinerRegistry.DIVIDE, DivideValuesCombinerWrapper::new
    ));

    public static final LayerCombinerWrapperType<NoiseLerpValuesCombinerWrapper, NoiseLerpValuesCombiner> NOISE_LERP_VALUES_COMBINER_WRAPPER = LAYER_COMBINER_WRAPPERS.register(new LayerCombinerWrapperType<>(
            "noise_lerp_values_combiner_wrapper", NoiseValueCombinerRegistry.NOISE_LERP, NoiseLerpValuesCombinerWrapper::new
    ));


}
