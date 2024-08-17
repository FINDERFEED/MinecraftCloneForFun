package org.example.blocks;

import org.example.Main;
import org.example.VertexBuffer;
import org.example.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class Block {

    public static final List<Block> registeredBlocks = new ArrayList<>();


    public static final Block AIR = new Block("air",-1,-1,-1);
    public static final Block NULL_AIR = new Block("no_chunk_air",-1,-1,-1);
    public static final Block GRASS = new Block("grass",0,2,1);
    public static final Block STONE = new Block("grass",3,3,3);

    private int top;
    private int bottom;
    private int side;
    public final int registeredId;
    private String name;

    public Block(String name,int top,int bottom,int side){
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


    public boolean shouldRenderSide(World world,Side side,int x,int y,int z){
        Block block = world.getBlock(new Vector3i(x,y,z).add(side.normal),false);
        return block.isAir() || block == Block.NULL_AIR;
    }



    public void render(World world, VertexBuffer buffer, float x, float y, float z){
        if (this.isAir()){
            return;
        }

        float step = 16f / Main.atlas.getTexWidth();

        float sU = uById(this.side);
        float sV = vById(this.side) - step;

        Vector3i normal;
        if (this.shouldRenderSide(world,Side.SOUTH,(int) x, (int) y,(int) z)) {
            normal = Side.SOUTH.normal;
            buffer.position(x, y, z).color(1f, 1f, 1f, 1f).uv(sU, sV).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y, z).color(1f, 1f, 1f, 1f).uv(sU + step, sV).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y + 1, z).color(1f, 1f, 1f, 1f).uv(sU + step, sV + step).normal(normal.x, normal.y, normal.z);
            buffer.position(x, y + 1, z).color(1f, 1f, 1f, 1f).uv(sU, sV + step).normal(normal.x, normal.y, normal.z);
        }

        if (this.shouldRenderSide(world,Side.NORTH,(int) x, (int) y,(int) z)) {
            normal = Side.NORTH.normal;
            buffer.position(x, y, z + 1).color(1f, 1f, 1f, 1f).uv(sU + step, sV).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y, z + 1).color(1f, 1f, 1f, 1f).uv(sU, sV).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y + 1, z + 1).color(1f, 1f, 1f, 1f).uv(sU, sV + step).normal(normal.x, normal.y, normal.z);
            buffer.position(x, y + 1, z + 1).color(1f, 1f, 1f, 1f).uv(sU + step, sV + step).normal(normal.x, normal.y, normal.z);
        }

        if (this.shouldRenderSide(world,Side.WEST,(int) x, (int) y,(int) z)) {
            normal = Side.WEST.normal;
            buffer.position(x, y, z).color(1f, 1f, 1f, 1f).uv(sU + step, sV).normal(normal.x, normal.y, normal.z);
            buffer.position(x, y, z + 1).color(1f, 1f, 1f, 1f).uv(sU, sV).normal(normal.x, normal.y, normal.z);
            buffer.position(x, y + 1, z + 1).color(1f, 1f, 1f, 1f).uv(sU, sV + step).normal(normal.x, normal.y, normal.z);
            buffer.position(x, y + 1, z).color(1f, 1f, 1f, 1f).uv(sU + step, sV + step).normal(normal.x, normal.y, normal.z);
        }

        if (this.shouldRenderSide(world,Side.EAST,(int) x, (int) y,(int) z)) {
            normal = Side.EAST.normal;
            buffer.position(x + 1, y, z).color(1f, 1f, 1f, 1f).uv(sU, sV).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y, z + 1).color(1f, 1f, 1f, 1f).uv(sU + step, sV).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y + 1, z + 1).color(1f, 1f, 1f, 1f).uv(sU + step, sV + step).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y + 1, z).color(1f, 1f, 1f, 1f).uv(sU, sV + step).normal(normal.x, normal.y, normal.z);
        }
        float dU = uById(this.bottom);
        float dV = vById(this.bottom) - step;

        if (this.shouldRenderSide(world,Side.BOTTOM,(int) x, (int) y,(int) z)) {
            normal = Side.BOTTOM.normal;
            buffer.position(x, y, z).color(1f, 1f, 1f, 1f).uv(dU, dV).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y, z).color(1f, 1f, 1f, 1f).uv(dU + step, dV).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y, z + 1).color(1f, 1f, 1f, 1f).uv(dU + step, dV + step).normal(normal.x, normal.y, normal.z);
            buffer.position(x, y, z + 1).color(1f, 1f, 1f, 1f).uv(dU, dV + step).normal(normal.x, normal.y, normal.z);
        }
        float tU = uById(this.top);
        float tV = vById(this.top) - step;

        if (this.shouldRenderSide(world,Side.TOP,(int) x, (int) y,(int) z)) {
            normal = Side.TOP.normal;
            buffer.position(x, y + 1, z).color(1f, 1f, 1f, 1f).uv(tU, tV).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y + 1, z).color(1f, 1f, 1f, 1f).uv(tU + step, tV).normal(normal.x, normal.y, normal.z);
            buffer.position(x + 1, y + 1, z + 1).color(1f, 1f, 1f, 1f).uv(tU + step, tV + step).normal(normal.x, normal.y, normal.z);
            buffer.position(x, y + 1, z + 1).color(1f, 1f, 1f, 1f).uv(tU, tV + step).normal(normal.x, normal.y, normal.z);
        }
    }

    private float uById(int id){
        float u = id % (Main.atlas.getTexWidth() / 16);
        return u * 16f / Main.atlas.getTexWidth();
    }
    private float vById(int id){
        float v = id / (Main.atlas.getTexHeight() / 16);
        return 1 - v * 16f / Main.atlas.getTexHeight();
    }

}
