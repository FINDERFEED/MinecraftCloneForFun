package com.finderfeed.noise_combiner;

import com.google.gson.JsonObject;

public interface JsonSerializable<T> {

    void serializeToJson(JsonObject object);

    void deserializeFromJson(JsonObject jsonObject);

}
