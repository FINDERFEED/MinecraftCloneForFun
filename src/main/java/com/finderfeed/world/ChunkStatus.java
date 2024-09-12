package com.finderfeed.world;

public enum ChunkStatus{
    EMPTY(0),
    LOADING(1),
    LOADED(2),
    FULL(3)

    ;


    public final int value;

    ChunkStatus(int val){
        this.value = val;
    }

}
