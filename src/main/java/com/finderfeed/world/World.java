package com.finderfeed.world;

import com.finderfeed.VertexBuffer;
import com.finderfeed.engine.immediate_buffer_supplier.ImmediateBufferSupplier;
import com.finderfeed.engine.immediate_buffer_supplier.RenderOptions;
import com.finderfeed.entity.Entity;
import com.finderfeed.util.*;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import com.finderfeed.Camera;
import com.finderfeed.Main;
import com.finderfeed.blocks.Block;
import com.finderfeed.world.chunk.Chunk;
import com.finderfeed.world.chunk.ChunkPos;
import com.finderfeed.world.chunk.RenderedChunk;
import com.finderfeed.world.chunk.WorldChunk;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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



    public Object2ObjectMap<UUID,Entity> entityByUuid = new Object2ObjectOpenHashMap<>();

    public List<Entity> entities = new ArrayList<>();

    public List<WorldChunk> chunksToRender = new ArrayList<>();

    public Long2ObjectLinkedOpenHashMap<RenderedChunk> renderedChunks = new Long2ObjectLinkedOpenHashMap<>();

    public volatile WorldChunks chunks;


    public World(){
        this.chunks = new WorldChunks(this);
    }


    public void tick(){

        this.chunks.tick();
        this.manageChunkLoad();
        this.tickEntities();

    }


    public void render(Camera camera,float partialTick){
        this.renderChunks(camera,partialTick);

//        Vector3d pos = camera.calculateCameraPos(partialTick);
//        var buf = ImmediateBufferSupplier.get(RenderOptions.DEFAULT_LINES);
//        for (Entity entity : entities){
//
//            Matrix4f offsetMat = new Matrix4f().translate(-(float)pos.x,-(float)pos.y,-(float)pos.z);
//            AABox box = entity.getBox(entity.position);
//            AABox bbox = box.offset(-pos.x,-pos.y,-pos.z);
//            if (Main.frustum.isVisible(bbox)){
//                RenderUtil.renderBox(new Matrix4f(),buf,bbox,1f,1f,1f,1f);
//                buf.position(offsetMat,new Vector3f(box.center())).color(1f,1f,0f,1f);
//                buf.position(offsetMat,new Vector3f(box.center().add(entity.getMovement()))).color(1f,1f,0f,1f);
//            }
//        }
//        ImmediateBufferSupplier.drawCurrent();

        this.renderTracedBlock(camera,partialTick);
    }

    private void tickEntities(){
        for (Entity entity : entities){
            ChunkPos pos = new ChunkPos(entity.position);
            if (this.chunks.hasChunk(pos)){
                entity.update();
            }
        }
    }

    private void manageChunkLoad(){
        ChunkPos currentPos = new ChunkPos(Main.camera.pos);

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
            if (this.shouldRenderedChunkBeDeleted(currentPos, c)){
                c.close();
                iter.remove();
            }
        }
    }

    private boolean shouldRenderedChunkBeDeleted(ChunkPos cameraPos, RenderedChunk renderedChunk){
        return renderedChunk.readyToCompile &&
                !this.isChunkInRenderDistance(renderedChunk.renderedChunk,cameraPos,Main.chunkRenderDistance) ||
                !this.chunks.chunkHashMap.containsValue(renderedChunk.renderedChunk);
    }

    public void addEntity(Entity entity){
        if (!entityByUuid.containsKey(entity.uuid)){
            this.entities.add(entity);
            this.entityByUuid.put(entity.uuid,entity);
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


    private void renderTracedBlock(Camera camera,float partialTick){
        Vector3d begin = camera.pos;
        Vector3d end = new Vector3d(camera.pos).add(new Vector3d(camera.look.mul(100)));
        var result = this.traceBlock(begin,end);
        if (result != null){
            var p = result.blockPos;
            Vector3f v  = camera.coordToLocal((float)p.x,(float)p.y,(float)p.z,partialTick);
            VertexBuffer b = ImmediateBufferSupplier.get(RenderOptions.DEFAULT_LINES);

            float offs = 0.005f;
            RenderUtil.renderBox(new Matrix4f(),b,new AABox(
                    v.x - offs,v.y - offs,v.z - offs,v.x + 1 + offs,v.y + 1 + offs,v.z + 1 + offs
            ),1f,1f,1f,1f);

//            for (AABox box : Entity.colliders){
//                Vector3f vs = camera.coordToLocal((float)box.minX,(float)box.minY,(float)box.minZ,partialTick);
//                RenderUtil.renderBox(new Matrix4f(),b,new AABox(
//                        vs.x,vs.y,vs.z,vs.x + 1,vs.y + 1,vs.z + 1
//                ).inflate(0.25,0,0.25),1f,0f,0f,1f);
//            }

            GL11.glLineWidth(3);
            ImmediateBufferSupplier.drawCurrent();
        }
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
        var path = RaycastUtil.voxelRaycast(begin,end);
        for (Vector3i v : path){
            Block block = this.getBlock(v.x,v.y,v.z);
            if (!block.isAir()) {
                 var pair = RaycastUtil.traceBox(baseBox.offset(
                        v.x, v.y, v.z
                ), begin, end);
                if (pair != null) {
                    return new BlockRayTraceResult(pair.first(),new Vector3i(v),block, pair.right());
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

    public void setBlock(Block block,int x,int y,int z){
        if (!isYInBounds(y)) return;
        Chunk chunk = this.getChunkAt(x,z);
        if (chunk.status.value >= ChunkStatus.LOADED.value){
            int lxpos = x - (x >> 4) * 16;
            int lzpos = z - (z >> 4) * 16;
            chunk.setBlock(block,lxpos,y,lzpos);
            chunk.recalculateHeightAt(lxpos,lzpos);
        }
        this.recompileChunksOnBlockBreaking(x,z);
    }

    private void recompileChunksOnBlockBreaking(int x,int z){
        ChunkPos original = new ChunkPos(x >> Chunk.CHUNK_SIZE_SQRT,z >> Chunk.CHUNK_SIZE_SQRT);
        this.recompileChunk(original);
        ChunkPos c1 = new ChunkPos((x + 1) >> Chunk.CHUNK_SIZE_SQRT,z >> Chunk.CHUNK_SIZE_SQRT);
        ChunkPos c2 = new ChunkPos(x >> Chunk.CHUNK_SIZE_SQRT,(z + 1) >> Chunk.CHUNK_SIZE_SQRT);
        if (!c1.equals(original)) {
            this.recompileChunk(c1);
        }else{
            c1 = new ChunkPos((x - 1) >> Chunk.CHUNK_SIZE_SQRT,z >> Chunk.CHUNK_SIZE_SQRT);
            if (!c1.equals(original)) {
                this.recompileChunk(c1);
            }
        }
        if (!c2.equals(original)) {
            this.recompileChunk(c2);
        }else{
            c2 = new ChunkPos(x >> Chunk.CHUNK_SIZE_SQRT,(z - 1) >> Chunk.CHUNK_SIZE_SQRT);
            if (!c2.equals(original)) {
                this.recompileChunk(c2);
            }
        }
    }

    private void recompileChunk(int x,int z){
        ChunkPos pos = new ChunkPos(
                x >> Chunk.CHUNK_SIZE_SQRT,z >> Chunk.CHUNK_SIZE_SQRT
        );
        this.recompileChunk(pos);
    }

    private void recompileChunk(ChunkPos pos){
        RenderedChunk renderedChunk = this.renderedChunks.get(pos.asLong());
        if (renderedChunk != null){
            renderedChunk.recompile(true);
        }
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
