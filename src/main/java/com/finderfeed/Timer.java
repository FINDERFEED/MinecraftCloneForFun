package com.finderfeed;

public class Timer {

    public static final int TICKS_PER_SECOND = 20;
    public static final int MILLISECONDS_PER_TICK = 1000 / TICKS_PER_SECOND;

    public int ticksElapsed = 0;
    public float partialTick = 0;

    private long previousTime = -1;


    /**
     * Returns how much ticks should game process
     */
    public int advanceTime(){
        long time = System.currentTimeMillis();
        if (previousTime == -1){
            previousTime = time;
            return 1;
        }
        int millisPassed = (int) (time - previousTime);
        int tickAmount = millisPassed / MILLISECONDS_PER_TICK;

        int millisTickAmount = tickAmount * MILLISECONDS_PER_TICK;

        ticksElapsed += tickAmount;
        previousTime += millisTickAmount;

        partialTick = (millisPassed - millisTickAmount) / (float) MILLISECONDS_PER_TICK;

        return tickAmount;
    }
}
