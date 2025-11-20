package com.finderfeed.noise_combiner.layer_combiner;

import com.finderfeed.noise_combiner.layer_combiner.instances.*;
import com.finderfeed.noise_combiner.registry.ObjectTypeRegistry;
import com.finderfeed.noise_combiner.registry.SimpleFactoryObjectType;

public class NoiseValueCombinerRegistry {

    public static final ObjectTypeRegistry<SimpleFactoryObjectType<FDNoiseValueCombiner<?>>, FDNoiseValueCombiner<?>> NOISE_VALUE_COMBINERS = new ObjectTypeRegistry<>();

    public static final SimpleFactoryObjectType<SumValuesCombiner> SUM = NOISE_VALUE_COMBINERS.register(new SimpleFactoryObjectType<>("sum", SumValuesCombiner::new));
    public static final SimpleFactoryObjectType<SubtractValuesCombiner> SUBTRACT = NOISE_VALUE_COMBINERS.register(new SimpleFactoryObjectType<>("subtract", SubtractValuesCombiner::new));
    public static final SimpleFactoryObjectType<MultiplyValuesCombiner> MULTIPLY = NOISE_VALUE_COMBINERS.register(new SimpleFactoryObjectType<>("multiply", MultiplyValuesCombiner::new));
    public static final SimpleFactoryObjectType<DivideValuesCombiner> DIVIDE = NOISE_VALUE_COMBINERS.register(new SimpleFactoryObjectType<>("divide", DivideValuesCombiner::new));
    public static final SimpleFactoryObjectType<NoiseLerpValuesCombiner> NOISE_LERP = NOISE_VALUE_COMBINERS.register(new SimpleFactoryObjectType<>("noise_lerp", NoiseLerpValuesCombiner::new));

}
