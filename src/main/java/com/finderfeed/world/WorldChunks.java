package com.finderfeed.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import com.finderfeed.Camera;
import com.finderfeed.Main;
import com.finderfeed.util.Util;
import com.finderfeed.world.chunk.Chunk;
import com.finderfeed.world.chunk.ChunkPos;
import com.finderfeed.world.chunk.DummyChunk;
import com.finderfeed.world.chunk.WorldChunk;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

public class WorldChunks {


    public Long2ObjectLinkedOpenHashMap<Chunk> chunkHashMap = new Long2ObjectLinkedOpenHashMap<>();

    private List<CompletableFuture<Chunk>> chunkGenerationTasks = new ArrayList<>();

    public World world;

    private boolean initialized = false;

    public WorldChunks(World world){
        this.world = world;
    }


    public void tick(){
        Camera camera = Main.camera;
        ChunkPos currentPos = new ChunkPos(camera.pos);
        if (camera.movedBetweenChunks || !initialized){


            var entryIterator = this.chunkHashMap.long2ObjectEntrySet().iterator();

            while (entryIterator.hasNext()){

                var c = entryIterator.next();

                Chunk chunk = c.getValue();
                var pos = chunk.pos;
                ChunkPos b = currentPos.subtract(pos);
                int dist = Math.max(Math.abs(b.x),Math.abs(b.z));
                if (dist > Main.chunkRenderDistance + 1 && this.canChunkBeUnloaded(chunk)){
                    chunk.blocks = null;
                    chunk.close();
                    chunk.status = ChunkStatus.EMPTY;
                    entryIterator.remove();
                }
            }

            var list = this.getChunksInSquareRadius(currentPos, null, Main.chunkRenderDistance + 1,false);
            for (Chunk c : list){
                if (c.status == ChunkStatus.EMPTY) {
                    c.status = ChunkStatus.LOADING;
                    CompletableFuture<Chunk> task = CompletableFuture.supplyAsync(() -> {
                        c.generate();
                        return c;
                    },Main.utilExecutor).handle((chunk, exception) -> {
                        if (exception != null) {
                            throw new RuntimeException(exception);
                        }
                        return chunk;
                    });
                    this.chunkGenerationTasks.add(task);
                }
            }
            initialized = false;

        }
        Iterator<CompletableFuture<Chunk>> tasks = this.chunkGenerationTasks.iterator();
        while (tasks.hasNext()){
            var task = tasks.next();
            if (task.isDone()) {
                try {
                    Chunk c = task.get();
                } catch (CompletionException | InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e.getCause());
                }
                tasks.remove();
            }
        }
//        if (wasAdded){
//            world.chunksToRender.sort(Comparator.comparingInt(chunk -> {
//                ChunkPos pos = chunk.pos;
//                return Math.abs(currentPos.x - pos.x) + Math.abs(currentPos.z - pos.z);
//            }));
//        }
    }



    public List<Chunk> getChunksInSquareRadius(ChunkPos pos, Predicate<Chunk> chunkPredicate, int distance, boolean nullToDummies){
        List<Chunk> chunks = new ArrayList<>();

        if (!nullToDummies || this.hasChunk(pos)) {
            Chunk c = this.getChunk(pos);
            if (chunkPredicate == null || chunkPredicate.test(c)) chunks.add(c);
        }
        for (int i = 1; i <= distance;i++){
            for (int x = -i; x <= i;x++){
                ChunkPos p1 = pos.offset(x,i);
                ChunkPos p2 = pos.offset(x,-i);
                if (!nullToDummies || this.hasChunk(p1)) {
                    Chunk chunk = this.getChunk(p1);
                    if (chunkPredicate == null || chunkPredicate.test(chunk)) chunks.add(chunk);
                }else{
                    chunks.add(new DummyChunk(world,p1));
                }

                if (!nullToDummies || this.hasChunk(p2)) {
                    Chunk chunk2 = this.getChunk(p2);
                    if (chunkPredicate == null || chunkPredicate.test(chunk2)) chunks.add(chunk2);
                }else{
                    chunks.add(new DummyChunk(world,p1));
                }
            }

            for (int z = -i + 1; z <= i - 1;z++){
                ChunkPos p1 = pos.offset(-i,z);
                ChunkPos p2 = pos.offset(i,z);

                if (!nullToDummies || this.hasChunk(p1)) {
                    Chunk chunk = this.getChunk(p1);
                    if (chunkPredicate == null || chunkPredicate.test(chunk)) chunks.add(chunk);
                }else{
                    chunks.add(new DummyChunk(world,p1));
                }

                if (!nullToDummies || this.hasChunk(p2)) {
                    Chunk chunk2 = this.getChunk(p2);
                    if (chunkPredicate == null || chunkPredicate.test(chunk2)) chunks.add(chunk2);
                }else{
                    chunks.add(new DummyChunk(world,p1));
                }
            }
        }
        return chunks;
    }

    public boolean canChunkBeUnloaded(Chunk c){
        ChunkPos west = c.pos.west();
        ChunkPos east = c.pos.east();
        ChunkPos north = c.pos.north();
        ChunkPos south = c.pos.south();
        if (hasChunk(west)){
            Chunk chunk = this.getChunk(west);
            if (chunk.status != ChunkStatus.FULL && chunk.status != ChunkStatus.LOADED) return false;
        }
        if (hasChunk(east)){
            Chunk chunk = this.getChunk(east);
            if (chunk.status != ChunkStatus.FULL && chunk.status != ChunkStatus.LOADED) return false;
        }
        if (hasChunk(south)){
            Chunk chunk = this.getChunk(south);
            if (chunk.status != ChunkStatus.FULL && chunk.status != ChunkStatus.LOADED) return false;
        }
        if (hasChunk(north)){
            Chunk chunk = this.getChunk(north);
            if (chunk.status != ChunkStatus.FULL && chunk.status != ChunkStatus.LOADED) return false;
        }
        return c.status == ChunkStatus.FULL || c.status == ChunkStatus.LOADED;
    }

    public void alertNeighboringChunks(WorldChunk chunk){
        Chunk north = this.getChunk(chunk.pos.north());  north.changed = true;
        Chunk west = this.getChunk(chunk.pos.west()); west.changed = true;
        Chunk east = this.getChunk(chunk.pos.east()); east.changed = true;
        Chunk south = this.getChunk(chunk.pos.south());  south.changed = true;
    }


    public Chunk getChunk(ChunkPos pos){
        long lpos = Util.coordsToLong(pos.x,pos.z);
        return this.chunkHashMap.computeIfAbsent(lpos,l->new WorldChunk(world,pos));
    }

    public boolean hasChunk(ChunkPos pos){
        long lpos = Util.coordsToLong(pos.x,pos.z);
        return this.chunkHashMap.containsKey(lpos);
    }

}
