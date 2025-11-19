package com.finderfeed.util;

import org.lwjgl.system.MemoryUtil;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Util {

    //Type of image - INT_ARGB
    public static ByteBuffer bufferedImageToBuffer(BufferedImage image){
        DataBufferInt dataBufferInt = (DataBufferInt) image.getRaster().getDataBuffer();
        int[] data = dataBufferInt.getData();
        ByteBuffer buffer = MemoryUtil.memAlloc(Integer.BYTES * data.length);
        //0xaarrggbb
        for (int col : data){
            int r = (col & 0x00ff0000) >> 16;
            int g = (col & 0x0000ff00);
            int b = (col & 0x000000ff);
            int a = (col & 0xff000000);
            col = a + (b << 16) + g + r;
            buffer.putInt(
                    col
            );
        }
        buffer.flip();

        return buffer;
    }

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
