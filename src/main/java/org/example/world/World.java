package org.example.world;

import org.example.Camera;
import org.example.Main;
import org.example.VertexBuffer;
import org.example.VertexFormat;
import org.example.blocks.Block;
import org.example.util.MathUtil;
import org.example.util.Util;
import org.example.util.noises.Noise;
import org.example.util.noises.Perlin3D;
import org.example.util.noises.Simplex3D;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class World {


    public HashMap<Long,Chunk> chunkHashMap = new LinkedHashMap<>();

    public List<Chunk> chunksToRender = new ArrayList<>();

    public boolean initialized = false;

    private VertexBuffer lines;

    public Noise noise;

    public World(int seed){
        noise = new Simplex3D(seed);
        lines = new VertexBuffer(1024, VertexFormat.POSITION_COLOR);
    }


    public void tick(){
        Camera camera = Main.camera;

        ChunkPos currentPos = new ChunkPos(camera.pos);

        if (camera.movedBetweenChunks || !initialized){
            List<Chunk> chunks = this.getChunksInRenderDistance(currentPos,true);
            for (Chunk c : chunks){
                if (!chunksToRender.contains(c)){
                    chunksToRender.add(c);
                    this.alertNeighboringChunks(c);
                }
            }
            this.chunksToRender.sort(Comparator.comparingInt(chunk->{
                ChunkPos pos = chunk.pos;
                return Math.abs(currentPos.x - pos.x) + Math.abs(currentPos.z - pos.z);
            }));
            initialized = false;
        }



        Iterator<Chunk> chunkIterator = this.chunksToRender.iterator();
        while (chunkIterator.hasNext()){
            Chunk chunk = chunkIterator.next();
            if (!chunk.compiling && !this.isChunkInRenderDistance(chunk,currentPos,Main.chunkRenderDistance)){
                this.alertNeighboringChunks(chunk);
                chunk.close();
                chunkIterator.remove();
            }
        }


    }

    public void alertNeighboringChunks(Chunk chunk){
        Chunk north = this.getChunkAt(chunk.pos.north(),false); if (north != null) north.changed = true;
        Chunk west = this.getChunkAt(chunk.pos.west(),false); if (west != null) west.changed = true;
        Chunk east = this.getChunkAt(chunk.pos.east(),false); if (east != null) east.changed = true;
        Chunk south = this.getChunkAt(chunk.pos.south(),false); if (south != null) south.changed = true;
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
                if (chunk != null) {
                    chunk.render(this);
                }
            }
        }


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

    public List<Chunk> getChunksInRenderDistance(ChunkPos pos,boolean generate){
        List<Chunk> chunks = new ArrayList<>();
        int distance = Main.chunkRenderDistance;
        chunks.add(this.getChunkAt(pos,generate));
        for (int i = 1; i <= distance;i++){
            for (int x = -i; x <= i;x++){
                ChunkPos p1 = pos.offset(x,i);
                ChunkPos p2 = pos.offset(x,-i);
                Chunk chunk = this.getChunkAt(p1,generate);
                Chunk chunk2 = this.getChunkAt(p2,generate);
                chunks.add(chunk);
                chunks.add(chunk2);
            }

            for (int z = -i + 1; z <= i - 1;z++){
                ChunkPos p1 = pos.offset(-i,z);
                ChunkPos p2 = pos.offset(i,z);
                Chunk chunk = this.getChunkAt(p1,generate);
                Chunk chunk2 = this.getChunkAt(p2,generate);
                chunks.add(chunk);
                chunks.add(chunk2);
            }
        }
        return chunks;
    }


    public Chunk getChunkAt(ChunkPos pos,boolean generate){
        return this.getChunkAt(pos.x << 4,pos.z << 4, generate);
    }

    public Chunk getChunkAt(int x,int z,boolean generate){
        int cx = x >> Chunk.CHUNK_SIZE_SQRT;
        int cz = z >> Chunk.CHUNK_SIZE_SQRT;
        long pos = Util.coordsToLong(cx,cz);
        if (!chunkHashMap.containsKey(pos) && generate){
            Chunk chunk = new Chunk(this,new ChunkPos(cx,cz));
            chunk.generate();
            chunkHashMap.put(pos,chunk);
            return chunk;
        }else{
            return chunkHashMap.get(pos);
        }
    }

    public Block getBlock(int x,int y,int z,boolean generate){
        Chunk chunk = this.getChunkAt(x,z,generate);
        if (chunk == null || !isYInBounds(y)){
            return Block.NULL_AIR;
        }
        Vector2i v = chunk.pos.normalPos();
        return chunk.getBlock(x - v.x,y,z - v.y);
    }

    public Block getBlock(Vector3i vp,boolean generate){
        return this.getBlock(vp.x,vp.y,vp.z,generate);
    }

    public boolean isYInBounds(int y){
        return y >= 0 && y < Chunk.HEIGHT;
    }

}
