package org.example.world;

import org.example.VertexBuffer;
import org.example.VertexFormat;
import org.example.blocks.Block;
import org.joml.Random;
import org.joml.Vector2i;

public class Chunk implements AutoCloseable{

    private static final Random r = new Random(32323232);

    public static final int CHUNK_SIZE_SQRT = 4;
    public static final int CHUNK_SIZE = CHUNK_SIZE_SQRT * CHUNK_SIZE_SQRT;
    public static final int HEIGHT = 10;

    public World world;
    public ChunkPos pos;
    public int[][][] blocks = new int[HEIGHT][CHUNK_SIZE][CHUNK_SIZE];

    public VertexBuffer buffer;

    public boolean changed = true;

    public Chunk(World world,ChunkPos pos){
        this.pos = pos;
        for (int i = 0; i < HEIGHT;i++){
            for (int g = 0; g < CHUNK_SIZE; g++){
                for (int g2 = 0; g2 < CHUNK_SIZE; g2++){
                    blocks[i][g][g2] = Block.AIR.registeredId;
                }
            }
        }
        this.world = world;
    }


    public void render(World world){
        if (changed){
            this.rebuild(world);
            changed = false;
        }
        buffer.draw(false);
    }

    private void rebuild(World world){
        if (buffer == null){
            buffer = new VertexBuffer(2048,VertexFormat.POSITION_COLOR_UV_NORMAL);
        }
        buffer.reset();
        Vector2i globalPos = this.pos.normalPos();
        for (int y = 0; y < HEIGHT;y++){
            for (int x = 0; x < CHUNK_SIZE;x++){
                for (int z = 0; z < CHUNK_SIZE;z++){
                    Block block = this.getBlock(x,y,z);
                    int gx = globalPos.x + x;
                    int gz = globalPos.y + z;
                    block.render(world,buffer,gx,y,gz);
                }
            }
        }
    }


    public void generate(){
        int rheight = r.nextInt(4) - 2;
        for (int x = 0; x < CHUNK_SIZE; x++){
            for (int z = 0; z < CHUNK_SIZE; z++){
                int h = HEIGHT / 2 + rheight;
                for (int y = 0; y < h;y++) {
                    if (y == h - 1) {
                        this.setBlock(Block.GRASS, x, y, z);
                    } else {
                        this.setBlock(Block.STONE, x, y, z);
                    }
                }
            }
        }


    }

    public void setBlock(Block block,int x, int y, int z){
        this.blocks[y][x][z] = block.registeredId;
    }

    public Block getBlock(int x,int y,int z){
        int id = this.blocks[y][x][z];
        return Block.registeredBlocks.get(id);
    }


    @Override
    public void close() {
        this.buffer.destroy();
        this.buffer = null;
    }
}
