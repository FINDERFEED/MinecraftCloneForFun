package com.finderfeed;

import com.finderfeed.noise_combiner.NoiseCombination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlobalWorldParameters {

    private static final HashMap<Object, Runnable> globalWorldParametersChangeListeners = new HashMap<>();

    private static double coordinateScale = 123.334f;
    private static int seed = 524652324;
    private static double noiseScale = 1.0d;
    private static int worldHeight = 100;

    private static NoiseCombination currentNoiseCombination = new NoiseCombination();

    public static NoiseCombination getCurrentNoiseCombination() {
        return currentNoiseCombination;
    }

    public static double getCoordinateScale() {
        return coordinateScale;
    }

    public static double getNoiseScale() {
        return noiseScale;
    }

    public static int getSeed() {
        return seed;
    }

    public static void setCoordinateScale(double coordinateScale) {
        GlobalWorldParameters.coordinateScale = coordinateScale;
        triggerGlobalParametersChanged();
    }

    public static void setNoiseScale(double noiseScale) {
        GlobalWorldParameters.noiseScale = noiseScale;
        triggerGlobalParametersChanged();
    }

    public static void setSeed(int seed) {
        GlobalWorldParameters.seed = seed;
        triggerGlobalParametersChanged();
    }

    private static void triggerGlobalParametersChanged(){
        for (var listener : globalWorldParametersChangeListeners.entrySet()){
            listener.getValue().run();
        }
    }

    public static void removeListener(Object object){
        globalWorldParametersChangeListeners.remove(object);
    }

    public static void addGlobalParameterChangeListener(Object o, Runnable action){
        globalWorldParametersChangeListeners.put(o, action);
    }

    public static int getWorldHeight() {
        return worldHeight;
    }

}
