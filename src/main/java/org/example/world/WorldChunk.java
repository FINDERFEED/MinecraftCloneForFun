package org.example.world;

import org.example.Camera;
import org.example.Main;
import org.example.VertexBuffer;
import org.example.VertexFormat;
import org.example.blocks.Block;

import org.example.util.MathUtil;
import org.joml.*;
import org.spongepowered.noise.Noise;
import org.spongepowered.noise.NoiseQuality;
import org.spongepowered.noise.module.NoiseModule;
import org.spongepowered.noise.module.source.Perlin;

public class WorldChunk extends Chunk implements AutoCloseable {


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
            this.status = ChunkStatus.FULL;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to render chunk at: " + this.pos,e);
        }
    }


    public static final Perlin p1 = new Perlin();
    public static final Perlin p2 = new Perlin();
    public static final Perlin p3 = new Perlin();

    static {
        p1.setNoiseQuality(NoiseQuality.STANDARD);
        p1.setOctaveCount(1);
        p1.setFrequency(1);
        p1.setSeed(423425434);
        p2.setSeed(93425434);
        p3.setSeed(564564953);
    }


    public void generate(){
        this.initIfNecessary();
        this.status = ChunkStatus.LOADING;

        Vector2i global = this.pos.normalPos();

        int baseHeight = 20;

        float mod = 137.345f;
        for (int x = 0; x < CHUNK_SIZE; x++){
            for (int z = 0; z < CHUNK_SIZE; z++){
                float xn = (global.x + x);
                float zn = (global.y + z);
//                float noiseValue = (float) p1.get(xn,232.433f,zn);

//                int ph = (int)( (noiseValue + 1) / 2 * (baseHeight));

                int h = HEIGHT/*/2 + ph*/;


                for (int y = 0; y < h;y++) {

                    double val1 = Noise.gradientCoherentNoise3D(xn / 143.34,y / 134.34,zn / 143.34,342534534,NoiseQuality.STANDARD);
                    double val2 = Noise.gradientCoherentNoise3D(xn / 243.34,y / 255.34,zn / 423.221,232534534,NoiseQuality.STANDARD);
                    double val3 = Noise.gradientCoherentNoise3D(xn / 434.23,y / 34.34,zn/ 542.232,652534534,NoiseQuality.STANDARD);
                    double val = MathUtil.lerp(val1,val2,val3) * 2 - 1;
                    if (val > 0){
                        this.setBlock(Block.AIR,x,y,z);
                    }else{
                        this.setBlock(Block.STONE,x,y,z);
                    }

                }
            }
        }


        this.status = ChunkStatus.LOADED;
    }




    @Override
    public void close() {
        if (this.buffer != null) {
            this.buffer.destroy();
            this.buffer = null;
        }
        super.close();
    }
}
