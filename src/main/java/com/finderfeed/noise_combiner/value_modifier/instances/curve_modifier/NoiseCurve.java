package com.finderfeed.noise_combiner.value_modifier.instances.curve_modifier;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.JsonSerializable;
import com.finderfeed.noise_combiner.NoiseLayer;
import com.finderfeed.util.MathUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class NoiseCurve implements JsonSerializable<NoiseCurve> {

    private List<NoiseCurvePoint> points = new ArrayList<>();

    public NoiseCurve(){
        //Initialized border points
        this.points.add(new NoiseCurvePoint(new Vector2f(-1,-1)));
        this.points.add(new NoiseCurvePoint(new Vector2f(1,1)));
    }

    public int getPointCount(){
        return points.size();
    }

    public void addPoint(int selectedPointIndex){
        if (selectedPointIndex >= this.getPointCount() - 1){
            return;
        }

        var point = this.points.get(selectedPointIndex);
        var nextPoint = this.points.get(selectedPointIndex + 1);

        var ppos = point.getPos();
        var nppos = nextPoint.getPos();

        Vector2f pos = new Vector2f(
                (ppos.x + nppos.x) / 2,
                (ppos.y + nppos.y) / 2
        );

        NoiseCurvePoint p = new NoiseCurvePoint(pos);

        this.points.add(selectedPointIndex + 1, p);
    }

    public void removePoint(int selectedPointIndex){
        if (selectedPointIndex <= 0 || selectedPointIndex >= this.getPointCount() - 1){
            return;
        }
        this.points.remove(selectedPointIndex);
    }

    public NoiseLayer getLayerAtPoint(int pointIndex){
        return points.get(pointIndex).getLayer();
    }

    public Vector2f getPointPos(int pointIndex){
        return points.get(pointIndex).getPos();
    }

    public void setPointPos(int pointIndex, Vector2f pointPos){

        pointPos.x = MathUtil.clamp(pointPos.x, -1, 1);
        pointPos.y = MathUtil.clamp(pointPos.y, -1, 1);

        if (pointIndex == 0 || pointIndex == this.getPointCount() - 1){
            pointPos.x = this.getPointPos(pointIndex).x; //border points cannot be moved horizontally
        }else{
            var next = points.get(pointIndex + 1);
            var prev = points.get(pointIndex - 1);
            pointPos.x = MathUtil.clamp(pointPos.x,
                    prev.getPos().x,
                    next.getPos().x
            );
        }

        var point = this.points.get(pointIndex);
        point.setPos(pointPos);

    }

    public float getValue(ComputationContext context, float value){
        value = MathUtil.clamp(value, -1, 1);

        for (int i = 0; i < points.size() - 1; i++){

            var curPoint = points.get(i);
            var nextPoint = points.get(i + 1);

            var curPointPos = curPoint.getPos();
            var nextPointPos = nextPoint.getPos();

            if (curPointPos.x <= value && nextPointPos.x >= value){

                float distance = nextPointPos.x - curPointPos.x;
                if (distance == 0){
                    distance += 0.00001f; // just so it doesn't blow up
                }
                float currentDistance = value - curPointPos.x;

                float localP = currentDistance / distance;

                var currentPointValue = curPoint.getLayer().computeValue(context);
                var nextPointValue = nextPoint.getLayer().computeValue(context);

                var result = MathUtil.lerp(currentPointValue, nextPointValue, localP);

                return result;
            }

        }

        return -1;
    }

    @Override
    public void serializeToJson(JsonObject object) {

        JsonArray array = new JsonArray();

        for (int i = 0; i < points.size(); i++){

            var point = points.get(i);

            var pointPos = point.getPos();
            var layer = point.getLayer();

            JsonObject obj = new JsonObject();
            obj.addProperty("x", pointPos.x);
            obj.addProperty("y", pointPos.y);

            JsonObject layerObj = new JsonObject();
            layer.serializeToJson(layerObj);
            obj.add("underlyingNoiseLayer", layerObj);

            array.add(obj);

        }

        object.add("points", array);

    }

    @Override
    public void deserializeFromJson(JsonObject jsonObject) {
        this.points.clear();

        var points = jsonObject.getAsJsonArray("points");
        for (var element : points){
            var obj = element.getAsJsonObject();
            var x = obj.get("x").getAsFloat();
            var y = obj.get("y").getAsFloat();

            NoiseCurvePoint point = new NoiseCurvePoint(new Vector2f(x, y));
            var layer = point.getLayer();
            var layerObj = obj.getAsJsonObject("underlyingNoiseLayer");
            layer.deserializeFromJson(layerObj);

            this.points.add(point);
        }


    }


}
