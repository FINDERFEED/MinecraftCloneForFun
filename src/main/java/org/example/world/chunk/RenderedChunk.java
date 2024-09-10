package org.example.world.chunk;

import org.example.Camera;
import org.example.Main;
import org.example.VertexBuffer;
import org.example.VertexFormat;
import org.example.blocks.Block;
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
            this.ready = true;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to render chunk at: " + this.renderedChunk.pos,e);
        }
    }


    public void render(World world){

        ChunkPos cpos = this.renderedChunk.pos;

        Vector2i globalPos = cpos.normalPos().add(8,8);
        Matrix4f modelviewImitation = Main.camera.getModelviewMatrix();
        Matrix4f projection = Main.projectionMatrix;
        Vector4f p = new Vector4f(
                globalPos.x - (float) Main.camera.pos.x,
                (float)Main.camera.pos.y,
                globalPos.y - (float) Main.camera.pos.z,
                1f);
        modelviewImitation.transform(p);
        projection.transform(p);
        p.x /= p.w;
        p.y /= p.w;
        p.z /= p.w;
        if (!MathUtil.isValueBetween(p.x,-1,1) || !MathUtil.isValueBetween(p.z,0,1)){
            return;
        }


        if (buffer != null && this.ready) {

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
