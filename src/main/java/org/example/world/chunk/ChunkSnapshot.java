package org.example.world.chunk;

import org.example.world.World;

import java.util.Arrays;

public class ChunkSnapshot extends Chunk {

    public ChunkSnapshot(Chunk chunk) {
        super(chunk.world, chunk.pos,false);
        if (chunk.blocks != null) {
            this.blocks = Arrays.copyOf(chunk.blocks, chunk.blocks.length);
        }
    }

    @Override
    public void generate() {

    }
}
