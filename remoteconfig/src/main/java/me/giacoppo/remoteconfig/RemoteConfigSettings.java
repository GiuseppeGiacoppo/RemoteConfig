package me.giacoppo.remoteconfig;

import java.util.concurrent.Callable;

import me.giacoppo.remoteconfig.core.CacheStrategy;
import me.giacoppo.remoteconfig.core.ILocalRepository;

/**
 * Settings for Remote Config instance.
 * This class is used to set required components as Local Repository {@link ILocalRepository} and Remote Repository {@link Callable}
 * or optional components as {@link CacheStrategy}
 *
 * @param <T> class of Config
 */
public class RemoteConfigSettings<T> {
    private final Builder<T> builder;
    private final ILocalRepository<T> internalRepository;
    private final Callable<T> remoteRepository;
    private final CacheStrategy cacheStrategy;

    public static class Builder<T> {
        private ILocalRepository<T> internalRepository;
        private Callable<T> remoteRepository;
        private CacheStrategy cacheStrategy;

        public Builder<T> setInternalRepository(ILocalRepository<T> internalRepository) {
            this.internalRepository = internalRepository;
            return this;
        }

        public Builder<T> setRemoteRepository(Callable<T> remoteRepository) {
            this.remoteRepository = remoteRepository;
            return this;
        }

        public Builder<T> setCacheStrategy(CacheStrategy cacheStrategy) {
            this.cacheStrategy = cacheStrategy;
            return this;
        }

        public RemoteConfigSettings<T> build() {
            Utilities.requireNonNull(internalRepository, "internal repository null");
            Utilities.requireNonNull(remoteRepository, "remote repository null");

            if (cacheStrategy == null)
                cacheStrategy = CacheStrategy.DEFAULT_STRATEGY;

            return new RemoteConfigSettings<>(this);
        }
    }

    private RemoteConfigSettings(Builder<T> builder) {
        this.builder = builder;
        this.internalRepository = builder.internalRepository;
        this.remoteRepository = builder.remoteRepository;
        this.cacheStrategy = builder.cacheStrategy;
    }

    public Builder<T> newBuilder() {
        return builder;
    }

    ILocalRepository<T> getInternalRepository() {
        return internalRepository;
    }

    Callable<T> getRemoteRepository() {
        return remoteRepository;
    }

    CacheStrategy getCacheStrategy() {
        return cacheStrategy;
    }
}