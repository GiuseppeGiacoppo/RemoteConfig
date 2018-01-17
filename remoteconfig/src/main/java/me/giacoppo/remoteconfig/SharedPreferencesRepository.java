package me.giacoppo.remoteconfig;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.giacoppo.remoteconfig.core.ILocalRepository;

@SuppressWarnings("unused")
@SuppressLint("ApplySharedPref")
public final class SharedPreferencesRepository<T> implements ILocalRepository<T> {
    private static final String FILENAME_PREFIX = "remote_config_";
    private final SharedPreferences sharedPreferences;
    private final Class<T> classOfConfig;

    private SharedPreferencesRepository(Context context, Class<T> classOfConfig) {
        sharedPreferences = context.getSharedPreferences(FILENAME_PREFIX + classOfConfig.getSimpleName().toLowerCase(), Context.MODE_PRIVATE);
        this.classOfConfig = classOfConfig;
    }

    public static <T> SharedPreferencesRepository<T> create(@NonNull Context context, @NonNull Class<T> classOfConfig) {
        return new SharedPreferencesRepository<>(context, classOfConfig);
    }

    @Override
    public void storeDefault(T defaultValue) {
        set(defaultValue, -1, DEFAULT_CONFIG);
        if (getTimestamp(LAST_ACTIVATED_CONFIG) == -1)
            set(defaultValue, -1, LAST_ACTIVATED_CONFIG);
    }

    @Override
    public void storeFetched(T fetchedConfig, long timestamp) {
        set(fetchedConfig, timestamp, LAST_FETCHED_CONFIG);
    }

    @Override
    public long getFetchedTimestamp() {
        return getTimestamp(LAST_FETCHED_CONFIG);
    }

    @Override
    public T getConfig() {
        return get(LAST_ACTIVATED_CONFIG);
    }

    @Override
    public void activateConfig() {
        if (getTimestamp(LAST_FETCHED_CONFIG) > getTimestamp(LAST_ACTIVATED_CONFIG)) //avoid overriding default config
            set(get(LAST_FETCHED_CONFIG), getTimestamp(LAST_FETCHED_CONFIG), LAST_ACTIVATED_CONFIG);
    }

    private long getTimestamp(@ConfigType String type) {
        return sharedPreferences.getLong(type + TIMESTAMP_SUFFIX, -1);
    }

    private T get(@ConfigType String type) {
        String value = sharedPreferences.getString(type, null);
        if (value == null)
            return null;

        return Utilities.Json.from(value, classOfConfig);
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

    @Override
    public void clear() {
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
