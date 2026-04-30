package com.finderfeed.noise_combiner.value_modifier.instances.curve_modifier;

import com.finderfeed.noise_combiner.ComputationContext;
import com.finderfeed.noise_combiner.registry.ObjectType;
import com.finderfeed.noise_combiner.value_modifier.FDValueModifier;
import com.finderfeed.noise_combiner.value_modifier.NoiseValueModifierRegistry;
import com.google.gson.JsonObject;

public class CurveModifier extends FDValueModifier<CurveModifier> {

    public NoiseCurve noiseCurve;

    public CurveModifier(){
        this.noiseCurve = new NoiseCurve();
    }

    @Override
    public float transformValue(ComputationContext computationContext, float value) {
        return noiseCurve.getValue(computationContext, value);
    }

    @Override
    public ObjectType<CurveModifier> getObjectType() {
        return NoiseValueModifierRegistry.NOISE_CURVE;
    }

    @Override
    public void serializeToJson(JsonObject object) {

        JsonObject noiseCurve = new JsonObject();
        this.noiseCurve.serializeToJson(noiseCurve);

        object.add("noiseCurve",noiseCurve);

    }

    @Override
    public void deserializeFromJson(JsonObject jsonObject) {
        var curve = jsonObject.get("noiseCurve").getAsJsonObject();
        this.noiseCurve.deserializeFromJson(curve);
    }

}
