package org.example.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import org.example.Camera;
import org.example.Main;
import org.example.blocks.Block;
import org.example.util.MathUtil;
import org.example.world.chunk.Chunk;
import org.example.world.chunk.ChunkPos;
import org.example.world.chunk.RenderedChunk;
import org.example.world.chunk.WorldChunk;

import java.util.*;
import java.util.function.Predicate;

public class World implements WorldAccessor {

    public List<WorldChunk> chunksToRender = new ArrayList<>();

    public Long2ObjectLinkedOpenHashMap<RenderedChunk> renderedChunks = new Long2ObjectLinkedOpenHashMap<>();

    public volatile WorldChunks chunks;

    public World(int seed){
        this.chunks = new WorldChunks(this);
    }


    public void tick(){
        Camera camera = Main.camera;

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

//        Iterator<WorldChunk> chunkIterator = this.chunksToRender.iterator();
//        while (chunkIterator.hasNext()){
//            WorldChunk chunk = chunkIterator.next();
//            if (chunk.status == ChunkStatus.FULL && !this.isChunkInRenderDistance(chunk,currentPos,Main.chunkRenderDistance)){
//                chunk.close();
//                chunkIterator.remove();
//            }else if (this.isChunkInRenderDistance(chunk,currentPos,Main.chunkRenderDistance) && chunk.status == ChunkStatus.LOADED && this.checkNeighbors(chunk,c->c.status.value >= ChunkStatus.LOADED.value)){
//                chunk.rebuild(this,false);
//            }
//        }

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

    public void render(){
        Camera camera = Main.camera;
//        if (chunksToRender != null) {
//            for (WorldChunk chunk : chunksToRender) {
//                chunk.render(this);
//            }
//        }

        for (var entry : this.renderedChunks.long2ObjectEntrySet()){
            RenderedChunk c = entry.getValue();
            c.render(this);
        }

    }

    public Chunk getChunkAt(ChunkPos pos){
        return this.chunks.getChunk(pos);
    }

    public Chunk getChunkAt(int x, int z){
        return this.chunks.getChunk(new ChunkPos(x >> 4,z >> 4));
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
