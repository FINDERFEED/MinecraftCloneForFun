package org.example.world;

import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.Objects;

public class ChunkPos {

    public int x;
    public int y;


    public ChunkPos(Vector3f pos){
        this.x = (int) pos.x >> Chunk.CHUNK_SIZE_SQRT;
        this.y = (int) pos.z >> Chunk.CHUNK_SIZE_SQRT;
    }

    public ChunkPos(int x,int y){
        this.x = x;
        this.y = y;
    }


    public ChunkPos offset(int x,int y){
        return new ChunkPos(this.x + x,this.y + y);
    }

    public Vector2i normalPos(){
        return new Vector2i(
                x << Chunk.CHUNK_SIZE_SQRT,
                y << Chunk.CHUNK_SIZE_SQRT
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkPos chunkPos = (ChunkPos) o;
        return x == chunkPos.x && y == chunkPos.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "ChunkPos{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
