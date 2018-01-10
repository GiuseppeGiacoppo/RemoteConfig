package me.giacoppo.remoteconfig;

import android.support.annotation.Nullable;

public final class HttpGetResourceModule<T> implements RemoteResource.GetterModule<T> {
    private final RemoteConfigRepository<T> repo;
    private final Class<T> classOfConfig;

    HttpGetResourceModule(Class<T> classOfConfig, RemoteConfigRepository<T> repo) {
        this.repo = repo;
        this.classOfConfig = classOfConfig;
    }

    @Override
    public void find(final RemoteConfig.Request request, final RemoteResource.ResponseListener<T> listener) {
        Utilities.requireNonNull(request, RemoteConfigMessages.NOT_VALID_REQUEST);
        if (request.getCacheExpiration() > 0) {
            // avoid network call if fetched config is still valid
            long lastFetchedTimestamp = repo.getLastFetchedTimestamp();
            if (System.currentTimeMillis() - lastFetchedTimestamp < request.getCacheExpiration()) {
                Logger.log(Logger.DEBUG, "Cached fetched config still valid");
                // last fetched config is still valid
                if (listener != null) {
                    listener.onSuccess(repo.getLastFetched());
                }

                return;
            }
        }

        NetworkModule.get(request.getUrl(), request.getHeaders(), new NetworkModule.HttpCallback() {
            @Override
            public void onSuccess(@Nullable String value) {
                if (value != null) {
                    T config = Utilities.Json.from(value, classOfConfig);

                    Logger.log(Logger.DEBUG, "Successfully fetched and stored config from " + request.getUrl());

                    if (listener != null)
                        listener.onSuccess(config);
                } else {
                    Logger.log(Logger.DEBUG, "Fetched value is null");
                    if (listener != null)
                        listener.onError(new IllegalArgumentException("Fetched value is null"));
                }
            }

            @Override
            public void onError(final Throwable t) {
                Logger.log(Logger.DEBUG, "Error fetching config from url " + request.getUrl());
                Logger.log(Logger.DEBUG, t.getMessage());

                if (listener != null)
                    listener.onError(t);
            }
        });
    }
}
