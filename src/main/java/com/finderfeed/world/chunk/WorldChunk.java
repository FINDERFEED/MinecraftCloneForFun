package com.finderfeed.world.chunk;

import com.finderfeed.blocks.Block;
import com.finderfeed.util.MathUtil;
import com.finderfeed.util.noises.Perlin3D;
import com.finderfeed.world.ChunkStatus;
import com.finderfeed.world.World;

import org.joml.*;
import org.spongepowered.noise.Noise;
import org.spongepowered.noise.NoiseQuality;

public class WorldChunk extends Chunk implements AutoCloseable {

    public WorldChunk(World world, ChunkPos pos){
        super(world,pos,true);
    }

    public void generate(){
        this.initIfNecessary();
        this.status = ChunkStatus.LOADING;

        this.buildTerrain();
        this.carveCaves();


        this.status = ChunkStatus.LOADED;
    }

    public static final com.finderfeed.util.noises.Noise n = new Perlin3D(234235413);

    private void carveCaves(){
        Vector2i global = this.pos.normalPos();
        for (int x = 0; x < CHUNK_SIZE; x++){
            for (int z = 0; z < CHUNK_SIZE; z++){
                int xn = (global.x + x);
                int zn = (global.y + z);
                int h = HEIGHT;
                for (int y = 0; y < h;y++) {
                    Block block = this.getBlock(x,y,z);
                    if (!block.isAir()) {
                        float m1 = 137.343f;
                        float m1y = m1 / 2.5f;
                        double val1 = n.get(xn / m1, y / m1y, zn / m1);

                        int height = this.getHeight(x,z);
                        float p = (y / (float) height);
                        float bias = MathUtil.lerp(0.2f,0.8f,p);
                        float bias2 = MathUtil.clamp(1 / (y + 0.01f) - 0.25f, 0 ,Integer.MAX_VALUE);


                        val1 += bias + bias2;
                        if (val1 < 0){
                            this.setBlock(Block.AIR,x,y,z);
                        }
                    }
                }
            }
        }
        this.recalculateHeightmap();
    }

    private void buildTerrain(){
        Vector2i global = this.pos.normalPos();

        for (int x = 0; x < CHUNK_SIZE; x++){
            for (int z = 0; z < CHUNK_SIZE; z++){
                int xn = (global.x + x);
                int zn = (global.y + z);
                int h = HEIGHT;


                for (int y = 0; y < h;y++) {
                    Block b = this.decideBlock(x,y,z,xn,zn);
                    if (!b.isAir()){
                        this.setHeight(x,z,y);
                    }
                }
            }
        }

        for (int x = 0; x < CHUNK_SIZE; x++){
            for (int z = 0; z < CHUNK_SIZE; z++){
                int h = this.getHeight(x,z);
                this.setBlock(Block.GRASS,x,h,z);
            }
        }



    }

    private Block decideBlock(int x, int y, int z,int globalX,int globalZ){

//        if (true){
//            if (y < 100){
//                return Block.GRASS;
//            }else{
//                return Block.AIR;
//            }
//        }

        float m1 = 143.34f;
        float m1y = 143.34f;
        float m2 = 243.34f;
        float m2y = 123.34f;
        float m3 = 43.34f;
        float m3y = 43.34f;

        double val1 = Noise.gradientCoherentNoise3D(globalX / m1,y / m1y,globalZ / m1,342534534,NoiseQuality.STANDARD);
        double val2 = Noise.gradientCoherentNoise3D(globalX / m2,y / m2y,globalZ / m2,232534534,NoiseQuality.STANDARD);
        double val3 = Noise.gradientCoherentNoise3D(globalX / m3,y / m3y, globalZ / m3,652534534,NoiseQuality.STANDARD);
        double val = MathUtil.lerp(val1,val2,val3) * 2 - 1;

        float p = MathUtil.easeInOut(y / (float) HEIGHT);
        float bias = MathUtil.lerp(-0.5f,0.45f,p);

        val += bias;

        if (val > 0){
            this.setBlock(Block.AIR,x,y,z);
            return Block.AIR;
        }else{
            this.setBlock(Block.STONE,x,y,z);
            return Block.STONE;
        }
    }


    public void recalculateHeightmap(){
        for (int x = 0;x < CHUNK_SIZE;x++){
            for (int z = 0;z < CHUNK_SIZE;z++){
                for (int y = HEIGHT - 1;y >= 0;y--){
                    Block block = this.getBlock(x,y,z);
                    if (!block.isAir()){
                        this.setHeight(x,z,y);
                        break;
                    }
                }
            }
        }

    }


    @Override
    public void close() {
        super.close();
    }
}
