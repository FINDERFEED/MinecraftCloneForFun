package org.example.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import org.example.Camera;
import org.example.Main;
import org.example.util.Util;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class WorldChunks {


    public Long2ObjectLinkedOpenHashMap<WorldChunk> chunkHashMap = new Long2ObjectLinkedOpenHashMap<>();

    private List<CompletableFuture<WorldChunk>> chunkGenerationTasks = new ArrayList<>();

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

                WorldChunk chunk = c.getValue();
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

            var list = this.getChunksInSquareRadius(currentPos, null, Main.chunkRenderDistance + 1);
            boolean addedImmediate = false;
            for (WorldChunk c : list){
                if (c.status == ChunkStatus.EMPTY) {
                    c.status = ChunkStatus.LOADING;
                    CompletableFuture<WorldChunk> task = CompletableFuture.supplyAsync(() -> {
                        c.generate();
                        return c;
                    },Main.utilExecutor).handle((chunk, exception) -> {
                        if (exception != null) {
                            throw new RuntimeException(exception);
                        }
                        return chunk;
                    });
                    this.chunkGenerationTasks.add(task);
                }else{
                    if (!world.chunksToRender.contains(c)) {
                        world.chunksToRender.add(c);
                        addedImmediate = true;
                    }
                }
            }
            if (addedImmediate){
                world.chunksToRender.sort(Comparator.comparingInt(chunk -> {
                    ChunkPos pos = chunk.pos;
                    return Math.abs(currentPos.x - pos.x) + Math.abs(currentPos.z - pos.z);
                }));
            }
            initialized = false;

        }
        Iterator<CompletableFuture<WorldChunk>> tasks = this.chunkGenerationTasks.iterator();
        boolean wasAdded = false;
        while (tasks.hasNext()){
            var task = tasks.next();
            if (task.isDone()) {
                try {
                    WorldChunk c = task.get();
                    world.chunksToRender.add(c);
                    wasAdded = true;
                } catch (CompletionException | InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e.getCause());
                }
                tasks.remove();
            }
        }
        if (wasAdded){
            world.chunksToRender.sort(Comparator.comparingInt(chunk -> {
                ChunkPos pos = chunk.pos;
                return Math.abs(currentPos.x - pos.x) + Math.abs(currentPos.z - pos.z);
            }));
        }
    }



    public List<WorldChunk> getChunksInSquareRadius(ChunkPos pos, List<ChunkStatus> status, int distance){
        List<WorldChunk> chunks = new ArrayList<>();

        WorldChunk c = this.getChunk(pos);
        if (status == null || status.contains(c.status)) chunks.add(c);

        for (int i = 1; i <= distance;i++){
            for (int x = -i; x <= i;x++){
                ChunkPos p1 = pos.offset(x,i);
                ChunkPos p2 = pos.offset(x,-i);
                WorldChunk chunk = this.getChunk(p1);
                WorldChunk chunk2 = this.getChunk(p2);
                if (status == null || status.contains(chunk.status)) chunks.add(chunk);
                if (status == null || status.contains(chunk2.status)) chunks.add(chunk2);
            }

            for (int z = -i + 1; z <= i - 1;z++){
                ChunkPos p1 = pos.offset(-i,z);
                ChunkPos p2 = pos.offset(i,z);
                WorldChunk chunk = this.getChunk(p1);
                WorldChunk chunk2 = this.getChunk(p2);
                if (status == null || status.contains(chunk.status)) chunks.add(chunk);
                if (status == null || status.contains(chunk2.status)) chunks.add(chunk2);
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
        WorldChunk north = this.getChunk(chunk.pos.north());  north.changed = true;
        WorldChunk west = this.getChunk(chunk.pos.west()); west.changed = true;
        WorldChunk east = this.getChunk(chunk.pos.east()); east.changed = true;
        WorldChunk south = this.getChunk(chunk.pos.south());  south.changed = true;
    }


    public WorldChunk getChunk(ChunkPos pos){
        long lpos = Util.coordsToLong(pos.x,pos.z);
        return this.chunkHashMap.computeIfAbsent(lpos,l->new WorldChunk(world,pos));
    }

    public boolean hasChunk(ChunkPos pos){
        long lpos = Util.coordsToLong(pos.x,pos.z);
        return this.chunkHashMap.containsKey(lpos);
    }

}
