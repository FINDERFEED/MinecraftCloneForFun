package org.example.world;

import org.example.Main;
import org.example.VertexBuffer;
import org.example.VertexFormat;
import org.example.blocks.Block;
import org.example.util.noises.Noise;
import org.joml.Random;
import org.joml.Vector2i;

public class Chunk implements AutoCloseable{

    private static final Random r = new Random(32323232);

    public static final int CHUNK_SIZE_SQRT = 4;
    public static final int CHUNK_SIZE = CHUNK_SIZE_SQRT * CHUNK_SIZE_SQRT;
    public static final int HEIGHT = 256;

    public volatile World world;
    public ChunkPos pos;
    public int[][][] blocks = new int[HEIGHT][CHUNK_SIZE][CHUNK_SIZE];

    public volatile VertexBuffer buffer;

    public volatile boolean compiling = false;

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
        if (!compiling && (changed || buffer == null)){
            this.rebuild(world);
            changed = false;
        }
        if (!compiling && buffer != null) {
            buffer.draw(false);
        }
    }

    private void rebuild(World world){
        if (buffer == null){
            VertexFormat format = VertexFormat.POSITION_COLOR_UV_NORMAL;
            int size = HEIGHT * CHUNK_SIZE * CHUNK_SIZE * format.byteSize * 8;
            buffer = new VertexBuffer(size,format);
        }
        buffer.reset();
        compiling = true;
        Main.renderExecutor.submit(()->{
            this.renderBlocks(buffer);
        });
    }

    private void renderBlocks(VertexBuffer buffer){
        try {
            Vector2i globalPos = this.pos.normalPos();
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < CHUNK_SIZE; x++) {
                    for (int z = 0; z < CHUNK_SIZE; z++) {
                        Block block = this.getBlock(x, y, z);
                        int gx = globalPos.x + x;
                        int gz = globalPos.y + z;
                        block.render(world, buffer, gx, y, gz);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to render chunk at: " + this.pos,e);
        }
        compiling = false;
    }


    public void generate(){
        Noise noise = this.world.noise;

        Vector2i global = this.pos.normalPos();

        int baseHeight = 20;

        float mod = 137.345f;
        for (int x = 0; x < CHUNK_SIZE; x++){
            for (int z = 0; z < CHUNK_SIZE; z++){
                float xn = (global.x + x) / mod;
                float zn = (global.y + z) / mod;

//                int ph = (int)( (noiseValue + 1) / 2 * (HEIGHT - baseHeight));

                int h = HEIGHT;


                for (int y = 0; y < h;y++) {


                    float noiseValue = noise.get(xn,y / mod,zn);

                    if (noiseValue > 0){
                        this.setBlock(Block.STONE,x,y,z);
                    }else{
                        this.setBlock(Block.AIR,x,y,z);
                    }
//                    if (y == h - 1) {
//                        this.setBlock(Block.GRASS, x, y, z);
//                    } else {
//                        this.setBlock(Block.STONE, x, y, z);
//                    }
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
