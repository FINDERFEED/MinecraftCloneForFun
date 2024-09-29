package com.finderfeed.entity;

import com.finderfeed.util.BlockRayTraceResult;
import com.finderfeed.world.World;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.UUID;

public class Entity {

    public UUID uuid;

    public World world;

    public Vector3d oldPosition = new Vector3d(0,0,0);

    public Vector3d position = new Vector3d(0,0,0);

    public Vector3d movement = new Vector3d(0,0,0);

    public boolean onGround = false;

    public Entity(World world){
        this.world = world;
        this.uuid = UUID.randomUUID();
    }


    public void update(){

        oldPosition = new Vector3d(movement);

        this.move(movement);



        this.movement.mul(this.getFriction(),1,this.getFriction()).add(0,-this.getGravity(),0);

        if (Math.sqrt(movement.x * movement.x + movement.z * movement.z) < 0.001){
            this.movement.x = 0;
            this.movement.z = 0;
        }

    }

    public void move(Vector3d movement){

        this.verticalCollision(movement);

    }

    private void verticalCollision(Vector3d movement) {

        double y = movement.y;

        Vector3d begin = new Vector3d(this.position);
        if (y > 0){
            begin.y += this.getHeight();
        }
        Vector3d end = new Vector3d(this.position).add(0,y,0);


        BlockRayTraceResult result = world.traceBlock(begin,end);
        if (result != null){
            Vector3d point = result.pos;
            if (y < 0){
                this.onGround = true;
                this.position = point;
            }else{
                this.position = new Vector3d(point.x,point.y - this.getHeight(),point.z);
            }
            movement.y = 0;
        }else{
            if (y > 0){
                this.onGround = false;
            }
            this.position.add(0,y,0);
        }
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
        return 0.8f;
    }

    public float getGravity(){
        return 0.08f;
    }

    public float getEyeHeight(){
        return 1.5f;
    }

    public float getHeight(){
        return 1.8f;
    }

}
