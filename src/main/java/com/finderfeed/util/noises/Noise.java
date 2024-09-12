package com.finderfeed.util.noises;

public interface Noise {


    default float get(float x,float y,float z,int octaves){
        float val = this.get(x,y,z);
        float ampl = 0.5f;
        for (int i = 0; i < octaves;i++){
            x *= 2;
            y *= 2;
            z *= 2;
            val += this.get(x,y,z) * ampl;
            val /= (1 + ampl);
            ampl *= 0.5f;
        }
        return val;
    }

    float get(float x,float y,float z);

}
