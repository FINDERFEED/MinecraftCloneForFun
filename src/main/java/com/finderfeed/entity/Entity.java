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
        var colliders = this.collectColliders(movement);
        AABox box = this.getBox(this.position);
        Vector3d pos = this.collide(box,colliders,movement);

        double xDiff = pos.x - this.position.x;
        double zDiff = pos.z - this.position.z;
        this.position.add(xDiff,0,zDiff);
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
        return 0.5f;
    }



    private List<AABox> collectColliders(Vector3d moveVector){
        Vector3i v = this.getBlockPos();
        List<AABox> boxes = new ArrayList<>();
        int testRad = 5;
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
                        boxes.add(new AABox(
                                p.x,p.y,p.z,
                                p.x + 1,p.y + 1,p.z + 1
                        ));
                    }
                }
            }
        }
        return boxes;
    }

    public Vector3d collide(AABox box,List<AABox> colliders, Vector3d moveVector){

        Vector3d center = box.center();
        Vector3d finalMove = center.add(moveVector);

        for (AABox collider : colliders){

            double xcb = collider.minX;
            double xb = box.maxX;
            double d = MathUtil.clamp(xcb - xb,0,moveVector.x);



            double zm = (d / moveVector.x) * moveVector.z;

            double boxZMin = box.minZ + zm;
            double boxZMax = box.maxZ + zm;

            if (boxZMin < collider.minZ && boxZMax < collider.minZ || boxZMin > collider.maxZ && boxZMax > collider.maxZ){

            }else{
                finalMove.x = center.x + d;
            }



        }

        return finalMove;
    }




    private Double collideX(AABox box,List<AABox> colliders, Vector3d moveVector){
        double xdc = (box.maxX - box.minX) / 2;
        double xc = box.minX + xdc;

        double xd = moveVector.x;
        double yd = moveVector.y;
        double zd = moveVector.z;

        double zs = box.minZ;
        double ze = box.minZ + zd;

        double ys = box.minY;
        double ye = ys + yd;

        if (xd == 0) return null;

        double x = xd > 0 ? box.maxX : box.minX;
        double nx = x + xd;

        double dist = Double.MAX_VALUE;
        Double returnValue = null;

        for (AABox b : colliders){
            double bx;
            if (xd > 0){
                bx = b.minX;
                double coord = bx - xdc;
                double d = Math.abs(xc - coord);
                if (returnValue != null){
                    if (d >= dist){
                        continue;
                    }
                }

                if (  !(nx > bx && xc < bx) ) {
                    continue;
                }
                double diff = (bx - x);
                double p = diff / xd;
                double ly = MathUtil.lerp(ys,ye,p);
                double lyy = ly + (box.maxY - box.minY);
                if (ly < b.minY && lyy < b.minY || ly > b.maxY && lyy > b.maxY){
                    continue;
                }

                double lz = MathUtil.lerp(zs,ze,p);
                double lzz = lz + (box.maxZ - box.minZ);
                if (lz < b.minZ && lzz < b.minZ || lz > b.maxZ && lzz > b.maxZ){
                    continue;
                }

                if (d < dist){
                    dist = d;
                    returnValue = coord;
                }
            }else{
                bx = b.maxX;

                double coord = bx + xdc;
                double d = Math.abs(xc - coord);
                if (returnValue != null){
                    if (d >= dist){
                        continue;
                    }
                }


                if ( !(nx < bx && xc > bx) ) {
                    continue;
                }
                double diff = (x - bx);
                double p = diff / -xd;
                double ly = MathUtil.lerp(ys,ye,p);
                double lyy = ly + (box.maxY - box.minY);
                if (ly < b.minY && lyy < b.minY || ly > b.maxY && lyy > b.maxY){
                    continue;
                }

                double lz = MathUtil.lerp(zs,ze,p);
                double lzz = lz + (box.maxZ - box.minZ);
                if (lz < b.minZ && lzz < b.minZ || lz > b.maxZ && lzz > b.maxZ){
                    continue;
                }

                if (d < dist){
                    dist = d;
                    returnValue = coord;
                }
            }
        }
        return returnValue;
    }

    private Double collideZ(AABox box,List<AABox> colliders, Vector3d moveVector){
        if (moveVector.z == 0) return null;
        double zdiff = box.maxZ - box.minZ;
        double zc = box.minZ + zdiff / 2;


        double xd = moveVector.x;
        double yd = moveVector.y;
        double zd = moveVector.z;


        double ys = box.minY;
        double ye = box.minY + yd;
        double xs = box.minX;
        double xe = box.minX + xd;

        double ydiff = box.maxY - box.minY;
        double xdiff = box.maxX - box.minX;

        double zb = zd > 0 ? box.maxZ : box.minZ;
        double nz = zb + zd;

        double dist = Double.MAX_VALUE;
        Double returnValue = null;

        for (AABox b : colliders){
            double bBorder;
            if (zd > 0){
                bBorder = b.minZ;
                double d = bBorder - zc;
                if (returnValue != null){
                    if (d >= dist){
                        continue;
                    }
                }
                if (!(zc < bBorder && nz > bBorder)) continue;
                double diff = bBorder - zb;
                double p = diff / zd;

                double ly = MathUtil.lerp(ys,ye,p);
                double lye = ly + ydiff;
                if (ly < b.minY && lye < b.minY || ly > b.maxY && lye > b.maxY) continue;

                double lx = MathUtil.lerp(xs,xe,p);
                double lxe = lx + xdiff;
                if (lx < b.minX && lxe < b.minX || lx > b.maxX && lxe > b.maxX) continue;

                if (d < dist){
                    returnValue = bBorder - zdiff / 2;
                    dist = d;
                }
            }else{
                bBorder = b.maxZ;
                double d = zc - bBorder;
                if (returnValue != null){
                    if (d >= dist){
                        continue;
                    }
                }
                if (!(zc < bBorder && nz > bBorder)) continue;
                double diff = zb - bBorder;
                double p = diff / -zd;

                double ly = MathUtil.lerp(ys,ye,p);
                double lye = ly + ydiff;
                if (ly < b.minY && lye < b.minY || ly > b.maxY && lye > b.maxY) continue;

                double lx = MathUtil.lerp(xs,xe,p);
                double lxe = lx + xdiff;
                if (lx < b.minX && lxe < b.minX || lx > b.maxX && lxe > b.maxX) continue;

                if (d < dist){
                    returnValue = bBorder + zdiff / 2;
                    dist = d;
                }

            }
        }
        return returnValue;
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
