package org.example.world;

import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Objects;

public class ChunkPos {

    public final int x;
    public final int z;


    public ChunkPos(Vector3f pos){
        this.x = (int) pos.x >> WorldChunk.CHUNK_SIZE_SQRT;
        this.z = (int) pos.z >> WorldChunk.CHUNK_SIZE_SQRT;
    }
    public ChunkPos(Vector3d pos){
        this.x = (int) pos.x >> WorldChunk.CHUNK_SIZE_SQRT;
        this.z = (int) pos.z >> WorldChunk.CHUNK_SIZE_SQRT;
    }

    public ChunkPos(int x,int z){
        this.x = x;
        this.z = z;
    }

    public ChunkPos north(){
        return this.offset(0,1);
    }
    public ChunkPos south(){
        return this.offset(0,-1);
    }
    public ChunkPos west(){
        return this.offset(-1,0);
    }
    public ChunkPos east(){
        return this.offset(1,0);
    }


    public ChunkPos offset(int x,int z){
        return new ChunkPos(this.x + x,this.z + z);
    }

    public ChunkPos add(ChunkPos other){
        return new ChunkPos(this.x + other.x,this.z + other.z);
    }

    public ChunkPos subtract(ChunkPos other){
        return new ChunkPos(this.x - other.x,this.z - other.z);
    }

    public Vector2i normalPos(){
        return new Vector2i(
                x << WorldChunk.CHUNK_SIZE_SQRT,
                z << WorldChunk.CHUNK_SIZE_SQRT
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkPos chunkPos = (ChunkPos) o;
        return x == chunkPos.x && z == chunkPos.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }

    @Override
    public String toString() {
        return "ChunkPos{" +
                "x=" + x +
                ", z=" + z +
                '}';
    }
}
