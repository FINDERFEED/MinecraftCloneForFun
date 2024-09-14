package com.finderfeed.world.chunk;

import com.finderfeed.*;
import com.finderfeed.blocks.Block;
import com.finderfeed.engine.RenderEngine;
import com.finderfeed.engine.shaders.Shaders;
import com.finderfeed.util.AABox;
import com.finderfeed.world.LocalChunkWorld;
import com.finderfeed.world.World;
import com.finderfeed.world.WorldAccessor;
import org.joml.*;
import org.joml.Math;

public class RenderedChunk implements AutoCloseable {

    public Chunk renderedChunk;

    public VertexBuffer buffer;

    public boolean readyToCompile = true;

    public AABox baseBox;

    public RenderedChunk(Chunk chunk){
        this.renderedChunk = chunk;
    }

    public void recompile(boolean onMainThread){
        if (readyToCompile) {
            this.readyToCompile = false;
            if (buffer == null) {
                VertexFormat format = VertexFormat.POSITION_COLOR_UV_NORMAL;
                int size = 65534;
                buffer = new VertexBuffer(size, format);
            }
            buffer.reset();
            if (!onMainThread) {
                LocalChunkWorld localChunkWorld = new LocalChunkWorld(renderedChunk.world, this.renderedChunk.pos, 1);
                Main.renderExecutor.submit(() -> {
                    this.compileBlocks(localChunkWorld, buffer);
                });
            } else {
                this.compileBlocks(this.renderedChunk.world, buffer);
            }
        }

    }


    private void compileBlocks(WorldAccessor world, VertexBuffer buffer){
        try {
            this.readyToCompile = false;
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

            int maxHeight = 0;
            for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
                for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                    int gx = globalPos.x + x;
                    int gz = globalPos.y + z;
                    maxHeight = Math.max(world.getHeight(gx,gz),maxHeight);
                }
            }


            baseBox = new AABox(
                    0,0,0,
                    Chunk.CHUNK_SIZE,maxHeight,Chunk.CHUNK_SIZE
            );
            this.readyToCompile = true;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to render chunk at: " + this.renderedChunk.pos,e);
        }
    }


    private boolean isChunkVisible(Camera camera){
        Frustum frustum = Main.frustum;

        Vector3d pos = camera.calculateCameraPos(Main.timer.partialTick);
        AABox box = this.baseBox.offset(
                -(float) pos.x + renderedChunk.pos.x * Chunk.CHUNK_SIZE,
                -(float) pos.y,
                -(float) pos.z + renderedChunk.pos.z * Chunk.CHUNK_SIZE
        );

        boolean visible = frustum.isVisible(box);

        return visible;
    }

    public void render(World world,Camera camera,float partialTick){
        if (buffer != null && this.readyToCompile) {

            if (!this.isChunkVisible(camera)){
                return;
            }

            Vector3d pos = camera.calculateCameraPos(partialTick);

            Vector3d offset = new Vector3d(-pos.x + renderedChunk.pos.x * Chunk.CHUNK_SIZE,0, -pos.z + renderedChunk.pos.z * Chunk.CHUNK_SIZE);


            var matrix = RenderEngine.getModelviewStack();

            matrix.pushMatrix();

            matrix.translate((float) offset.x,(float)-pos.y,(float) offset.z);

            RenderEngine.applyModelviewMatrix();


            Shaders.BLOCK.mat4Uniform("modelview",RenderEngine.getModelviewMatrix());

            buffer.draw(false);

            matrix.popMatrix();
            RenderEngine.applyModelviewMatrix();
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
