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
        float maxYSpeed = 3f;
        this.movement.y = MathUtil.clamp(movement.y,-maxYSpeed,maxYSpeed);

        oldPosition = new Vector3d(position);

        this.move(movement);


        float gravity = this.getGravity();

        this.movement.mul(this.getFriction(),1,this.getFriction()).sub(0,gravity,0);


        if (Math.sqrt(movement.x * movement.x + movement.z * movement.z) < 0.01){
            this.movement.x = 0;
            this.movement.z = 0;
        }

    }


    public void move(Vector3d movement){

        this.collision(movement);

    }

    private void collision(Vector3d movement){
        AABox box = this.getBox(this.position);

        var colliders = this.collectColliders(this.position,movement);

        var collidePos = this.collide(box,colliders,movement);


        this.position = collidePos;
    }

    public static List<AABox> colliders = new ArrayList<>();

    public List<AABox> collectColliders(Vector3d pos,Vector3d speed){

        colliders.clear();

        AABox box = this.getBox(pos);
        AABox inflated = box.inflateInDirection(speed);

        List<AABox> boxes = new ArrayList<>();

        for (int x = (int) Math.floor(inflated.minX); x <= (int)Math.ceil(inflated.maxX); x++){
            for (int y = (int) Math.floor(inflated.minY); y <= (int)Math.ceil(inflated.maxY); y++){
                for (int z = (int) Math.floor(inflated.minZ); z <= (int)Math.ceil(inflated.maxZ); z++){
                    if (!world.getBlock(x,y,z).isAir()) {
                        AABox b = new AABox(x, y, z, x + 1, y + 1, z + 1);
                        boxes.add(b);
                    }

                }
            }
        }


        colliders.addAll(boxes);

        return boxes;
    }


    public Vector3d collide(AABox box,List<AABox> colliders, Vector3d speed){


        double xr = box.getXRadius();
        double yr = box.getYRadius();
        double zr = box.getZRadius();

        Vector3d currentPos = box.centerAtY0();
        Vector3d addition = new Vector3d();

        boolean collidedGround = false;

        while (true){

            //check if speed is zero, then end
            if (speed.x == 0 && speed.y == 0 && speed.z == 0){
                break;
            }

            //find begin and end vectors
            Vector3d begin = currentPos.add(speed.mul(-0.0001,new Vector3d()),new Vector3d());
            Vector3d end = currentPos.add(speed,new Vector3d());

            double mindist = end.distance(begin);

            Side collidedSide = null;

            //cycle through all colliders to find closest raycast point
            for (AABox collider : colliders){

                AABox b = collider.inflate(
                        xr,0,zr
                ); b.minY -= (box.maxY - box.minY);


                var res = RaycastUtil.traceBox(b,begin,end);
                if (res == null) continue;

                Side side = res.first();
                Vector3d nrm = side.normal3d();
                if (nrm.dot(speed) > 0) continue;


                Vector3d point = res.second();


                double d = begin.distance(point);
                if (d < mindist){
                    collidedSide = side;
                    mindist = d;
                    currentPos = point;
                }
            }

            //collided side = null equals to no collision occured.
            if (collidedSide == null) {
                 break;
            }else{
                collidedGround = collidedSide == Side.TOP;
            }

            Vector3i nrm = collidedSide.getNormal();

            //remove the corresponding speed component
            speed.mul(
                    1 - Math.abs(nrm.x),
                    1 - Math.abs(nrm.y),
                    1 - Math.abs(nrm.z)
            );
            float smallAddition = 0.0001f;
            Vector3f add = new Vector3f(nrm.x * smallAddition,nrm.y * smallAddition,nrm.z * smallAddition);
            currentPos.add(add);
            addition.add(add);
        }

        this.onGround = collidedGround;


        return currentPos.add(speed).sub(addition);
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
