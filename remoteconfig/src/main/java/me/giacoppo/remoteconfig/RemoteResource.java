package me.giacoppo.remoteconfig;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Wrapper of remote configuration
 *
 * @param <T>
 */
public final class RemoteResource<T> {
    private final RemoteConfigRepository<T> repo;
    private final Class<T> classOfResource;

    RemoteResource(Class<T> classOfResource, GetterModule<T> getter) {
        repo = RemoteConfigRepository.create(RemoteConfig.Holder.context.get(), classOfResource);
        this.classOfResource = classOfResource;
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
     * Fetch a config using a custom Getter Module
     *
     * @param getter   Implemented GetterModule
     * @param callback Callback
     */
    public void fetch(@NonNull final GetterModule<T> getter, @Nullable final RemoteConfig.Callback callback) {
        getter.find(new ResponseListener<T>() {
            @Override
            public void onSuccess(T config) {
                // update fetched config
                repo.setLastFetchedConfig(config);

                if (callback != null)
                    // notify on ui thread
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess();
                        }
                    });
            }

            @Override
            public void onError(final Throwable t) {
                // notify on ui thread
                if (callback != null)
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
     * Fetch a config using a default Http Getter Module
     * @param httpGetRequest Library Http Getter Module based on HttpRequest object
     * @param callback Callback
     */
    public void fetch(@NonNull final RemoteConfig.HttpRequest httpGetRequest, @Nullable final RemoteConfig.Callback callback) {
        GetterModule<T> getter = new HttpGetResourceModule<>(httpGetRequest, repo, classOfResource);
        fetch(getter, callback);
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

    public interface GetterModule<T> {
        void find(ResponseListener<T> callback);
    }

    public interface ResponseListener<T> {
        void onSuccess(T data);

        void onError(Throwable t);
    }
}
