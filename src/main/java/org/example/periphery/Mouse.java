package org.example.periphery;

public class Mouse {

    public float x = 0;
    public float y = 0;
    public float xo = 0;
    public float yo = 0;

    public float dx = 0;
    public float dy = 0;

    public void update(float x,float y){
        this.xo = this.x;
        this.yo = this.y;
        this.x = x;
        this.y = y;
        this.dx = x - xo;
        this.dy = y - yo;
    }

}
