package me.giacoppo.remoteconfig.core;

import io.reactivex.Single;

/**
 * Defines method used in remote resource to fetch updated config
 * @param <T>
 */
public interface IRemoteRepository<T> {
    Single<T> fetch();
}
