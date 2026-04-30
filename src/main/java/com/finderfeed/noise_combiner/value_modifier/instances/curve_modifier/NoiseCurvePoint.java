package com.finderfeed.noise_combiner.value_modifier.instances.curve_modifier;

import com.finderfeed.noise_combiner.NoiseLayer;
import com.finderfeed.noise_combiner.noise.instances.FDConstantValueNoise;
import org.joml.Vector2f;

public class NoiseCurvePoint {

    private Vector2f position;
    private NoiseLayer layer;

    public NoiseCurvePoint(Vector2f position){
        this.position = position;
        this.layer = new NoiseLayer();
        this.layer.setNoise(new FDConstantValueNoise(position.y));
    }

    public void setPos(Vector2f position){
        this.position = position;
        var noise = this.layer.getNoise();
        if (noise instanceof FDConstantValueNoise constantValueNoise){
            constantValueNoise.constantValue = position.y;
        }
    }

    public Vector2f getPos(){
        return position;
    }

    public NoiseLayer getLayer() {
        return layer;
    }

}
