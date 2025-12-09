package com.finderfeed.world.chunk;

import com.finderfeed.blocks.Block;
import com.finderfeed.world.ChunkStatus;
import com.finderfeed.world.World;

public abstract class Chunk implements AutoCloseable {


    public static final int CHUNK_SIZE_SQRT = 4;
    public static final int CHUNK_SIZE = CHUNK_SIZE_SQRT * CHUNK_SIZE_SQRT;
    public static final int HEIGHT = 256;

    public volatile World world;

    public ChunkStatus status = ChunkStatus.EMPTY;

    public ChunkPos pos;

    public short[] blocks;

    public short[] heightmap;

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
            this.heightmap = new short[CHUNK_SIZE * CHUNK_SIZE];
        }
    }

    public boolean isInitialized(){
        return blocks != null;
    }

    public abstract void generate();


    public int getHeight(int x,int z){
        return this.heightmap[x + z * CHUNK_SIZE];
    }

    public int getMaxHeightInChunk(){
        int maxHeight = 0;
        for (int i = 0; i < CHUNK_SIZE;i++){
            for (int g = 0; g < CHUNK_SIZE;g++){
                int height = this.getHeight(i,g);
                if (height > maxHeight){
                    maxHeight = height;
                }
            }
        }
        return maxHeight;
    }

    public void recalculateHeightAt(int x,int z){
        for (int y = HEIGHT - 1;y >= 0;y--){
            Block block = this.getBlock(x,y,z);
            if (!block.isAir()){
                this.setHeight(x,z,y);
                break;
            }
        }
    }

    public void setHeight(int x,int z,int height){
        this.heightmap[x + z * CHUNK_SIZE] = (short) height;
    }

    public void setBlock(Block block,int x, int y, int z){
        this.blocks[y * CHUNK_SIZE + z * (CHUNK_SIZE * HEIGHT) + x] = (short) block.registeredId;
    }

    public Block getBlock(int x,int y,int z){
        int id = this.blocks[y * CHUNK_SIZE + z * (CHUNK_SIZE * HEIGHT) + x];
        return Block.registeredBlocks.get(id);
    }

    public boolean isEmpty(){
        return status == ChunkStatus.EMPTY;
    }

    @Override
    public void close() {
        this.status = ChunkStatus.LOADED;
        this.changed = false;
    }

}
