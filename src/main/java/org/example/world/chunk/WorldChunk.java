package org.example.world.chunk;

import org.example.blocks.Block;

import org.example.util.MathUtil;
import org.example.world.ChunkStatus;
import org.example.world.World;
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
                        float m1 = 160.542f;
                        double val1 = Noise.gradientCoherentNoise3D(xn / m1, y / m1, zn / m1, 54534, NoiseQuality.STANDARD);

                        float p = (y / (float) HEIGHT);
                        float bias = MathUtil.lerp(0.2f,0.7f,p);
                        float bias2 = 1 / (y + 0.01f);



                        val1 = val1 * 2 - 1;
                        val1 += bias + bias2;
                        if (val1 < 0){
                            this.setBlock(Block.AIR,x,y,z);
                        }
                    }
                }
            }
        }
    }

    private void buildTerrain(){
        Vector2i global = this.pos.normalPos();

        for (int x = 0; x < CHUNK_SIZE; x++){
            for (int z = 0; z < CHUNK_SIZE; z++){
                int xn = (global.x + x);
                int zn = (global.y + z);
                int h = HEIGHT;


                for (int y = 0; y < h;y++) {
                    this.decideBlock(x,y,z,xn,zn);
                }
            }
        }
    }

    private void decideBlock(int x, int y, int z,int globalX,int globalZ){

        float m1 = 143.34f;
        float m2 = 243.34f;
        float m3 = 43.34f;

        double val1 = Noise.gradientCoherentNoise3D(globalX / m1,y / m1,globalZ / m1,342534534,NoiseQuality.STANDARD);
        double val2 = Noise.gradientCoherentNoise3D(globalX / m2,y / m2,globalZ / m2,232534534,NoiseQuality.STANDARD);
        double val3 = Noise.gradientCoherentNoise3D(globalX / m3,y / m3, globalZ / m3,652534534,NoiseQuality.STANDARD);
        double val = MathUtil.lerp(val1,val2,val3) * 2 - 1;

        float p = MathUtil.easeInOut(y / (float) HEIGHT);
        float bias = MathUtil.lerp(-0.5f,0.45f,p);

        val += bias;

        if (val > 0){
            this.setBlock(Block.AIR,x,y,z);
        }else{
            this.setBlock(Block.STONE,x,y,z);
        }

    }




    @Override
    public void close() {
        super.close();
    }
}
