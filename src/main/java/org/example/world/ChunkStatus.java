package org.example.world;

public enum ChunkStatus{
    EMPTY(0),
    LOADING(1),
    LOADED(2),
    COMPILING(3),
    FULL(4)

    ;


    public final int value;

    ChunkStatus(int val){
        this.value = val;
    }

}
