package org.example.world.chunk;

import org.example.*;
import org.example.blocks.Block;
import org.example.util.AABox;
import org.example.util.MathUtil;
import org.example.world.LocalChunkWorld;
import org.example.world.World;
import org.example.world.WorldAccessor;
import org.joml.*;
import org.joml.Math;

public class RenderedChunk implements AutoCloseable {

    public Chunk renderedChunk;

    public VertexBuffer buffer;

    public boolean ready = false;

    public AABox baseBox;

    public RenderedChunk(Chunk chunk){
        this.renderedChunk = chunk;
    }

    public void recompile(boolean onMainThread){
        this.ready = false;
        if (buffer == null){
            VertexFormat format = VertexFormat.POSITION_COLOR_UV_NORMAL;
            int size = 65534;
            buffer = new VertexBuffer(size,format);
        }
        buffer.reset();
        if (!onMainThread) {
            LocalChunkWorld localChunkWorld = new LocalChunkWorld(renderedChunk.world, this.renderedChunk.pos, 1);
            Main.renderExecutor.submit(() -> {
                this.compileBlocks(localChunkWorld, buffer);
            });
        }else{
            this.compileBlocks(this.renderedChunk.world,buffer);
        }

    }


    private void compileBlocks(WorldAccessor world, VertexBuffer buffer){
        try {
            this.ready = false;
            Vector2i globalPos = this.renderedChunk.pos.normalPos();
            for (int y = 0; y < Chunk.HEIGHT; y++) {
                for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
                    for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                        int gx = globalPos.x + x;
                        int gz = globalPos.y + z;
                        Block block = world.getBlock(gx, y, gz);
                        block.render(world, buffer,x,y,z, gx, y, gz);
                    }
                }
            }
            baseBox = new AABox(
                    0,0,0,
                    Chunk.CHUNK_SIZE,Chunk.HEIGHT,Chunk.CHUNK_SIZE
            );
            this.ready = true;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to render chunk at: " + this.renderedChunk.pos,e);
        }
    }


    private boolean isChunkVisible(){
        Frustum frustum = Main.frustum;

        Vector3d pos = Main.camera.calculateCameraPos(Main.timer.partialTick);

        AABox box = this.baseBox.offset(
                -(float) pos.x + renderedChunk.pos.x * Chunk.CHUNK_SIZE,
                0,
                -(float) pos.z + renderedChunk.pos.z * Chunk.CHUNK_SIZE
        );

        boolean visible = frustum.isVisible(box);

        return visible;
    }

    public void render(World world){
        if (buffer != null && this.ready) {

            if (!this.isChunkVisible()){
                return;
            }

            Camera camera = Main.camera;

            Vector3d pos = camera.calculateCameraPos(Main.timer.partialTick);

            Vector3d offset = new Vector3d(-pos.x + renderedChunk.pos.x * Chunk.CHUNK_SIZE,0, -pos.z + renderedChunk.pos.z * Chunk.CHUNK_SIZE);


            var matrix = camera.getModelviewMatrix();

            matrix.pushMatrix();

            matrix.translate((float) offset.x,(float)-pos.y,(float) offset.z);



            Main.BLOCK.mat4Uniform("modelview",matrix);

            buffer.draw(false);

            matrix.popMatrix();
        }
    }


    @Override
    public void close() {
        if (this.buffer != null){
            this.buffer.close();
            this.buffer = null;
        }
    }
}
