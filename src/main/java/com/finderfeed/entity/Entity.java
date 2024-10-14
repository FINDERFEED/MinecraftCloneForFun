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

//        this.verticalCollision(movement);
        this.horizontalCollision(movement);

    }

    private void horizontalCollision(Vector3d movement){
        AABox box = this.getBox(this.position);
        var colliders = this.collectColliders(box,movement);

        Vector3d pos = this.collide(box,colliders,movement);



        this.position = pos;
    }



    private List<MoveCollider> collectColliders(AABox entityBox,Vector3d moveVector){
        Vector3i v = this.getBlockPos();
        List<MoveCollider> boxes = new ArrayList<>();
        int testRad = 5;

        double ex = 0;
        double ey = 0;
        double ez = 0;
        var center = entityBox.center();

        int x1 = (int) Math.floor(center.x);
        int x2 = (int) Math.floor(moveVector.x);
        int z1 = (int) Math.floor(center.z);
        int z2 = (int) Math.floor(moveVector.z);
        int y1 = (int) Math.floor(center.y);
        int y2 = (int) Math.floor(moveVector.y);
//
//        for (int x = Math.min(0,x2) - 1;x <= Math.max(0,x2) + 1;x++){
//            for (int y = Math.min(0,y2) - 1;y <= Math.max(0,y2) + 1;y++){
//                for (int z = Math.min(0,z2) - 1;z <= Math.min(0,z2) + 1;z++){

        for (int x = -testRad;x <= testRad;x++){
            for (int y = -testRad;y <= testRad;y++){
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
                        double by;
                        double bz;
                        double ex1;
                        double ey1;
                        double ez1;

                        if (moveVector.y >= 0){
                            by = box.minY;
                            ey = entityBox.maxY;
                            ey1 = ey + moveVector.y;

                        }else{
                            by = box.maxY;
                            ey = entityBox.minY;
                            ey1 = ey + moveVector.y;

                        }

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

                            double value1 = (by - ey) * (ez1 - ez) - (bz - ez) * (ey1 - ey);

                            if (value1 < 0) {
                                collider.type = MoveCollider.Y_COLLIDING;
                                collider.wall = by;

                            }else{
                                collider.type = MoveCollider.Z_COLLIDING;
                                collider.wall = bz;
                            }
                        }else{

                            double value1 = (by - ey) * (ex1 - ex) - (bx - ex) * (ey1 - ey);

                            if (value1 < 0) {
                                collider.type = MoveCollider.Y_COLLIDING;
                                collider.wall = by;

                            }else{
                                collider.type = MoveCollider.X_COLLIDING;
                                collider.wall = bx;
                            }
                        }

                        boxes.add(collider);

                    }
                }
            }
        }


        double finalEy = ey;
        double finalEx = ex;
        double finalEz = ez;
        boxes.sort(Comparator.comparingDouble(c->{
            if (c.type == MoveCollider.X_COLLIDING){
                return Math.abs(c.wall - finalEx);
            }else if (c.type == MoveCollider.Z_COLLIDING){
                return Math.abs(c.wall - finalEz);
            }else{
                return Math.abs(c.wall - finalEy);
            }
        }));


        return boxes;
    }

    public Vector3d collide(AABox box,List<MoveCollider> colliders, Vector3d moveVector){

        Vector3d center = box.center();


        Vector3d finalMove = center.add(moveVector);
        finalMove.y = box.minY + moveVector.y;

        double xd = moveVector.x;
        double yd = moveVector.y;
        double zd = moveVector.z;

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
                            double ym = p * moveVector.y;
                            if (!(box.minY + ym <= collider.minY && box.maxY + ym <= collider.minY || box.minY + ym >= collider.maxY && box.maxY + ym >= collider.maxY)) {
                                xCollision = collider.minX - xRad;
                                moveVector.x = 0;

                            }
                        }
                    }
                } else {
                    if (!(box.minX + xd > collider.maxX && box.maxX + xd > collider.maxX) && box.minX >= collider.maxX) { //it did "collide" on X
                        double delta = box.minX - collider.maxX;
                        double p = delta / Math.abs(xd);
                        double zm = p * moveVector.z;
                        if (!(box.minZ + zm <= collider.minZ && box.maxZ + zm <= collider.minZ || box.minZ + zm >= collider.maxZ && box.maxZ + zm >= collider.maxZ)) {
                            double ym = p * moveVector.y;
                            if (!(box.minY + ym <= collider.minY && box.maxY + ym <= collider.minY || box.minY + ym >= collider.maxY && box.maxY + ym >= collider.maxY)) {
                                xCollision = collider.maxX + xRad;

                                moveVector.x = 0;

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
                            double ym = p * moveVector.y;
                            if (!(box.minY + ym <= collider.minY && box.maxY + ym <= collider.minY || box.minY + ym >= collider.maxY && box.maxY + ym >= collider.maxY)) {
                                moveVector.z = 0;
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
                            double ym = p * moveVector.y;
                            if (!(box.minY + ym <= collider.minY && box.maxY + ym <= collider.minY || box.minY + ym >= collider.maxY && box.maxY + ym >= collider.maxY)) {
                                moveVector.z = 0;
                                zCollision = collider.maxZ + zRad;
                            }
                        }
                    }
                }
            }else{
                if (yd > 0) {
                    if (!(box.maxY + yd < collider.minY && box.minY + yd < collider.minY) && box.maxY <= collider.minY) { //it did "collide" on Y
                        double delta = collider.minY - box.maxY;

                        double p = delta / Math.abs(yd);
                        double xm = p * moveVector.x;
                        if (!(box.minX + xm <= collider.minX && box.maxX + xm <= collider.minX || box.minX + xm >= collider.maxX && box.maxX + xm >= collider.maxX)) {
                            double zm = moveVector.z;
                            if (!(box.minZ + zm <= collider.minZ && box.maxZ + zm <= collider.minZ || box.minZ + zm >= collider.maxZ && box.maxZ + zm >= collider.maxZ)) {
                                moveVector.y = 0;

                                yCollision = collider.minY - this.getHeight();
                            }
                        }
                    }
                } else {
                    if (!(box.minY + yd > collider.maxY && box.maxY + yd > collider.maxY) && box.minY >= collider.maxY) { //it did "collide" on Y
                        double delta = box.minY - collider.maxY;
                        double p = delta / Math.abs(yd);
                        double xm = p * moveVector.x;
                        if (!(box.minX + xm <= collider.minX && box.maxX + xm <= collider.minX || box.minX + xm >= collider.maxX && box.maxX + xm >= collider.maxX)) {
                            double zm = p * moveVector.z;
                            if (!(box.minZ + zm <= collider.minZ && box.maxZ + zm <= collider.minZ || box.minZ + zm >= collider.maxZ && box.maxZ + zm >= collider.maxZ)) {
                                moveVector.y = 0;
                                onGround = true;
                                yCollision = collider.maxY;
                            }
                        }
                    }
                }
            }





        }

        return new Vector3d(xCollision,yCollision,zCollision);
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
