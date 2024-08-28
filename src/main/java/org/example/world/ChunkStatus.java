package org.example.world;

public enum ChunkStatus{
    EMPTY(0),
    GENERATING(1),
    GENERATED(2),
    COMPILING(3),
    FULL(4)

    ;


    public final int value;

    ChunkStatus(int val){
        this.value = val;
    }

}
