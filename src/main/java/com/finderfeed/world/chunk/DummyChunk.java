package com.finderfeed.world.chunk;

import com.finderfeed.blocks.Block;
import com.finderfeed.world.World;

public class DummyChunk extends Chunk {

    public DummyChunk(World world, ChunkPos pos) {
        super(world, pos,false);
    }

    @Override
    public void generate() {

    }

    @Override
    public Block getBlock(int x, int y, int z) {
        return Block.AIR;
    }
}
