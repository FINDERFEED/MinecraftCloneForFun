package com.finderfeed.noise_combiner.layer_combiner;

import com.finderfeed.noise_combiner.FactoryRegistry;

import java.util.function.Supplier;

public class NoiseValueCombinerRegistry {

    public static final FactoryRegistry<FDNoiseValueCombiner> NOISE_VALUE_COMBINERS = new FactoryRegistry<>();

    public static final Supplier<SumValuesCombiner> SUM = NOISE_VALUE_COMBINERS.register("sum", SumValuesCombiner::new);
    public static final Supplier<SubtractValuesCombiner> SUBTRACT = NOISE_VALUE_COMBINERS.register("subtract", SubtractValuesCombiner::new);
    public static final Supplier<MultiplyValuesCombiner> MULTIPLY = NOISE_VALUE_COMBINERS.register("multiply", MultiplyValuesCombiner::new);
    public static final Supplier<DivideValuesCombiner> DIVIDE = NOISE_VALUE_COMBINERS.register("divide", DivideValuesCombiner::new);
    public static final Supplier<NoiseLerpValuesCombiner> NOISE_LERP = NOISE_VALUE_COMBINERS.register("noise_lerp", NoiseLerpValuesCombiner::new);

}
