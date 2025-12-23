package com.finderfeed.noise_combiner;

import com.finderfeed.noise_combiner.layer_combiner.FDNoiseValueCombiner;
import com.finderfeed.noise_combiner.layer_combiner.NoiseValueCombinerRegistry;
import com.finderfeed.noise_combiner.layer_combiner.instances.SumValuesCombiner;
import com.google.gson.JsonObject;

public class NoiseCombinationLayer implements JsonSerializable<NoiseCombinationLayer> {

    private NoiseLayer noiseLayer;
    private FDNoiseValueCombiner<?> combiner;

    public NoiseCombinationLayer(){
        this.combiner = new SumValuesCombiner();
        this.noiseLayer = new NoiseLayer();
    }

    public FDNoiseValueCombiner<?> getCombiner() {
        return combiner;
    }

    public NoiseLayer getNoiseLayer() {
        return noiseLayer;
    }

    public void setCombiner(FDNoiseValueCombiner<?> combiner) {
        this.combiner = combiner;
    }

    @Override
    public void serializeToJson(JsonObject object) {
        JsonObject noiseLayerObject = new JsonObject();
        this.noiseLayer.serializeToJson(noiseLayerObject);
        object.add("noiseLayer", noiseLayerObject);

        var combinerType = this.combiner.getType();
        String regId = combinerType.getRegistryId();
        JsonObject combinerObject = new JsonObject();
        combinerObject.addProperty("type", regId);
        this.getCombiner().serializeToJson(combinerObject);
        object.add("combiner", combinerObject);

    }

    @Override
    public void deserializeFromJson(JsonObject jsonObject) {
        this.noiseLayer.deserializeFromJson(jsonObject.getAsJsonObject("noiseLayer"));

        var combinerObject = jsonObject.get("combiner").getAsJsonObject();
        String regId = combinerObject.get("type").getAsString();
        var type = NoiseValueCombinerRegistry.NOISE_VALUE_COMBINERS.getObjectType(regId);
        this.combiner = type.generateObject();
        this.combiner.deserializeFromJson(combinerObject);
    }
}
