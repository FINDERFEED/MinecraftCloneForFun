package com.finderfeed.menu.wrappers.value_modifier_wrappers;

import com.finderfeed.menu.wrappers.value_modifier_wrappers.instances.*;
import com.finderfeed.noise_combiner.registry.ObjectTypeRegistry;
import com.finderfeed.noise_combiner.value_modifier.*;
import com.finderfeed.noise_combiner.value_modifier.instances.*;

public class ValueModifierWrapperRegistry {

    public static final ObjectTypeRegistry<ValueModifierWrapperType<?,?>, ValueModifierWrapper<?,?>> VALUE_MODIFIER_WRAPPERS = new ObjectTypeRegistry<>();

    public static final ValueModifierWrapperType<AbsoluteModifierWrapper, AbsoluteModifier> ABSOLUTE_MODIFIER_WRAPPER = VALUE_MODIFIER_WRAPPERS.register(new ValueModifierWrapperType<>(
            "absolute_modifier_wrapper", NoiseValueModifierRegistry.ABS_VALUE, AbsoluteModifierWrapper::new
    ));

    public static final ValueModifierWrapperType<AddValueModifierWrapper, AddValueModifier> ADD_VALUE_MODIFIER = VALUE_MODIFIER_WRAPPERS.register(new ValueModifierWrapperType<>(
            "add_valuer_modifier_wrapper", NoiseValueModifierRegistry.ADD_VALUE, AddValueModifierWrapper::new
    ));

    public static final ValueModifierWrapperType<SubtractValueModifierWrapper, SubtractValueModifier> SUBTRACT_VALUE_MODIFIER = VALUE_MODIFIER_WRAPPERS.register(new ValueModifierWrapperType<>(
            "subtract_value_modifier_wrapper", NoiseValueModifierRegistry.SUBTRACT_VALUE,SubtractValueModifierWrapper::new
    ));

    public static final ValueModifierWrapperType<MultiplyValueModifierWrapper, MultiplyValueModifier> MULTIPLY_VALUE_MODIFIER = VALUE_MODIFIER_WRAPPERS.register(new ValueModifierWrapperType<>(
            "multiply_value_modifier_wrapper", NoiseValueModifierRegistry.MULTIPLY_VALUE, MultiplyValueModifierWrapper::new
    ));

    public static final ValueModifierWrapperType<DivideByValueModifierWrapper, DivideValueModifier> DIVIDE_BY_VALUE_MODIFIER = VALUE_MODIFIER_WRAPPERS.register(new ValueModifierWrapperType<>(
            "divide_value_modifier_wrapper", NoiseValueModifierRegistry.DIVIDE_VALUE, DivideByValueModifierWrapper::new
    ));

    public static final ValueModifierWrapperType<InvertValueModifierWrapper, InvertModifier> INVERT_VALUE_MODIFIER = VALUE_MODIFIER_WRAPPERS.register(new ValueModifierWrapperType<>(
            "invert_value_modifier_wrapper", NoiseValueModifierRegistry.INVERT_VALUE, InvertValueModifierWrapper::new
    ));

    public static final ValueModifierWrapperType<PowModifierWrapper, PowModifier> POWER_VALUE_MODIFIER = VALUE_MODIFIER_WRAPPERS.register(new ValueModifierWrapperType<>(
            "power_value_modifier_wrapper", NoiseValueModifierRegistry.POW_VALUE, PowModifierWrapper::new
    ));

    public static final ValueModifierWrapperType<ClampModifierWrapper, ClampModifier> CLAMP_VALUE_MODIFIER = VALUE_MODIFIER_WRAPPERS.register(new ValueModifierWrapperType<>(
            "clamp_value_modifier_wrapper", NoiseValueModifierRegistry.CLAMP_VALUE, ClampModifierWrapper::new
    ));

    public static final ValueModifierWrapperType<NoiseLerpModifierWrapper, NoiseLerpValueModifier> NOISE_LERP_VALUE_MODIFIER = VALUE_MODIFIER_WRAPPERS.register(new ValueModifierWrapperType<>(
            "noise_lerp_value_modifier_wrapper", NoiseValueModifierRegistry.NOISE_LERP_VALUE, NoiseLerpModifierWrapper::new
    ));

    public static final ValueModifierWrapperType<ApplyEasingValueModifierWrapper, EasingValueModifier> APPLY_EASING_MODIFIER = VALUE_MODIFIER_WRAPPERS.register(new ValueModifierWrapperType<>(
            "apply_easing_modifier_wrapper", NoiseValueModifierRegistry.APPLY_EASING, ApplyEasingValueModifierWrapper::new
    ));

}
