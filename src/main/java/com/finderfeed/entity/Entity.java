package com.finderfeed.entity;

import com.finderfeed.blocks.Block;
import com.finderfeed.util.AABox;
import com.finderfeed.util.BlockRayTraceResult;
import com.finderfeed.util.MathUtil;
import com.finderfeed.world.World;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Comparator;
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

        this.verticalCollision(movement);
        this.horizontalCollision(movement);

    }

    private void horizontalCollision(Vector3d movement){
        AABox box = this.getBox(this.position);
        var colliders = this.collectColliders(box,movement);

        Vector3d pos = this.collide(box,colliders,movement);

        double xDiff = pos.x - this.position.x;
        double zDiff = pos.z - this.position.z;
        this.position.add(xDiff,0,zDiff);
    }



    private List<MoveCollider> collectColliders(AABox entityBox,Vector3d moveVector){
        Vector3i v = this.getBlockPos();
        List<MoveCollider> boxes = new ArrayList<>();
        int testRad = 5;

        double ex;
        double ez;
        var center = entityBox.center();


        for (int x = -testRad;x <= testRad;x++){
            for (int y = 0;y <= testRad;y++){
                for (int z = -testRad;z <= testRad;z++){
                    Vector3i p = new Vector3i(
                            v.x + x,
                            v.y + y,
                            v.z + z
                    );
                    Block block = world.getBlock(p);
                    if (!block.isAir()){
                        AABox box = new AABox(
                                p.x,p.y,p.z,
                                p.x + 1,p.y + 1,p.z + 1
                        );



                        double bx;
                        double bz;
                        double ex1;
                        double ez1;
                        if (moveVector.x >= 0 && moveVector.z >= 0){
                            bx = box.minX;
                            bz = box.minZ;
                            ex = entityBox.maxX;
                            ez = entityBox.maxZ;
                            ex1 = ex + moveVector.x;
                            ez1 = ez + moveVector.z;
                        }else if (moveVector.x < 0 && moveVector.z >= 0){
                            bx = box.maxX;
                            bz = box.minZ;
                            ex = entityBox.minX;
                            ez = entityBox.maxZ;
                            ex1 = ex - moveVector.x;
                            ez1 = ez - moveVector.z;
                        } else if (moveVector.x < 0 && moveVector.z < 0){
                            bx = box.maxX;
                            bz = box.maxZ;
                            ex = entityBox.minX;
                            ez = entityBox.minZ;
                            ex1 = ex + moveVector.x;
                            ez1 = ez + moveVector.z;
                        }else{
                            bx = box.minX;
                            bz = box.maxZ;
                            ex = entityBox.maxX;
                            ez = entityBox.minZ;
                            ex1 = ex - moveVector.x;
                            ez1 = ez - moveVector.z;
                        }

                        double value = (bx - ex) * (ez1 - ez) - (bz - ez) * (ex1 - ex);



                        MoveCollider collider = new MoveCollider(-1,0,box);

                        if (value < 0){
                            collider.type = MoveCollider.Z_COLLIDING;
                            collider.wall = bz;
                        }else{
                            collider.wall = bx;
                            collider.type = MoveCollider.X_COLLIDING;
                        }

                        boxes.add(collider);

                    }
                }
            }
        }


        boxes.sort(Comparator.comparingDouble(c->{
            if (c.type == MoveCollider.X_COLLIDING){
                return Math.abs(c.wall - center.x);
            }else if (c.type == MoveCollider.Z_COLLIDING){
                return Math.abs(c.wall - center.z);
            }else{
                return Math.abs(c.wall - center.y);
            }
        }));


        return boxes;
    }

    public Vector3d collide(AABox box,List<MoveCollider> colliders, Vector3d moveVector){

        Vector3d center = box.center();


        Vector3d finalMove = center.add(moveVector);

        double xd = moveVector.x;
        double yd = moveVector.y;
        double zd = moveVector.z;

        double boxX;
        double boxY;
        double boxZ;

        if (xd > 0){
            boxX = box.maxX;
        }else{
            boxX = box.minX;
        }
        if (zd > 0){
            boxZ = box.maxZ;
        }else{
            boxZ = box.minZ;
        }

        double xRad = box.getXRadius();
        double yRad = box.getYRadius();
        double zRad = box.getZRadius();

        double xCollision = finalMove.x;
        double yCollision = finalMove.y;
        double zCollision = finalMove.z;

        for (MoveCollider c : colliders){
            AABox collider = c.box;
            if (c.type == MoveCollider.X_COLLIDING) {
                if (xd > 0) {
                    if (!(box.maxX + xd < collider.minX && box.minX + xd < collider.minX) && box.maxX <= collider.minX) { //it did "collide" on X
                        double delta = collider.minX - box.maxX;

                        double p = delta / Math.abs(xd);
                        double zm = p * moveVector.z;
                        if (!(box.minZ + zm <= collider.minZ && box.maxZ + zm <= collider.minZ || box.minZ + zm >= collider.maxZ && box.maxZ + zm >= collider.maxZ)) {
                            double ym = moveVector.y;
                            if (!(box.minY + ym < collider.minY && box.maxY + ym < collider.minY || box.minY + ym > collider.maxY && box.maxY + ym > collider.maxY)) {
                                xCollision = collider.minX - xRad;
                                if (delta < 0.0001) {
                                    moveVector.x = 0;
                                }
                            }
                        }
                    }
                } else {
                    if (!(box.minX + xd > collider.maxX && box.maxX + xd > collider.maxX) && box.minX >= collider.maxX) { //it did "collide" on X
                        double delta = box.minX - collider.maxX;
                        double p = delta / Math.abs(xd);
                        double zm = p * moveVector.z;
                        if (!(box.minZ + zm <= collider.minZ && box.maxZ + zm <= collider.minZ || box.minZ + zm >= collider.maxZ && box.maxZ + zm >= collider.maxZ)) {
                            double ym = moveVector.y;
                            if (!(box.minY + ym <= collider.minY && box.maxY + ym <= collider.minY || box.minY + ym >= collider.maxY && box.maxY + ym >= collider.maxY)) {
                                xCollision = collider.maxX + xRad;
                                if (delta < 0.0001) {
                                    moveVector.x = 0;
                                }
                            }
                        }
                    }
                }
            }else if (c.type == MoveCollider.Z_COLLIDING) {

                if (zd > 0) {
                    if (!(box.maxZ + zd < collider.minZ && box.minZ + zd < collider.minZ) && box.maxZ <= collider.minZ) { //it did "collide" on Z
                        double delta = collider.minZ - box.maxZ;

                        double p = delta / Math.abs(zd);
                        double xm = p * moveVector.x;
                        if (!(box.minX + xm <= collider.minX && box.maxX + xm <= collider.minX || box.minX + xm >= collider.maxX && box.maxX + xm >= collider.maxX)) {
                            double ym = moveVector.y;
                            if (!(box.minY + ym <= collider.minY && box.maxY + ym <= collider.minY || box.minY + ym >= collider.maxY && box.maxY + ym >= collider.maxY)) {
                                if (delta < 0.0001) {
                                    moveVector.z = 0;
                                }
                                zCollision = collider.minZ - zRad;
                            }
                        }
                    }
                } else {
                    if (!(box.minZ + zd > collider.maxZ && box.maxZ + zd > collider.maxZ) && box.minZ >= collider.maxZ) { //it did "collide" on Z
                        double delta = box.minZ - collider.maxZ;
                        double p = delta / Math.abs(zd);
                        double xm = p * moveVector.x;
                        if (!(box.minX + xm <= collider.minX && box.maxX + xm <= collider.minX || box.minX + xm >= collider.maxX && box.maxX + xm >= collider.maxX)) {
                            double ym = moveVector.y;
                            if (!(box.minY + ym <= collider.minY && box.maxY + ym <= collider.minY || box.minY + ym >= collider.maxY && box.maxY + ym >= collider.maxY)) {
                                if (delta < 0.0001) {
                                    moveVector.z = 0;
                                }
                                zCollision = collider.maxZ + zRad;
                            }
                        }
                    }
                }
            }





        }

        return new Vector3d(xCollision,yCollision,zCollision);
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
