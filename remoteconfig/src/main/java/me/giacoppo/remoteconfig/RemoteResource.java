package me.giacoppo.remoteconfig;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.observers.DisposableCompletableObserver;
import me.giacoppo.remoteconfig.core.CacheStrategy;
import me.giacoppo.remoteconfig.core.ILocalRepository;

/**
 * a Remote resource is a container for remote configurations. You can configure remote repository
 * for fetching the conf, local repository for storing it, set default conf, fetch and activate and much more.
 *
 * @param <T>
 */
public final class RemoteResource<T> {
    private ILocalRepository<T> localRepository;
    private Callable<T> remoteRepository;
    private CacheStrategy cacheStrategy;

    public void initialize(RemoteConfigSettings<T> settings) {
        localRepository = settings.getInternalRepository();
        remoteRepository = settings.getRemoteRepository();
        cacheStrategy = settings.getCacheStrategy();
    }

    /**
     * Set a default configuration. This conf will be stored in local repository and will be returned
     * if no active conf is found.
     *
     * @param config config that will be stored as default value
     */
    public void setDefaultConfig(@NonNull T config) {
        checkInitialization();
        localRepository.storeDefault(config);
    }

    /**
     * Fetch the remote configuration and store it as fetched config.
     * If a previous fetched config is present, the library will check the max age defined with {@link CacheStrategy}
     *
     * @return It's possible to subscribe to a {@link FetchSuccess}, {@link FetchError} or both {@link FetchResponse}
     */
    public FetchResult fetch() {
        checkInitialization();
        if (System.currentTimeMillis() - localRepository.getFetchedTimestamp() < cacheStrategy.maxAgeInMillis())
            return new FetchResult(Completable.complete());

        final SingleExecutor<T> runner = new SingleExecutor<>();
        Completable c = runner.execute(remoteRepository).flatMapCompletable(t -> {
            localRepository.storeFetched(t, System.currentTimeMillis());
            return Completable.complete();
        });

        return  new FetchResult(c);
    }

    /**
     * Activate last fetched config, if present. Otherwise does nothing
     */
    public void activateFetched() {
        checkInitialization();
        localRepository.activateConfig();
    }

    /**
     * Return last activated config, if present. Otherwise will return the default config, or null
     *
     * @return last activated config, if present. Otherwise will return the default config, or null
     */
    @Nullable
    public T get() {
        checkInitialization();
        return localRepository.getConfig();
    }

    /**
     * Clear default, fetched and activated config
     */
    public void clear() {
        checkInitialization();
        localRepository.clear();
    }

    private void checkInitialization() {
        if (localRepository == null || remoteRepository == null || cacheStrategy == null)
            throw new IllegalStateException(RemoteConfigMessages.REMOTE_RESOURCE_NOT_INITIALIZED);
    }

    public interface FetchSuccess {
        void onSuccess();
    }

    public interface FetchError {
        void onError(Throwable t);
    }

    public interface FetchResponse extends FetchSuccess, FetchError {
    }

    /**
     * Manages the result of the fetch operation and let add listeners
     */
    public class FetchResult {
        private final Completable fetchCompletable;

        private FetchResult(@NonNull Completable fetchCompletable) {
            this.fetchCompletable = fetchCompletable.cache();
            this.fetchCompletable.subscribe(new DisposableCompletableObserver() {
                @Override
                public void onComplete() {
                    Logger.log(Logger.DEBUG, "Fetch complete");
                }

                @Override
                public void onError(Throwable e) {
                    Logger.log(Logger.DEBUG, "Fetch failed with exception: "+e.getMessage());
                }
            });
        }

        public void addSuccessListener(final FetchSuccess success) {
            onResponse(success, null);
        }

        public void addErrorListener(final FetchError error) {
            onResponse(null, error);
        }

        public void addResponseListener(final FetchResponse response) {
            onResponse(response, response);
        }

        private void onResponse(final FetchSuccess success, final FetchError error) {
            fetchCompletable.subscribe(new DisposableCompletableObserver() {
                @Override
                public void onComplete() {
                    if (success != null)
                        success.onSuccess();
                }

                @Override
                public void onError(Throwable e) {
                    if (error != null)
                        error.onError(e);
                }
            });
        }
    }
}
