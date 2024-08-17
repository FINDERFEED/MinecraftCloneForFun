package org.example.world;

import org.example.blocks.Block;
import org.example.util.Util;
import org.joml.Vector2i;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class World {


    public HashMap<Long,Chunk> chunkHashMap = new LinkedHashMap<>();

    public World(){

    }

    public void render(){
        for (Chunk c : chunkHashMap.values()){
            c.render(this);
        }
    }


    public Chunk getChunkAt(int x,int z,boolean generate){
        int cx = x >> Chunk.CHUNK_SIZE_SQRT;
        int cz = z >> Chunk.CHUNK_SIZE_SQRT;
        long pos = Util.coordsToLong(cx,cz);
        if (!chunkHashMap.containsKey(pos) && generate){
            Chunk chunk = new Chunk(this,new ChunkPos(cx,cz));
            chunk.generate();
            chunkHashMap.put(pos,chunk);
            return chunk;
        }else{
            return chunkHashMap.get(pos);
        }
    }

    public Block getBlock(int x,int y,int z,boolean generate){
        Chunk chunk = this.getChunkAt(x,z,generate);
        if (chunk == null || !isYInBounds(y)){
            return Block.NULL_AIR;
        }
        Vector2i v = chunk.pos.normalPos();
        return chunk.getBlock(x - v.x,y,z - v.y);
    }

    public Block getBlock(Vector3i vp,boolean generate){
        return this.getBlock(vp.x,vp.y,vp.z,generate);
    }

    public boolean isYInBounds(int y){
        return y >= 0 && y < Chunk.HEIGHT;
    }

}
