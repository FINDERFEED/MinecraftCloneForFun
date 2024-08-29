package org.example.world;

import org.example.Camera;
import org.example.Main;
import org.example.VertexBuffer;
import org.example.VertexFormat;
import org.example.blocks.Block;
import org.example.util.noises.Noise;
import org.joml.*;

public class WorldChunk extends Chunk implements AutoCloseable{


    public volatile VertexBuffer buffer;

    public WorldChunk(World world, ChunkPos pos){
        super(world,pos);
    }


    public void render(World world){
        if (changed && status == ChunkStatus.FULL){
            this.rebuild(world,true);
            changed = false;
        }
        if (this.status == ChunkStatus.FULL && buffer != null) {

            Camera camera = Main.camera;

            Vector3d pos = camera.calculateCameraPos(Main.timer.partialTick);



            Vector3d offset = new Vector3d(-pos.x + this.pos.x * CHUNK_SIZE,0, -pos.z + this.pos.z * CHUNK_SIZE);


            var matrix = camera.getModelviewMatrix();

            matrix.pushMatrix();

            matrix.translate((float) offset.x,0,(float) offset.z);



            Main.BLOCK.mat4Uniform("modelview",matrix);

            buffer.draw(false);

            matrix.popMatrix();
        }
    }

    public void rebuild(World world,boolean onMainThread){
        if (buffer == null){
            VertexFormat format = VertexFormat.POSITION_COLOR_UV_NORMAL;
            int size = 65534;
            buffer = new VertexBuffer(size,format);
        }
        buffer.reset();
        this.status = ChunkStatus.COMPILING;
        if (!onMainThread) {
            LocalChunkWorld localChunkWorld = new LocalChunkWorld(world, this.pos, 1);
            Main.renderExecutor.submit(() -> {
                this.renderBlocks(localChunkWorld, buffer);
            });
        }else{
            this.renderBlocks(world,buffer);
        }
    }

    private void renderBlocks(WorldAccessor world,VertexBuffer buffer){
        try {
            Vector2i globalPos = this.pos.normalPos();
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < CHUNK_SIZE; x++) {
                    for (int z = 0; z < CHUNK_SIZE; z++) {
                        Block block = this.getBlock(x, y, z);
                        int gx = globalPos.x + x;
                        int gz = globalPos.y + z;
                        block.render(world, buffer,x,y,z, gx, y, gz);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to render chunk at: " + this.pos,e);
        }
        this.status = ChunkStatus.FULL;
    }


    public void generate(){
        this.status = ChunkStatus.LOADING;
        Noise noise = this.world.noise;

        Vector2i global = this.pos.normalPos();

        int baseHeight = 20;

        float mod = 137.345f;
        for (int x = 0; x < CHUNK_SIZE; x++){
            for (int z = 0; z < CHUNK_SIZE; z++){
                float xn = (global.x + x) / mod;
                float zn = (global.y + z) / mod;
                float noiseValue = noise.get(xn,232.433f,zn);

                int ph = (int)( (noiseValue + 1) / 2 * (baseHeight));

                int h = HEIGHT/2 + ph;


                for (int y = 0; y < h;y++) {
                    if (y == h - 1) {
                        this.setBlock(Block.GRASS, x, y, z);
                    } else {
                        this.setBlock(Block.STONE, x, y, z);
                    }
                }
            }
        }


        this.status = ChunkStatus.LOADED;
    }




    @Override
    public void close() {
        this.buffer.destroy();
        this.buffer = null;
        super.close();
    }
}
