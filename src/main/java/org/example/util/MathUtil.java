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

    public static int clamp(int val,int min,int max){
        if (val < min){
            return min;
        }else if (val > max){
            return max;
        }else{
            return val;
        }
    }

    public static float easeInOut(float p){
        if (p <= 0.5){
            return 2 * p * p;
        }else{
            return -2 * (1 - p) * (1 - p) + 1;
        }
    }


    public static boolean isValueBetween(int val,int down,int up){
        return !(val < down || val > up);
    }

    public static boolean isValueBetween(float val,float down,float up){
        return !(val < down || val > up);
    }

    public static float lerp(float x1,float x2,float p){
        return (x2 - x1) * p + x1;
    }

    public static double lerp(double x1,double x2,double p){
        return (x2 - x1) * p + x1;
    }

}
