package me.giacoppo.remoteconfig.core;

/**
 * Define methods used in remote resource used to store, get and activate config
 * @param <T>
 */
public interface ILocalRepository<T> {
    void storeDefault(T val);
    void storeFetched(T val, long timestamp);
    long getFetchedTimestamp();
    T getConfig();
    void activateConfig();
    void clear();
}
