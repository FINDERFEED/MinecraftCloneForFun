package com.finderfeed.world.chunk;

import com.finderfeed.blocks.Block;
import com.finderfeed.util.EasingFunction;
import com.finderfeed.util.MathUtil;
import com.finderfeed.util.noises.Perlin3D;
import com.finderfeed.world.ChunkStatus;
import com.finderfeed.world.World;

import org.joml.*;
import org.spongepowered.noise.Noise;
import org.spongepowered.noise.NoiseQuality;
import org.spongepowered.noise.module.source.Perlin;

public class WorldChunk extends Chunk implements AutoCloseable {

    public WorldChunk(World world, ChunkPos pos){
        super(world,pos,true);
    }

    public void generate(){
        this.initIfNecessary();
        this.status = ChunkStatus.LOADING;

        this.buildTerrain();
//        this.carveCaves();


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

    private static final EasingFunction function = new EasingFunction()
            .addPoint(0,50)
            .addPoint(0.05f,100)
            .addPoint(0.25f,90)
            .addPoint(0.7f,120)
            .addPoint(0.8f,220)
            .addPoint(1,250);

    private static final Perlin p = new Perlin();
    private static double maxp;
    static {
        p.setFrequency(1);
        p.setLacunarity(0.5f);
        p.setPersistence(0.5f);
        p.setOctaveCount(4);
        p.setSeed(4534324);
        maxp = p.maxValue();
    }

    private Block decideBlock(int x, int y, int z,int globalX,int globalZ){

        int gx = globalX;
        int gz = globalZ;

        double val = Noise.gradientCoherentNoise3D(gx / 145.324,y / 145.324,gz / 145.324,2545324,NoiseQuality.STANDARD);

        val = val * 2 - 1;




//        float d = (float) Noise.gradientCoherentNoise3D(gx / 345.324,987.098,gz / 345.324,20908324,NoiseQuality.STANDARD);
        float d1 = (float) (p.get(gx / 134.324f,875.8768,gz / 134.324f) / maxp);

        float height = function.lerp(d1);

        float p = y / height * 2 - 1;

        val += p;


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
