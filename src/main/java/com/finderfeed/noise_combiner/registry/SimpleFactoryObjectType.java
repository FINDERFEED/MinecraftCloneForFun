package com.finderfeed.noise_combiner.registry;

import java.util.function.Supplier;

public class SimpleFactoryObjectType<T> extends ObjectType<T> {

    private Supplier<T> factory;

    public SimpleFactoryObjectType(String registryId, Supplier<T> factory) {
        super(registryId);
        this.factory = factory;
    }

    public T generateObject(){
        return factory.get();
    }

}
