package com.finderfeed.noise_combiner.value_modifier;

import com.finderfeed.noise_combiner.registry.ObjectTypeRegistry;
import com.finderfeed.noise_combiner.registry.SimpleFactoryObjectType;
import com.finderfeed.noise_combiner.value_modifier.instances.*;

public class NoiseValueModifierRegistry {

    public static final ObjectTypeRegistry<SimpleFactoryObjectType<FDValueModifier<?>>, FDValueModifier<?>> VALUE_MODIFIERS = new ObjectTypeRegistry<>();

    public static final SimpleFactoryObjectType<AddValueModifier> ADD_VALUE = VALUE_MODIFIERS.register(new SimpleFactoryObjectType<>("add_value", AddValueModifier::new));
    public static final SimpleFactoryObjectType<SubtractValueModifier> SUBTRACT_VALUE = VALUE_MODIFIERS.register(new SimpleFactoryObjectType<>("subtract_value", SubtractValueModifier::new));
    public static final SimpleFactoryObjectType<MultiplyValueModifier> MULTIPLY_VALUE = VALUE_MODIFIERS.register(new SimpleFactoryObjectType<>("multiply_value", MultiplyValueModifier::new));
    public static final SimpleFactoryObjectType<DivideValueModifier> DIVIDE_VALUE = VALUE_MODIFIERS.register(new SimpleFactoryObjectType<>("divide_value", DivideValueModifier::new));
    public static final SimpleFactoryObjectType<AbsoluteModifier> ABS_VALUE = VALUE_MODIFIERS.register(new SimpleFactoryObjectType<>("abs_value", AbsoluteModifier::new));
    public static final SimpleFactoryObjectType<InvertModifier> INVERT_VALUE = VALUE_MODIFIERS.register(new SimpleFactoryObjectType<>("invert_value", InvertModifier::new));

}
