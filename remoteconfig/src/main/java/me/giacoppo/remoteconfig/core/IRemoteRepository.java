package me.giacoppo.remoteconfig.core;

import io.reactivex.Single;

public interface IRemoteRepository<T> {
    Single<T> fetch();
}
