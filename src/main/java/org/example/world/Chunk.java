package org.example.world;

import org.example.blocks.Block;

public abstract class Chunk implements AutoCloseable {


    public static final int CHUNK_SIZE_SQRT = 4;
    public static final int CHUNK_SIZE = CHUNK_SIZE_SQRT * CHUNK_SIZE_SQRT;
    public static final int HEIGHT = 128;

    public volatile World world;

    public ChunkStatus status = ChunkStatus.EMPTY;

    public ChunkPos pos;

    public short[][][] blocks = new short[HEIGHT][CHUNK_SIZE][CHUNK_SIZE];

    public boolean changed = false;

    public Chunk(World world, ChunkPos pos){
        this.pos = pos;
        for (int i = 0; i < HEIGHT;i++){
            for (int g = 0; g < CHUNK_SIZE; g++){
                for (int g2 = 0; g2 < CHUNK_SIZE; g2++){
                    blocks[i][g][g2] = (short) Block.AIR.registeredId;
                }
            }
        }
        this.world = world;
    }


    public abstract void render(World world);


    public void setBlock(Block block,int x, int y, int z){
        this.blocks[y][x][z] = (short) block.registeredId;
    }

    public Block getBlock(int x,int y,int z){
        int id = this.blocks[y][x][z];
        return Block.registeredBlocks.get(id);
    }

    @Override
    public void close() {
        this.status = ChunkStatus.LOADED;
        this.changed = false;
    }

}
