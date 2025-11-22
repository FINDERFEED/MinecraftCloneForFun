package com.finderfeed.util;

import java.util.function.Function;

public enum EasingType {
    EASE_IN(MathUtil::easeIn),
    EASE_OUT(MathUtil::easeOut),
    EASE_IN_OUT(MathUtil::easeInOut)
    ;

    private Function<Float, Float> function;

    EasingType(Function<Float, Float> func){
        this.function = func;
    }

    public float transformValue(float p){
        return function.apply(p);
    }

}