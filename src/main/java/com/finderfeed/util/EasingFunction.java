package com.finderfeed.util;

import it.unimi.dsi.fastutil.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public class EasingFunction {

    private List<Point> points = new ArrayList<>();
    private float length = 0;

    public float lerp(float x){

        var pair = this.currentAndNext(x);

        float v1 = pair.first().x;
        float v2 = pair.second().x;

        float d = v2 - v1;

        float dist = MathUtil.clamp(x - v1,0,d);

        float p = MathUtil.easeInOut(dist / d);


        return MathUtil.lerp(pair.first().y.get(),pair.second().y.get(),p);
    }

    private Pair<Point,Point> currentAndNext(float x){
        if (x < 0){
            return Pair.of(points.getFirst(),points.get(1));
        }else if (x >= length){
            return Pair.of(points.get(points.size() - 2),points.getLast());
        }

        for (int i = 1; i < points.size(); i++){
            Point p1 = points.get(i);
            if (x <= p1.x){
                return Pair.of(points.get(i - 1),points.get(i));
            }
        }

        return null;
    }

    public EasingFunction addPoint(float x,float y){
        return this.addPoint(new Point(x,()->y));
    }

    public EasingFunction addPoint(float x,Supplier<Float> y){
        return this.addPoint(new Point(x,y));
    }

    public EasingFunction addPoint(Point point){

        if (point.x < 0) {
            throw new RuntimeException("X cannot be less than zero");
        }

        this.points.add(point);

        this.points.sort(Comparator.comparingDouble(v->v.x));

        this.length = this.points.getLast().x;

        return this;
    }





   public record Point(float x, Supplier<Float> y){

   }

}
