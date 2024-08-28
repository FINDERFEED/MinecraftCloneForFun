package org.example.world;

import org.example.Camera;
import org.example.Main;
import org.example.VertexBuffer;
import org.example.VertexFormat;
import org.example.blocks.Block;
import org.example.util.MathUtil;
import org.example.util.noises.Noise;
import org.example.util.noises.Simplex3D;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class World implements WorldAccessor {


    public List<Chunk> chunksToRender = new ArrayList<>();


    private VertexBuffer lines;

    public Noise noise;

    public WorldChunks chunks;

    public World(int seed){
        noise = new Simplex3D(seed);
        lines = new VertexBuffer(1024, VertexFormat.POSITION_COLOR);
        this.chunks = new WorldChunks(this);
    }


    public void tick(){
        Camera camera = Main.camera;
//
        ChunkPos currentPos = new ChunkPos(camera.pos);
//
//        if (camera.movedBetweenChunks || !initialized){
//            List<Chunk> chunks = this.getChunksInRenderDistance(currentPos,true);
//            for (Chunk c : chunks){
//                if (!chunksToRender.contains(c)){
//                    chunksToRender.add(c);
//                    this.alertNeighboringChunks(c);
//                }
//            }
//            this.chunksToRender.sort(Comparator.comparingInt(chunk->{
//                ChunkPos pos = chunk.pos;
//                return Math.abs(currentPos.x - pos.x) + Math.abs(currentPos.z - pos.z);
//            }));
//            initialized = false;
//        }
//
        this.chunks.tick();


        Iterator<Chunk> chunkIterator = this.chunksToRender.iterator();
        while (chunkIterator.hasNext()){
            Chunk chunk = chunkIterator.next();
            if (chunk.status == ChunkStatus.FULL && !this.isChunkInRenderDistance(chunk,currentPos,Main.chunkRenderDistance)){
                chunks.alertNeighboringChunks(chunk);
                chunk.close();
                chunkIterator.remove();
            }
        }


    }

    public boolean isChunkInRenderDistance(Chunk chunk,ChunkPos currentPos,int renderDistance){
        ChunkPos chunkPos = chunk.pos;
        return MathUtil.isValueBetween(chunkPos.x,currentPos.x - renderDistance,currentPos.x + renderDistance) &&
                MathUtil.isValueBetween(chunkPos.z,currentPos.z - renderDistance,currentPos.z + renderDistance);
    }

    public void render(){
        Camera camera = Main.camera;
        if (chunksToRender != null) {
            for (Chunk chunk : chunksToRender) {
                chunk.render(this);
            }
        }

        //doest work rn lol
        if (Main.drawChunkLines){
            Main.POSITION_COLOR.run(Main.projectionMatrix,Main.camera.getModelviewMatrix());
            GL11.glLineWidth(4);

            ChunkPos pos = new ChunkPos(camera.pos);
            Vector2i p = pos.normalPos();

            int dist = Main.chunkRenderDistance;
            for (int i = -dist;i <= dist + 1;i++){
                for (int g = -dist;g <= dist + 1;g++){
                    lines.position(p.x + i * 16,-20,p.y + g * 16).color(1f,0,0,1f);
                    lines.position(p.x + i * 16,Chunk.HEIGHT * 2,p.y + g * 16).color(1f,0,0,1f);
                }
            }
            lines.drawLines(true);
            Main.POSITION_COLOR.clear();
        }

    }

    public Chunk getChunkAt(ChunkPos pos){
        return this.chunks.getChunk(pos);
    }

    public Chunk getChunkAt(int x,int z){
        return this.chunks.getChunk(new ChunkPos(x >> 4,z >> 4));
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        if (!isYInBounds(y)) return Block.NULL_AIR;
        Chunk chunk = this.getChunkAt(x,z);
        if (chunk.status.value > ChunkStatus.GENERATED.value){
            int lxpos = x - (x >> 4) * 16;
            int lzpos = z - (z >> 4) * 16;
            return chunk.getBlock(lxpos,y,lzpos);
        }
        return Block.NULL_AIR;
    }

    public static boolean isYInBounds(int y){
        return y >= 0 && y < Chunk.HEIGHT;
    }


}
