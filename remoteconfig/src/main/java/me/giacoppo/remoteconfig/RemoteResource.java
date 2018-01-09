package me.giacoppo.remoteconfig;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Wrapper of remote configuration
 * @param <T>
 */
public final class RemoteResource<T> {
    private final RemoteConfigSettings<T> remoteConfigSettings;
    private final RemoteConfigRepository<T> repo;

    RemoteResource(RemoteConfigSettings<T> remoteConfigSettings) {
        this.remoteConfigSettings = remoteConfigSettings;
        repo = RemoteConfigRepository.create(RemoteConfig.Holder.context.get(), remoteConfigSettings.getClassOfConfig());
    }

    /**
     * Set a default value
     *
     * @param config config that will be stored as default value
     */
    public void setDefaultConfig(@NonNull T config) {
        repo.setDefaultConfig(config);
    }

    /**
     * Fetch a config from a specific URL
     *
     * @param request  Config request
     * @param callback callback
     */
    public void fetch(@NonNull final RemoteConfig.Request request, @Nullable final RemoteConfig.Callback callback) {
        Utilities.requireNonNull(request, RemoteConfigMessages.NOT_VALID_REQUEST);

        if (request.cacheExpiration > 0) {
            // avoid network call if fetched config is still valid
            long lastFetchedTimestamp = repo.getLastFetchedTimestamp();
            if (System.currentTimeMillis() - lastFetchedTimestamp < request.cacheExpiration) {
                Logger.log(Logger.DEBUG, "Cached fetched config still valid");
                // last fetched config is still valid
                if (callback != null) {
                    callback.onSuccess();
                }

                return;
            }
        }

        NetworkModule.get(request.url, request.headers, new NetworkModule.HttpCallback() {
            @Override
            public void onSuccess(@Nullable String value) {
                if (value != null) {
                    T config = Utilities.Json.from(value, remoteConfigSettings.getClassOfConfig());

                    // update fetched config
                    repo.setLastFetchedConfig(config);

                    Logger.log(Logger.DEBUG, "Successfully fetched and stored config from " + request.url);

                    if (callback != null)
                        // notify on ui thread
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess();
                            }
                        });
                } else {
                    Logger.log(Logger.DEBUG, "Fetched value is null");
                    if (callback != null)
                        // notify on ui thread
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(new IllegalArgumentException("Fetched value is null"));
                            }
                        });
                }
            }

            @Override
            public void onError(final Throwable t) {
                Logger.log(Logger.DEBUG, "Error fetching config from url " + request.url);
                Logger.log(Logger.DEBUG, t.getMessage());

                if (callback != null)
                    // notify on ui thread
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(t);
                        }
                    });
            }
        });
    }

    /**
     * Activate last fetched config
     */
    public void activateFetched() {
        repo.activateFetchedConfig();
    }

    /**
     * Returns last activated config, if present. Otherwise will return the default config, or null
     *
     * @return last activated config, if present. Otherwise will return the default config, or null
     */
    @Nullable
    public T get() {
        return repo.getActivated();
    }

    /**
     * Clear default, fetched and activated config
     */
    public void clear() {
        repo.clearConfig();
    }

}
