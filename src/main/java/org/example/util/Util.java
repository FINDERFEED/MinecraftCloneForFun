package org.example.util;

public class Util {


    public static long coordsToLong(int x, int y){
        long l1 = x >= 0 ? 2L * x : -2L * x - 1;
        long l2 = y >= 0 ? 2L * y : -2L * y - 1;
        return l1 >= l2 ? l1 * l1 + l1 + l2 : l1 + l2 * l2;
    }

}
