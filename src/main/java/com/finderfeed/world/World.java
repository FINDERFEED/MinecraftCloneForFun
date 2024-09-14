package com.finderfeed.world;

import com.finderfeed.VertexBuffer;
import com.finderfeed.engine.immediate_buffer_supplier.ImmediateBufferSupplier;
import com.finderfeed.engine.immediate_buffer_supplier.RenderOptions;
import com.finderfeed.util.*;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import com.finderfeed.Camera;
import com.finderfeed.Main;
import com.finderfeed.blocks.Block;
import com.finderfeed.world.chunk.Chunk;
import com.finderfeed.world.chunk.ChunkPos;
import com.finderfeed.world.chunk.RenderedChunk;
import com.finderfeed.world.chunk.WorldChunk;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.function.Predicate;

import static com.finderfeed.engine.shaders.Shaders.BLOCK;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;

public class World implements WorldAccessor {

    public List<WorldChunk> chunksToRender = new ArrayList<>();

    public Long2ObjectLinkedOpenHashMap<RenderedChunk> renderedChunks = new Long2ObjectLinkedOpenHashMap<>();

    public volatile WorldChunks chunks;

    public World(int seed){
        this.chunks = new WorldChunks(this);
    }


    public void tick(){
        Camera camera = Main.camera;

//        Block b = this.traceBlock(camera.pos,camera.pos.add(camera.look.mul(10,new Vector3f()),new Vector3d()));
//        System.out.println(b);

        ChunkPos currentPos = new ChunkPos(camera.pos);

        this.chunks.tick();

        for (var entry : this.chunks.chunkHashMap.long2ObjectEntrySet()){
            long l = entry.getLongKey();
            Chunk c = entry.getValue();
            if (!this.renderedChunks.containsKey(l) && c.status.value >= ChunkStatus.LOADED.value && this.checkNeighbors(c,chunk->chunk.status.value >= ChunkStatus.LOADED.value)) {
                RenderedChunk chunk = new RenderedChunk(c);
                chunk.recompile(false);
                this.renderedChunks.put(l, chunk);
            }
        }

        var iter = this.renderedChunks.long2ObjectEntrySet().iterator();

        while (iter.hasNext()){
            var entry = iter.next();
            RenderedChunk c = entry.getValue();
            if (c.ready && !this.isChunkInRenderDistance(c.renderedChunk,currentPos,Main.chunkRenderDistance)){
                c.close();
                iter.remove();
            }
        }
    }


    public boolean checkNeighbors(Chunk chunk, Predicate<Chunk> check){
        if (!this.chunks.hasChunk(chunk.pos.north()) || !check.test(this.getChunkAt(chunk.pos.north()))) return false;
        if (!this.chunks.hasChunk(chunk.pos.west()) || !check.test(this.getChunkAt(chunk.pos.west()))) return false;
        if (!this.chunks.hasChunk(chunk.pos.east()) || !check.test(this.getChunkAt(chunk.pos.east()))) return false;
        if (!this.chunks.hasChunk(chunk.pos.south()) || !check.test(this.getChunkAt(chunk.pos.south()))) return false;
        return true;
    }

    public boolean isChunkInRenderDistance(Chunk chunk, ChunkPos currentPos, int renderDistance){
        ChunkPos chunkPos = chunk.pos;
        return MathUtil.isValueBetween(chunkPos.x,currentPos.x - renderDistance,currentPos.x + renderDistance) &&
                MathUtil.isValueBetween(chunkPos.z,currentPos.z - renderDistance,currentPos.z + renderDistance);
    }

    public void render(Camera camera,float partialTick){
        this.renderChunks(camera,partialTick);

        Vector3d begin = camera.pos;
        Vector3d end = new Vector3d(camera.pos).add(new Vector3d(camera.look.mul(1)));


        var path = RaycastUtil.voxelRaycast(begin,end);
        GL11.glLineWidth(2);

        VertexBuffer b = ImmediateBufferSupplier.get(RenderOptions.DEFAULT_LINES);
        int tracesAmount = 0;
        for (Vector3i v : path){

            Vector3f p = camera.coordToLocal(v.x,v.y,v.z,partialTick);

            AABox box = new AABox(p.x,p.y,p.z,p.x + 1,p.y + 1,p.z + 1);
            RenderUtil.renderBox(new Matrix4f(),b,
                    box,1f,1f,1f,1f
            );
            var point = RaycastUtil.traceBox(new AABox(v.x,v.y,v.z,v.x + 1,v.y + 1,v.z + 1),
                    begin,end);
            if (true){
                //break;
            }
            if (point != null){
                tracesAmount++;
            }
        }
        System.out.println("Amount of boxes: " + path.size());
        System.out.println("Traces: " + tracesAmount);
        ImmediateBufferSupplier.drawCurrent();

//        var result = this.traceBlock(begin,end);
//
//        if (result != null){
//            var p = result.pos();
//            Vector3f v  = camera.coordToLocal((float)p.x,(float)p.y,(float)p.z,partialTick);
//            var b = ImmediateBufferSupplier.get(RenderOptions.DEFAULT_LINES);
//            b.position(v.x,v.y,v.z).color(1f,0f,0f,1f);
//            b.position(v.x,v.y + 0.1f,v.z).color(1f,0f,0f,1f);
//            GL11.glLineWidth(10);
//            ImmediateBufferSupplier.drawCurrent();
//        }
//
//        System.out.println(result);

    }

    private void renderChunks(Camera camera,float partialTick){
        GL11.glEnable(GL_DEPTH_TEST);
        GL11.glEnable(GL_CULL_FACE);
        BLOCK.run();
        Main.atlasTexture.atlas.bind(0);
        BLOCK.samplerUniform(0);
        for (var entry : this.renderedChunks.long2ObjectEntrySet()){
            RenderedChunk c = entry.getValue();
            c.render(this,camera,partialTick);
        }
        BLOCK.clear();
        GL11.glDisable(GL_CULL_FACE);
    }

    public Chunk getChunkAt(ChunkPos pos){
        return this.chunks.getChunk(pos);
    }

    public Chunk getChunkAt(int x, int z){
        return this.chunks.getChunk(new ChunkPos(x >> 4,z >> 4));
    }


    public BlockRayTraceResult traceBlock(Vector3d begin, Vector3d end){
        AABox baseBox = new AABox(0,0,0,1,1,1);
        Vector3d b = end.sub(begin,new Vector3d());
        for (int i = 0; i < b.length();i++){
            Vector3d p = begin.add(b.normalize(new Vector3d()).mul(i,i,i,new Vector3d()),new Vector3d());
            int x = (int)p.x;
            int y = (int)p.y;
            int z = (int)p.z;
            Block block = this.getBlock(x,y,z);
            if (!block.isAir()){
                var point = RaycastUtil.traceBox(baseBox.offset(x,y,z),begin,end);
                if (point != null){
                    return new BlockRayTraceResult(block,point);
                }
            }
        }
        return null;
    }



    @Override
    public Block getBlock(int x, int y, int z) {
        if (!isYInBounds(y)) return Block.NULL_AIR;
        Chunk chunk = this.getChunkAt(x,z);
        if (chunk.status.value >= ChunkStatus.LOADED.value){
            int lxpos = x - (x >> 4) * 16;
            int lzpos = z - (z >> 4) * 16;
            return chunk.getBlock(lxpos,y,lzpos);
        }
        return Block.NULL_AIR;
    }

    @Override
    public int getHeight(int x, int z) {
        Chunk c = this.getChunkAt(x,z);
        int localX = x - (c.pos.x << 4);
        int localZ = z - (c.pos.z << 4);
        return c.getHeight(localX,localZ);
    }

    public static boolean isYInBounds(int y){
        return y >= 0 && y < WorldChunk.HEIGHT;
    }


    public void addChunkToRender(WorldChunk c){
        ChunkPos currentPos = new ChunkPos(Main.camera.pos);
        this.chunksToRender.add(c);
        chunksToRender.sort(Comparator.comparingInt(chunk -> {
            ChunkPos pos = chunk.pos;
            return Math.abs(currentPos.x - pos.x) + Math.abs(currentPos.z - pos.z);
        }));
    }
}
