package com.finderfeed.entity;

import com.finderfeed.blocks.Block;
import com.finderfeed.util.BlockRayTraceResult;
import com.finderfeed.util.MathUtil;
import com.finderfeed.world.World;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.UUID;

public class Entity {

    public UUID uuid;

    public World world;

    public Vector3d oldPosition = new Vector3d(0,0,0);

    public Vector3d position = new Vector3d(0,0,0);

    public Vector3d movement = new Vector3d(0,0,0);

    public boolean onGround = false;

    public boolean inBlocks = false;

    public Entity(World world){
        this.world = world;
        this.uuid = UUID.randomUUID();
    }


    public void update(){

        this.checkInBlocks();
        this.movement.y = MathUtil.clamp(movement.y,-3,3);

        oldPosition = new Vector3d(position);

        this.move(movement);



        this.movement.mul(this.getFriction(),1,this.getFriction()).add(0,-this.getGravity(),0);

        if (Math.sqrt(movement.x * movement.x + movement.z * movement.z) < 0.001){
            this.movement.x = 0;
            this.movement.z = 0;
        }

    }


    public void move(Vector3d movement){

        this.verticalCollision(movement);
        this.horizontalCollision(movement);

    }

    private void horizontalCollision(Vector3d movement){

        Vector3d pos = new Vector3d(this.position).add(0,0.1,0);
        Vector3d endX = pos.add(movement.x,0,0,new Vector3d());
        Vector3d endZ = pos.add(0,0,movement.z,new Vector3d());

        BlockRayTraceResult resultX = world.traceBlock(pos,endX);
        BlockRayTraceResult resultZ = world.traceBlock(pos,endZ);
        float rad = this.cubeCollisionRadius();
        if (resultX != null){
            Vector3d point = resultX.pos;
            this.position.x = point.x + (movement.x > 0 ? -rad : rad);
            movement.x = 0;
        }else{
            this.position.x += movement.x;
        }
        if (resultZ != null){
            Vector3d point = resultZ.pos;
            this.position.z = point.z + (movement.z > 0 ? -rad : rad);
            movement.z = 0;
        }else{
            this.position.z += movement.z;
        }

    }

    private void verticalCollision(Vector3d movement) {

        double y = movement.y;


        Vector3d begin = new Vector3d(this.position);
        Vector3d end = new Vector3d(this.position).add(0,y,0);
        if (y > 0){
            begin.y += this.getHeight();
            end.y += this.getHeight();
        }

        BlockRayTraceResult result = world.traceBlock(begin,end);
        if (result != null){
            Vector3d point = result.pos;
            if (y < 0){
                this.onGround = true;
                this.position = point;
            }else{
                this.onGround = false;
                if (!inBlocks) {
                    this.position = new Vector3d(point.x, point.y - this.getHeight(), point.z);
                }else{
                    this.position.add(0,y,0);
                    return;
                }
            }
            movement.y = 0;
        }else{
            if (y > 0){
                this.onGround = false;
            }
            this.position.add(0,y,0);
        }
    }


    private void checkInBlocks(){
//        boolean inBlocks = true;
//        for (int i = 0; i < this.getHeight();i++){
//
//            Vector3i pos = new Vector3i(
//                    (int)Math.floor(this.position.x),
//                    (int)Math.floor(this.position.y + i),
//                    (int)Math.floor(this.position.z)
//            );
//
//            Block block = world.getBlock(pos);
//            if (block.isAir()){
//                inBlocks = false;
//            }
//        }
        Vector3i pos = new Vector3i(
                    (int)Math.floor(this.position.x),
                    (int)Math.floor(this.position.y + this.getHeight()),
                    (int)Math.floor(this.position.z)
        );
        Block block = world.getBlock(pos);
        inBlocks = !block.isAir();
    }

    public void setMovement(Vector3d movement){
        this.movement = movement;
    }

    public Vector3d getMovement() {
        return movement;
    }

    public void addMovement(double x,double y,double z){
        this.movement.add(x,y,z);
    }
    public void addMovement(Vector3d v){
        this.addMovement(v.x,v.y,v.z);
    }

    public float getFriction(){
        return 0.7f;
    }

    public float getGravity(){
        return 0.09f;
    }

    public float getEyeHeight(){
        return 1.5f;
    }

    public float getHeight(){
        return 1.8f;
    }

    public float getMaxMovementSpeed(){
        return 1f;
    }

    public float cubeCollisionRadius(){
        return 0.25f;
    }

}
