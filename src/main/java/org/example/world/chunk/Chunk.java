package org.example.world.chunk;

import org.example.blocks.Block;
import org.example.world.ChunkStatus;
import org.example.world.World;

public abstract class Chunk implements AutoCloseable {


    public static final int CHUNK_SIZE_SQRT = 4;
    public static final int CHUNK_SIZE = CHUNK_SIZE_SQRT * CHUNK_SIZE_SQRT;
    public static final int HEIGHT = 128;

    public volatile World world;

    public ChunkStatus status = ChunkStatus.EMPTY;

    public ChunkPos pos;

    public short[] blocks;

    public boolean changed = false;

    public Chunk(World world, ChunkPos pos,boolean init){
        this.pos = pos;
        this.world = world;
        if (init) {
            this.initIfNecessary();
        }
    }

    public void initIfNecessary(){
        if (blocks == null){
            blocks = new short[HEIGHT * CHUNK_SIZE * CHUNK_SIZE];
            for (int y = 0; y < HEIGHT;y++){
                for (int x = 0; x < CHUNK_SIZE; x++){
                    for (int z = 0; z < CHUNK_SIZE; z++){
                        this.setBlock(Block.AIR,x,y,z);
                    }
                }
            }
        }
    }

    public boolean isInitialized(){
        return blocks != null;
    }

    public abstract void generate();


    public void setBlock(Block block,int x, int y, int z){
        this.blocks[y * CHUNK_SIZE + z * (CHUNK_SIZE * HEIGHT) + x] = (short) block.registeredId;
    }

    public Block getBlock(int x,int y,int z){
        int id = this.blocks[y * CHUNK_SIZE + z * (CHUNK_SIZE * HEIGHT) + x];
        return Block.registeredBlocks.get(id);
    }

    @Override
    public void close() {
        this.status = ChunkStatus.LOADED;
        this.changed = false;
    }

}
