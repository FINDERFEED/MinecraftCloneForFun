package com.finderfeed.entity;

import com.finderfeed.blocks.Block;
import com.finderfeed.blocks.Side;
import com.finderfeed.util.AABox;
import com.finderfeed.util.MathUtil;
import com.finderfeed.util.RaycastUtil;
import com.finderfeed.world.World;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Entity {

    public UUID uuid;

    public World world;

    public Vector3d oldPosition = new Vector3d(0,0,0);

    public Vector3d position = new Vector3d(0,0,0);

    public Vector3d movement = new Vector3d(0,0,0);

    public Vector3i blockPos = new Vector3i(0,0,0);

    public boolean onGround = false;

    public boolean inBlocks = false;

    public Entity(World world){
        this.world = world;
        this.uuid = UUID.randomUUID();
    }


    public void update(){

        this.blockPos = new Vector3i(
                (int)Math.floor(this.position.x),
                (int)Math.floor(this.position.y),
                (int)Math.floor(this.position.z)
        );

        this.checkInBlocks();
        this.movement.y = MathUtil.clamp(movement.y,-3,3);

        oldPosition = new Vector3d(position);

        this.move(movement);



        this.movement.mul(this.getFriction(),1,this.getFriction()).add(0,-this.getGravity(),0);


        if (Math.sqrt(movement.x * movement.x + movement.z * movement.z) < 0.01){
            this.movement.x = 0;
            this.movement.z = 0;
        }

    }


    public void move(Vector3d movement){

//        this.verticalCollision(movement);
        this.collision(movement);

    }

    private void collision(Vector3d movement){
        AABox box = this.getBox(this.position);

        var colliders = this.collectColliders(this.position,movement);

        var collidePos = this.collide(box,colliders,movement);

        this.position = collidePos;
    }

    public List<AABox> collectColliders(Vector3d pos,Vector3d speed){

        List<AABox> boxes = new ArrayList<>();
        int rad = 5;
        Vector3i bp = new Vector3i(
                (int)Math.floor(pos.x),
                (int)Math.floor(pos.y),
                (int)Math.floor(pos.z)
        );

        for (int x = -rad;x <= rad;x++){
            for (int y = -rad;y <= rad;y++){
                for (int z = -rad;z <= rad;z++){
                    if (!world.getBlock(x + bp.x,y + bp.y,z + bp.z).isAir()) {
                        AABox box = new AABox(x, y, z, x + 1, y + 1, z + 1)
                                .offset(bp.x,bp.y,bp.z);
                        boxes.add(box);
                    }
                }
            }
        }

        return boxes;
    }


    public Vector3d collide(AABox box,List<AABox> colliders, Vector3d speed){

        Vector3d center = box.center();


        var xd = (box.maxX - box.minX) / 2;
        var yd = (box.maxY - box.minY) / 2;
        var zd = (box.maxZ - box.minZ) / 2;
        var mind = Math.min(zd,Math.min(xd,yd));

        Vector3d rayStart = center.add(speed.mul(-1,new Vector3d()).normalize().mul(mind),new Vector3d());
        Vector3d rayEnd = center.add(speed,new Vector3d());

        Side finalSide = null;
        Vector3d point = new Vector3d(rayEnd);
        double dist = speed.length();

        for (AABox collider : colliders){

            var c = collider.inflate(
                    xd,0,zd
            );

            c.minY -= yd * 2;

            var raycastResult = RaycastUtil.traceBox(c,rayStart,rayEnd);

            if (raycastResult == null) continue;


            Side s = raycastResult.first();
            var n = s.getNormal();
            double d = speed.dot(n.x,n.y,n.z);
            if (d < 0){
                Vector3d p = raycastResult.second();
                double l = center.sub(p,new Vector3d()).length();
                if (l < dist){
                    dist = point.length();
                    point = p;
                    finalSide = s;
                }
            }
        }


        if (finalSide == Side.TOP){
            onGround = true;
            movement.y = 0;
        }else{
            onGround = false;
        }

        return point;
    }




    private void checkInBlocks(){
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






    public AABox getBox(Vector3d pos){
        return new AABox(
                pos.x - this.cubeCollisionRadius(),
                    pos.y,
                pos.z - this.cubeCollisionRadius(),
                pos.x + this.cubeCollisionRadius(),
                pos.y + this.getHeight(),
                pos.z + this.cubeCollisionRadius()
        );
    }

    public Vector3i getBlockPos(){
        return new Vector3i(blockPos);
    }

}
