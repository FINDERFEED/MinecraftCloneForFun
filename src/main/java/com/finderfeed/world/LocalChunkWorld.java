package com.finderfeed.world;

import com.finderfeed.blocks.Block;
import com.finderfeed.util.Util;
import com.finderfeed.world.chunk.Chunk;
import com.finderfeed.world.chunk.ChunkPos;
import com.finderfeed.world.chunk.ChunkSnapshot;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class LocalChunkWorld implements WorldAccessor {


    public HashMap<Long, Chunk> chunkHashMap = new LinkedHashMap<>();

    public LocalChunkWorld(World world, ChunkPos pos, int chunkRadius){
        List<Chunk> chunks = world.chunks.getChunksInSquareRadius(pos,null,chunkRadius,true);
        for (Chunk c : chunks){
            long p = Util.coordsToLong(c.pos.x,c.pos.z);
            c = new ChunkSnapshot(c);
            chunkHashMap.put(p,c);
        }
    }


    @Override
    public Block getBlock(int x, int y, int z) {
        if (!World.isYInBounds(y)) return Block.NULL_AIR;

        int xc = x >> 4;
        int zc = z >> 4;

        long c = Util.coordsToLong(xc,zc);
        if (!chunkHashMap.containsKey(c)){
            throw new RuntimeException("Cannot get chunk as it doesn't exist in current context: [" + xc + "," + zc +"]");
        }
        Chunk chunk = chunkHashMap.get(c);
        int lxpos = x - xc * 16;
        int lzpos = z - zc * 16;
        return chunk.getBlock(lxpos,y,lzpos);
    }

    @Override
    public int getHeight(int x, int z) {

        int xc = x >> 4;
        int zc = z >> 4;

        long c = Util.coordsToLong(xc,zc);
        if (!chunkHashMap.containsKey(c)){
            throw new RuntimeException("Cannot get chunk as it doesn't exist in current context: [" + xc + "," + zc +"]");
        }
        Chunk chunk = chunkHashMap.get(c);
        int lxpos = x - xc * 16;
        int lzpos = z - zc * 16;
        return chunk.getHeight(lxpos,lzpos);
    }
}
