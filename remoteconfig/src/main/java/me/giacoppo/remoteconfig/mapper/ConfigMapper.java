package me.giacoppo.remoteconfig.mapper;


public interface ConfigMapper<T> {
    String toString(T config);
    T fromString(String s);
}
