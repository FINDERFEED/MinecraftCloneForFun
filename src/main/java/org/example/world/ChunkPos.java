package org.example.world;

import org.joml.Vector2i;
import org.joml.Vector3f;

public class ChunkPos {

    public int x;
    public int y;


    public ChunkPos(Vector3f pos){
        this.x = (int) pos.x >> Chunk.CHUNK_SIZE_SQRT;
        this.y = (int) pos.y >> Chunk.CHUNK_SIZE_SQRT;
    }

    public ChunkPos(int x,int y){
        this.x = x;
        this.y = y;
    }

    public Vector2i normalPos(){
        return new Vector2i(
                x << Chunk.CHUNK_SIZE_SQRT,
                y << Chunk.CHUNK_SIZE_SQRT
        );
    }

}
