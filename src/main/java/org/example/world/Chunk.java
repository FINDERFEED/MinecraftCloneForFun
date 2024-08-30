package org.example.world;

import org.example.blocks.Block;

public abstract class Chunk implements AutoCloseable {


    public static final int CHUNK_SIZE_SQRT = 4;
    public static final int CHUNK_SIZE = CHUNK_SIZE_SQRT * CHUNK_SIZE_SQRT;
    public static final int HEIGHT = 128;

    public volatile World world;

    public ChunkStatus status = ChunkStatus.EMPTY;

    public ChunkPos pos;

    public short[] blocks;

    public boolean changed = false;

    public Chunk(World world, ChunkPos pos){
        this.pos = pos;
        this.initIfNecessary();
        this.world = world;
    }

    public void initIfNecessary(){
        if (blocks == null){
            blocks = new short[HEIGHT * CHUNK_SIZE * CHUNK_SIZE];

            // x + y * i

            for (int y = 0; y < HEIGHT;y++){
                for (int x = 0; x < CHUNK_SIZE; x++){
                    for (int z = 0; z < CHUNK_SIZE; z++){


//                        blocks[y][x][z] = (short) Block.AIR.registeredId;
                        this.setBlock(Block.AIR,x,y,z);
                    }
                }
            }
        }
    }

    public boolean isInitialized(){
        return blocks != null;
    }

    public abstract void render(World world);


    public void setBlock(Block block,int x, int y, int z){
        this.blocks[y * CHUNK_SIZE + z * (CHUNK_SIZE * HEIGHT) + x] = (short) block.registeredId;
//        this.blocks[y][x][z] = (short) block.registeredId;
    }

    public Block getBlock(int x,int y,int z){
//        int id = this.blocks[y][x][z];
        int id = this.blocks[y * CHUNK_SIZE + z * (CHUNK_SIZE * HEIGHT) + x];
        return Block.registeredBlocks.get(id);
    }

    @Override
    public void close() {
        this.status = ChunkStatus.LOADED;
        this.changed = false;
    }

}
