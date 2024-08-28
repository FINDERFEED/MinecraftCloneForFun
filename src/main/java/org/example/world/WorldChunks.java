package org.example.world;

import org.example.Camera;
import org.example.Main;
import org.example.util.Util;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class WorldChunks {


    public HashMap<Long,Chunk> chunkHashMap = new LinkedHashMap<>();

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
            var list = this.getChunksInRenderDistance(currentPos,null);
            for (Chunk c : list){
                if (c.status == ChunkStatus.EMPTY) {
                    c.generate();
                }
                if (!world.chunksToRender.contains(c)) {
                    world.chunksToRender.add(c);
                    this.alertNeighboringChunks(c);
                }
//                CompletableFuture<Chunk> task = CompletableFuture.supplyAsync(()->{
//                    c.generate();
//                    return c;
//                }).handle((chunk,exception)->{
//                    if (exception != null){
//                        throw new RuntimeException(exception);
//                    }
//                    return chunk;
//                });
//                this.chunkGenerationTasks.add(task);
            }
            world.chunksToRender.sort(Comparator.comparingInt(chunk->{
                ChunkPos pos = chunk.pos;
                return Math.abs(currentPos.x - pos.x) + Math.abs(currentPos.z - pos.z);
            }));

            initialized = false;
        }
        Iterator<CompletableFuture<Chunk>> tasks = this.chunkGenerationTasks.iterator();
        while (tasks.hasNext()){
            var task = tasks.next();
            if (task.isDone()) {
                try {
                    Chunk c = task.get();
                    world.chunksToRender.add(c);
                } catch (CompletionException | InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e.getCause());
                }
                tasks.remove();
            }
        }
    }



    public List<Chunk> getChunksInRenderDistance(ChunkPos pos,List<ChunkStatus> status){
        List<Chunk> chunks = new ArrayList<>();
        int distance = Main.chunkRenderDistance;

        Chunk c = this.getChunk(pos);
        if (status == null || status.contains(c.status)) chunks.add(c);

        for (int i = 1; i <= distance;i++){
            for (int x = -i; x <= i;x++){
                ChunkPos p1 = pos.offset(x,i);
                ChunkPos p2 = pos.offset(x,-i);
                Chunk chunk = this.getChunk(p1);
                Chunk chunk2 = this.getChunk(p2);
                if (status == null || status.contains(chunk.status)) chunks.add(chunk);
                if (status == null || status.contains(chunk2.status)) chunks.add(chunk2);
            }

            for (int z = -i + 1; z <= i - 1;z++){
                ChunkPos p1 = pos.offset(-i,z);
                ChunkPos p2 = pos.offset(i,z);
                Chunk chunk = this.getChunk(p1);
                Chunk chunk2 = this.getChunk(p2);
                if (status == null || status.contains(chunk.status)) chunks.add(chunk);
                if (status == null || status.contains(chunk2.status)) chunks.add(chunk2);
            }
        }
        return chunks;
    }


    public void alertNeighboringChunks(Chunk chunk){
        Chunk north = this.getChunk(chunk.pos.north());  north.changed = true;
        Chunk west = this.getChunk(chunk.pos.west()); west.changed = true;
        Chunk east = this.getChunk(chunk.pos.east()); east.changed = true;
        Chunk south = this.getChunk(chunk.pos.south());  south.changed = true;
    }


    public synchronized Chunk getChunk(ChunkPos pos){
        long lpos = Util.coordsToLong(pos.x,pos.z);
        return this.chunkHashMap.computeIfAbsent(lpos,l->new Chunk(world,pos));
    }

}
