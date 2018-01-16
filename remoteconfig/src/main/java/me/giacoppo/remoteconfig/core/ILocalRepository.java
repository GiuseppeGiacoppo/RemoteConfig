package me.giacoppo.remoteconfig.core;

public interface ILocalRepository<T> {
    void storeDefault(T val);
    void storeFetched(T val, long timestamp);
    long getFetchedTimestamp();
    T getConfig();
    void activateConfig();
    void clear();
}
