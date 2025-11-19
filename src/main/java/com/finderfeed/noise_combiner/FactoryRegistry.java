package com.finderfeed.noise_combiner;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Supplier;

public class FactoryRegistry<T> {

    private LinkedHashMap<String, Supplier<? extends T>> factories = new LinkedHashMap<>();

    public <D extends T> Supplier<D> register(String id, Supplier<D> factory){
        this.factories.put(id, factory);
        return factory;
    }

    public <D extends T> Supplier<D> getFactory(String id){
        return (Supplier<D>) factories.get(id);
    }

    public List<Supplier<? extends T>> getFactories(){
        var collection = factories.values();
        return collection.stream().toList();
    }

}
