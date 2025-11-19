package com.finderfeed.noise_combiner.registry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Supplier;

public class ObjectTypeRegistry<O extends ObjectType<? extends T>, T> {

    private LinkedHashMap<String, O> objectTypes = new LinkedHashMap<>();

    public <D extends ObjectType<? extends T>> D register(D objectType){
        String id = objectType.getRegistryId();
        if (this.objectTypes.containsKey(id)){
            throw new RuntimeException("Object with id: " + id + " is already registered!");
        }
        this.objectTypes.put(id, (O) objectType);
        return objectType;
    }

    public O getObjectType(String id){
        return objectTypes.get(id);
    }

    public List<O> getObjectTypes(){
        var collection = objectTypes.values();
        return collection.stream().toList();
    }

}
