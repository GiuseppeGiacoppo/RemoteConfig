package me.giacoppo.remoteconfig;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import me.giacoppo.remoteconfig.core.CacheStrategy;
import me.giacoppo.remoteconfig.core.ILocalRepository;
import me.giacoppo.remoteconfig.core.IRemoteRepository;

/**
 * Wrapper of remote configuration
 *
 * @param <T>
 */
public final class RemoteResource<T> {
    private ILocalRepository<T> internalRepository;
    private IRemoteRepository<T> remoteRepository;
    private CacheStrategy cacheStrategy;

    public void initialize(RemoteConfigSettings<T> settings) {
        internalRepository = settings.getInternalRepository();
        remoteRepository = settings.getRemoteRepository();
        cacheStrategy = settings.getCacheStrategy();
    }

    /**
     * Set a default value
     *
     * @param config config that will be stored as default value
     */
    public void setDefaultConfig(@NonNull T config) {
        checkInitialization();
        internalRepository.storeDefault(config);
    }

    public FetchResult fetch() {
        checkInitialization();
        if (System.currentTimeMillis() - internalRepository.getFetchedTimestamp() < cacheStrategy.maxAge())
            return new FetchResult(Completable.complete());

        final SingleRunner<T> runner = new SingleRunner<>();
        Completable c = Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter emitter) throws Exception {
                runner.execute(remoteRepository.fetch()).subscribeWith(new DisposableSingleObserver<T>() {
                    @Override
                    public void onSuccess(T t) {
                        internalRepository.storeFetched(t, System.currentTimeMillis());
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        emitter.onError(e);
                    }
                });
            }
        });
        FetchResult result = new FetchResult(c);

        return result;
    }

    /**
     * Activate last fetched config
     */
    public void activateFetched() {
        checkInitialization();
        internalRepository.activateConfig();
    }

    /**
     * Returns last activated config, if present. Otherwise will return the default config, or null
     *
     * @return last activated config, if present. Otherwise will return the default config, or null
     */
    @Nullable
    public T get() {
        checkInitialization();
        return internalRepository.getConfig();
    }

    /**
     * Clear default, fetched and activated config
     */
    public void clear() {
        checkInitialization();
        internalRepository.clear();
    }

    private void checkInitialization() {
        if (internalRepository == null || remoteRepository == null || cacheStrategy == null)
            throw new IllegalStateException("Remote resource not initialized");
    }

    public interface FetchSuccess {
        void onSuccess();
    }

    public interface FetchError {
        void onError(Throwable t);
    }

    public interface FetchResponse extends FetchSuccess, FetchError {
    }

    public class FetchResult {
        private final Completable fetchCompletable;

        private FetchResult(@NonNull Completable fetchCompletable) {
            this.fetchCompletable = fetchCompletable;
        }

        public void onSuccessListener(final FetchSuccess success) {
            onResponse(success, null);
        }

        public void onErrorListener(final FetchError error) {
            onResponse(null, error);
        }

        public void onResponseListener(final FetchResponse response) {
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
