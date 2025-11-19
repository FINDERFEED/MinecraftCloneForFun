package com.finderfeed.noise_combiner.value_modifier;

import com.finderfeed.noise_combiner.FactoryRegistry;

import java.util.function.Supplier;

public class NoiseValueModifierRegistry {

    public static final FactoryRegistry<FDValueModifier> VALUE_MODIFIERS = new FactoryRegistry<>();

    public static final Supplier<AddValueModifier> ADD_VALUE = VALUE_MODIFIERS.register("add_value", AddValueModifier::new);
    public static final Supplier<SubtractValueModifier> SUBTRACT_VALUE = VALUE_MODIFIERS.register("subtract_value", SubtractValueModifier::new);
    public static final Supplier<MultiplyValueModifier> MULTIPLY_VALUE = VALUE_MODIFIERS.register("multiply_value", MultiplyValueModifier::new);
    public static final Supplier<DivideValueModifier> DIVIDE_VALUE = VALUE_MODIFIERS.register("divide_value", DivideValueModifier::new);
    public static final Supplier<AbsoluteModifier> ABS_VALUE = VALUE_MODIFIERS.register("abs_value", AbsoluteModifier::new);
    public static final Supplier<InvertModifier> INVERT_VALUE = VALUE_MODIFIERS.register("invert_value", InvertModifier::new);

}
