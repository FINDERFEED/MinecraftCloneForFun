package com.finderfeed.noise_combiner;

import com.finderfeed.noise_combiner.noise.FDNoise;
import com.finderfeed.noise_combiner.noise.NoiseRegistry;
import com.finderfeed.noise_combiner.value_modifier.FDValueModifier;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class NoiseLayer implements JsonSerializable<NoiseLayer> {

    public String layerName = "New noise layer";
    private FDNoise<?> noise;
    private List<FDValueModifier<?>> valueModifiers = new ArrayList<>();

    public NoiseLayer(){
        this.noise = NoiseRegistry.CONSTANT_VALUE.generateObject();
    }

    public boolean moveValueModifier(int id, boolean up){
        if (valueModifiers.size() < 2) return false;

        if (up){
            if (id > 0 && id < valueModifiers.size()){
                FDValueModifier<?> currentModifier = valueModifiers.get(id);
                FDValueModifier<?> upperModifier = valueModifiers.get(id - 1);
                valueModifiers.set(id - 1, currentModifier);
                valueModifiers.set(id, upperModifier);
                return true;
            }
        }else{
            if (id >= 0 && id < valueModifiers.size() - 1){
                FDValueModifier<?> currentModifier = valueModifiers.get(id);
                FDValueModifier<?> downModifier = valueModifiers.get(id + 1);
                valueModifiers.set(id + 1, currentModifier);
                valueModifiers.set(id, downModifier);
                return true;
            }
        }

        return false;
    }

    public float computeValue(ComputationContext computationContext){
        float baseValue = noise.computeNoiseValue(computationContext);
        for (var valueMod : valueModifiers){
            baseValue = valueMod.transformValue(computationContext, baseValue);
        }
        return baseValue;
    }

    public void setNoise(FDNoise noise) {
        this.noise = noise;
    }

    public FDNoise<?> getNoise() {
        return noise;
    }

    public List<FDValueModifier<?>> getValueModifiers() {
        return valueModifiers;
    }

    @Override
    public void serializeToJson(JsonObject object) {
        object.addProperty("layerName", this.layerName);



        String noiseId = NoiseRegistry.NOISE_REGISTRY.getObjectRegistryId(this.noise.getObjectType());
        object.addProperty("noiseType");
    }

    @Override
    public void deserializeFromJson(JsonObject jsonObject) {
        this.layerName = jsonObject.get("layerName").getAsString();

    }
}
