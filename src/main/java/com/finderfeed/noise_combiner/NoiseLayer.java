package com.finderfeed.noise_combiner;

import com.finderfeed.noise_combiner.noise.FDNoise;
import com.finderfeed.noise_combiner.noise.NoiseRegistry;
import com.finderfeed.noise_combiner.registry.ObjectType;
import com.finderfeed.noise_combiner.registry.SimpleFactoryObjectType;
import com.finderfeed.noise_combiner.value_modifier.FDValueModifier;
import com.finderfeed.noise_combiner.value_modifier.NoiseValueModifierRegistry;
import com.google.gson.JsonArray;
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

    public void setNoise(FDNoise<?> noise) {
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

        JsonObject noiseObject = new JsonObject();

        var noiseType = this.getNoise().getObjectType();
        noiseObject.addProperty("noiseType", noiseType.getRegistryId());
        this.getNoise().serializeToJson(noiseObject);


        JsonArray noiseModifiers = new JsonArray();
        for (var modifier : this.getValueModifiers()){
            JsonObject modifierJson = new JsonObject();
            modifierJson.addProperty("modifierType", modifier.getObjectType().getRegistryId());
            modifier.serializeToJson(modifierJson);
            noiseModifiers.add(modifierJson);
        }
        object.add("noise", noiseObject);
        object.add("noiseModifiers", noiseModifiers);

    }

    @Override
    public void deserializeFromJson(JsonObject jsonObject) {
        this.layerName = jsonObject.get("layerName").getAsString();

        var noise = jsonObject.get("noise").getAsJsonObject();
        var ntype = noise.get("noiseType").getAsString();
        this.setNoise(NoiseRegistry.NOISE_REGISTRY.getObjectType(ntype).generateObject());
        this.getNoise().deserializeFromJson(noise);

        this.valueModifiers.clear();
        JsonArray array = jsonObject.getAsJsonArray("noiseModifiers");
        for (var mod : array){
            var modifierObject = mod.getAsJsonObject();
            var id = modifierObject.get("modifierType").getAsString();
            var modifier = NoiseValueModifierRegistry.VALUE_MODIFIERS.getObjectType(id).generateObject();
            modifier.deserializeFromJson(modifierObject);
            this.valueModifiers.add(modifier);
        }
    }

}
