package com.finderfeed.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Util {


    public static long coordsToLong(int x, int y){
        long l1 = x >= 0 ? 2L * x : -2L * x - 1;
        long l2 = y >= 0 ? 2L * y : -2L * y - 1;
        return l1 >= l2 ? l1 * l1 + l1 + l2 : l1 + l2 * l2;
    }

    public static <A,B> Function<A,B> cache(Function<A,B> func){
        return new Function<A, B>() {

            private final Map<A,B> cache = new HashMap<>();

            @Override
            public B apply(A a) {
                return cache.computeIfAbsent(a,func);
            }
        };
    }

}
