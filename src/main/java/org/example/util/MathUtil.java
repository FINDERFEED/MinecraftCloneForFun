package org.example.util;

public class MathUtil {


    public static float clamp(float val,float min,float max){
        if (val < min){
            return min;
        }else if (val > max){
            return max;
        }else{
            return val;
        }
    }

}
