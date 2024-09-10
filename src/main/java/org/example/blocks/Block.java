package org.example.blocks;

import org.example.Main;
import org.example.VertexBuffer;
import org.example.textures.atlases.AtlasTexture;
import org.example.world.World;
import org.example.world.WorldAccessor;
import org.joml.Matrix4f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class Block {

    public static final List<Block> registeredBlocks = new ArrayList<>();


    public static final Block AIR = new Block("air",null,null,null);
    public static final Block NULL_AIR = new Block("no_chunk_air",null,null,null);
    public static final Block GRASS = new Block("grass","grass_top.png","dirt.png","grass_side.png");
    public static final Block STONE = new Block("stone","stone.png","stone.png","stone.png");

    private String top;
    private String bottom;
    private String side;
    public final int registeredId;
    private String name;

    public Block(String name,String top,String bottom,String side){
        this.name = name;
        this.top = top;
        this.bottom = bottom;
        this.side = side;
        registeredBlocks.add(this);
        this.registeredId = registeredBlocks.size() - 1;
    }


    public boolean isAir(){
        return this == Block.AIR || this == Block.NULL_AIR;
    }


    public boolean shouldRenderSide(WorldAccessor world,Side side,int x,int y,int z){
        Block block = world.getBlock(new Vector3i(x,y,z).add(side.normal));
        return block.isAir() || block == Block.NULL_AIR;
    }



    public void render(WorldAccessor world, VertexBuffer buffer, float x, float y, float z, int gx, int gy, int gz){
        if (this.isAir()){
            return;
        }


//        float step = 16f / Main.atlas.getTexWidth();


//        float side.u1() = uById(this.side);
//        float side.v1() = vById(this.side) - step;

        AtlasTexture.TextureData side = Main.atlasTexture.imageData.get(this.side);
        AtlasTexture.TextureData top = Main.atlasTexture.imageData.get(this.top);
        AtlasTexture.TextureData bottom = Main.atlasTexture.imageData.get(this.bottom);

        Vector3i normal;
        if (this.shouldRenderSide(world,Side.SOUTH,(int) gx, (int) gy,(int) gz)) {
            normal = Side.SOUTH.normal;

            buffer.position(x, y, z + 1).color(1f, 1f, 1f, 1f).uv(side.u2(), side.v1()).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y, z + 1).color(1f, 1f, 1f, 1f).uv(side.u1(), side.v1()).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y + 1, z + 1).color(1f, 1f, 1f, 1f).uv(side.u1(), side.v2()).normal(normal.x, normal.y, normal.z);
            buffer.position(x, y + 1, z + 1).color(1f, 1f, 1f, 1f).uv(side.u2(), side.v2()).normal(normal.x, normal.y, normal.z);

        }

        if (this.shouldRenderSide(world,Side.NORTH,(int) gx, (int) gy,(int) gz)) {
            normal = Side.NORTH.normal;

            buffer.position(x, y + 1, z).color(1f, 1f, 1f, 1f).uv(side.u1(), side.v2()).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y + 1, z).color(1f, 1f, 1f, 1f).uv(side.u2(), side.v2()).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y, z).color(1f, 1f, 1f, 1f).uv(side.u2(), side.v1()).normal(normal.x, normal.y, normal.z);
            buffer.position(x, y, z).color(1f, 1f, 1f, 1f).uv(side.u1(), side.v1()).normal(normal.x, normal.y, normal.z);

        }

        if (this.shouldRenderSide(world,Side.WEST,(int) gx, (int) gy,(int) gz)) {
            normal = Side.WEST.normal;
            buffer.position(x, y, z).color(1f, 1f, 1f, 1f).uv(side.u2(), side.v1()).normal(normal.x, normal.y, normal.z);
            buffer.position(x, y, z + 1).color(1f, 1f, 1f, 1f).uv(side.u1(), side.v1()).normal(normal.x, normal.y, normal.z);
            buffer.position(x, y + 1, z + 1).color(1f, 1f, 1f, 1f).uv(side.u1(), side.v2()).normal(normal.x, normal.y, normal.z);
            buffer.position(x, y + 1, z).color(1f, 1f, 1f, 1f).uv(side.u2(), side.v2()).normal(normal.x, normal.y, normal.z);
        }

        if (this.shouldRenderSide(world,Side.EAST,(int) gx, (int) gy,(int) gz)) {
            normal = Side.EAST.normal;
            
            buffer.position(x + 1, y + 1, z).color(1f, 1f, 1f, 1f).uv(side.u1(), side.v2()).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y + 1, z + 1).color(1f, 1f, 1f, 1f).uv(side.u2(), side.v2()).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y, z + 1).color(1f, 1f, 1f, 1f).uv(side.u2(), side.v1()).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y, z).color(1f, 1f, 1f, 1f).uv(side.u1(), side.v1()).normal(normal.x, normal.y, normal.z);

        }
//        float dU = uById(this.bottom);
//        float dV = vById(this.bottom) - step;

        if (this.shouldRenderSide(world,Side.BOTTOM,(int) gx, (int) gy,(int) gz)) {
            normal = Side.BOTTOM.normal;
            buffer.position(x, y, z).color(1f, 1f, 1f, 1f).uv(bottom.u1(), bottom.v1()).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y, z).color(1f, 1f, 1f, 1f).uv(bottom.u2(), bottom.v1()).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y, z + 1).color(1f, 1f, 1f, 1f).uv(bottom.u2(), bottom.v2()).normal(normal.x, normal.y, normal.z);
            buffer.position(x, y, z + 1).color(1f, 1f, 1f, 1f).uv(bottom.u1(), bottom.v2()).normal(normal.x, normal.y, normal.z);
        }
//        float tU = uById(this.top);
//        float tV = vById(this.top) - step;

        if (this.shouldRenderSide(world,Side.TOP,(int) gx, (int) gy,(int) gz)) {
            normal = Side.TOP.normal;

            buffer.position(x, y + 1, z + 1).color(1f, 1f, 1f, 1f).uv(top.u1(), top.v2()).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y + 1, z + 1).color(1f, 1f, 1f, 1f).uv(top.u2(), top.v2()).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y + 1, z).color(1f, 1f, 1f, 1f).uv(top.u2(), top.v1()).normal(normal.x, normal.y, normal.z);
            buffer.position(x, y + 1, z).color(1f, 1f, 1f, 1f).uv(top.u1(), top.v1()).normal(normal.x, normal.y, normal.z);

        }
    }

//    private float uById(int id){
//        float u = id % (Main.atlas.getTexWidth() / 16);
//        return u * 16f / Main.atlas.getTexWidth();
//    }
//    private float vById(int id){
//        float v = id / (Main.atlas.getTexHeight() / 16);
//        return 1 - v * 16f / Main.atlas.getTexHeight();
//    }

    @Override
    public String toString() {
        return name;
    }
}
