package me.giacoppo.remoteconfig;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;

import java.lang.ref.WeakReference;
import java.util.Map;

@SuppressWarnings({"WeakerAccess", "unused", "UnusedReturnValue"})
public final class RemoteConfig<T> {
    private final RemoteConfigSettings<T> remoteConfigSettings;
    private final RemoteConfigRepository<T> repo;

    /**
     * Init library with default values
     *
     * @param context App context
     */
    public static void initializeWithDefaults(@NonNull Context context) {
        initialize(Initializer.newBuilder(context).build());
    }

    /**
     * Init library with custom values
     *
     * @param initializer initializer for library
     */
    public static void initialize(@NonNull Initializer initializer) {
        Utilities.requireNonNull(initializer, RemoteConfigMessages.NOT_VALID_INITIALIZER);

        if (initializer.context.getApplicationContext() != null)
            Holder.context = new WeakReference<>(initializer.context.getApplicationContext());
        else
            Holder.context = new WeakReference<>(initializer.context);

        if (initializer.lruCacheSize > 0)
            Holder.lruCache = new LruCache<>(initializer.lruCacheSize);
        //else don't use cache

        if (initializer.developerMode)
            Logger.setLogLevel(Logger.DEBUG);
    }

    /**
     * Get a new instance of Remote Config
     *
     * @param classOfConfig class of config
     * @param <T>           Generic representing the config object class
     * @return an instance of a RemoteConfig of the specific config class
     */
    @NonNull
    public static <T> RemoteConfig<T> of(@NonNull Class<T> classOfConfig) {
        Utilities.requireNonNull(Holder.context, RemoteConfigMessages.NOT_INITIALIZED);
        Utilities.requireNonNull(classOfConfig, RemoteConfigMessages.NOT_VALID_CLASS);

        RemoteConfig<T> remoteConfig;
        final String key = classOfConfig.getSimpleName().toLowerCase();

        if (Holder.lruCache != null) {
            //noinspection unchecked
            remoteConfig = (RemoteConfig<T>) Holder.lruCache.get(key);
            if (remoteConfig != null) {
                Logger.log(Logger.DEBUG, key + " already in cache");
                return remoteConfig;
            }
        }

        RemoteConfigSettings<T> settings = RemoteConfigSettings.newBuilder(classOfConfig).build();
        remoteConfig = new RemoteConfig<>(settings);

        if (Holder.lruCache != null) {
            Logger.log(Logger.DEBUG, key + " not cached. Adding now");
            Holder.lruCache.put(key, remoteConfig);
        }

        return remoteConfig;
    }

    /* Instance methods */

    private RemoteConfig(RemoteConfigSettings<T> remoteConfigSettings) {
        this.remoteConfigSettings = remoteConfigSettings;
        repo = RemoteConfigRepository.create(Holder.context.get(), remoteConfigSettings.getClassOfConfig());
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
    public void fetch(@NonNull final Request request, @Nullable final Callback<T> callback) {
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

    public void clear() {
        repo.clearConfig();
    }

    /* Inner classes*/
    public static class Initializer {
        private final boolean developerMode;
        private final int lruCacheSize;
        private final Context context;

        private Initializer(Builder builder) {
            this.context = builder.context;
            this.lruCacheSize = builder.lruCacheSize;
            this.developerMode = builder.developerMode;
        }

        @NonNull
        public static Builder newBuilder(@NonNull Context c) {
            return new Builder(c);
        }

        public static class Builder {
            private boolean developerMode;
            private int lruCacheSize = Holder.baseLRUCacheSize;
            private final Context context;

            private Builder(@NonNull Context context) {
                this.context = context;
            }

            /**
             * Changes the log level
             *
             * @param developerMode boolean
             * @return builder
             */
            public Builder setDeveloperMode(boolean developerMode) {
                this.developerMode = developerMode;
                return this;
            }

            public Builder setLRUCacheSize(@IntRange(from = 0) int lruCacheSize) {
                this.lruCacheSize = lruCacheSize;
                return this;
            }

            @NonNull
            public Initializer build() {
                check();
                return new Initializer(this);
            }

            private void check() {
                Utilities.requireNonNull(context, "Non-null Context required.");

                if (lruCacheSize < 0)
                    throw new IllegalArgumentException("Non-negative LRUCache size required. Current value: " + lruCacheSize);
            }
        }
    }

    public static class Request {
        private final Builder builder;
        private final String url;
        private final long cacheExpiration;
        private final Map<String, String> headers;

        private Request(Builder builder) {
            this.builder = builder;
            this.url = builder.url;
            this.cacheExpiration = builder.cacheExpiration;
            this.headers = builder.headers;
        }

        @NonNull
        public static Builder newBuilder(@NonNull String url) {
            return new Builder(url);
        }

        @NonNull
        public Builder newBuilder() {
            return builder;
        }

        public static class Builder {
            private String url;
            private long cacheExpiration = Holder.baseRequestCacheIntervalInMillis; //default value
            private Map<String, String> headers;

            private Builder(String url) {
                this.url = url;
            }

            public Builder setCacheExpiration(@IntRange(from = 0) long cacheExpiration) {
                this.cacheExpiration = cacheExpiration;
                return this;
            }

            public Builder setHeaders(Map<String, String> headers) {
                this.headers = headers;
                return this;
            }

            @NonNull
            public Request build() {
                check();
                return new Request(this);
            }

            private void check() {
                if (!Utilities.Network.isValidUrl(url))
                    throw new IllegalArgumentException("Url is not valid");

                if (cacheExpiration < 0)
                    throw new IllegalArgumentException("Non-negative cache expiration required. Current value: " + cacheExpiration);
            }
        }
    }

    private final static class Holder {
        static WeakReference<Context> context;
        static final long baseRequestCacheIntervalInMillis = 14400000; //4h
        static final int baseLRUCacheSize = 3;
        static LruCache<String, RemoteConfig> lruCache;
    }

    public interface Callback<T> {
        void onSuccess();

        void onError(Throwable t);
    }
}
