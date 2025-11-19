package com.finderfeed.noise_combiner.registry;

public abstract class ObjectType<T> {

    private String registryId;

    public ObjectType(String registryId){
        this.registryId = registryId;
    }

    public String getRegistryId() {
        return registryId;
    }

    @Override
    public String toString() {
        return registryId;
    }

}
