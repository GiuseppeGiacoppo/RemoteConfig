package me.giacoppo.remoteconfig;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings("unused")
@SuppressLint("ApplySharedPref")
class RemoteConfigRepository<T> {
    private static final String FILENAME_PREFIX = "remote_config_";
    private final SharedPreferences sharedPreferences;
    private final Class<T> classOfConfig;
    private T activeConfig;

    private RemoteConfigRepository(Context context, Class<T> classOfConfig) {
        this.classOfConfig = classOfConfig;
        sharedPreferences = context.getSharedPreferences(FILENAME_PREFIX + classOfConfig.getSimpleName().toLowerCase(), Context.MODE_PRIVATE);
    }

    static <T> RemoteConfigRepository<T> create(Context context, Class<T> classType) {
        Utilities.requireNonNull(context);
        Utilities.requireNonNull(classType);

        return new RemoteConfigRepository<>(context, classType);
    }

    void setDefaultConfig(T defaultValue) {
        set(defaultValue, -1, DEFAULT_CONFIG);

        //if no activated config exists, set default config as activated
        if (getTimestamp(LAST_ACTIVATED_CONFIG) == -1)
            activateConfig(defaultValue, -1);
    }

    void setLastFetchedConfig(T fetchedConfig) {
        set(fetchedConfig, System.currentTimeMillis(), LAST_FETCHED_CONFIG);
    }

    void activateFetchedConfig() {
        if (getTimestamp(LAST_FETCHED_CONFIG) != -1)
            activateConfig(get(LAST_FETCHED_CONFIG), getTimestamp(LAST_FETCHED_CONFIG));
        else
            Logger.log(Logger.DEBUG, "No fetched config to activate");
    }

    private void activateConfig(T config, long instant) {
        set(config, instant, LAST_ACTIVATED_CONFIG);
        invalidateInMemoryConfig();
    }

    private void invalidateInMemoryConfig() {
        synchronized (this) {
            activeConfig = null;
        }
    }

    T getActivated() {
        if (activeConfig == null) {
            synchronized (this) {
                activeConfig = get(LAST_ACTIVATED_CONFIG);
            }
        }

        return activeConfig;
    }

    T getLastFetched() {
        return get(LAST_FETCHED_CONFIG);
    }

    private T get(@ConfigType String type) {
        String value = sharedPreferences.getString(type, null);
        if (value == null)
            return null;

        return Utilities.Json.from(value, classOfConfig);
    }

    private long getTimestamp(@ConfigType String type) {
        return sharedPreferences.getLong(type + TIMESTAMP_SUFFIX, -1);
    }

    long getLastFetchedTimestamp() {
        return getTimestamp(LAST_FETCHED_CONFIG);
    }

    private void set(@Nullable T value, long timestamp, @ConfigType String type) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value == null) {
            editor.remove(type);
            editor.remove(type + TIMESTAMP_SUFFIX);
        } else {
            editor.putString(type, Utilities.Json.to(value));
            editor.putLong(type + TIMESTAMP_SUFFIX, timestamp);
        }

        editor.commit();
    }

    void clearConfig() {
        invalidateInMemoryConfig();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();
    }

    private static final String LAST_FETCHED_CONFIG = "last_fetched_config";
    private static final String LAST_ACTIVATED_CONFIG = "last_activated_config";
    private static final String TIMESTAMP_SUFFIX = "_timestamp";
    private static final String DEFAULT_CONFIG = "default_config";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({LAST_FETCHED_CONFIG, LAST_ACTIVATED_CONFIG, DEFAULT_CONFIG})
    @interface ConfigType {
    }
}
