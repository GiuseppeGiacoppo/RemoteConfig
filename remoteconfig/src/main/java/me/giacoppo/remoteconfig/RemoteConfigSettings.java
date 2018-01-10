package me.giacoppo.remoteconfig;

import android.support.annotation.NonNull;

/**
 * Settings for Remote Config instance
 *
 * @param <T> class of Config
 */
class RemoteConfigSettings<T> {
    private final Class<T> classOfConfig;

    private RemoteConfigSettings(Builder<T> builder) {
        this.classOfConfig = builder.classOfConfig;
    }

    Class<T> getClassOfConfig() {
        return classOfConfig;
    }

    public static class Builder<T> {
        private final Class<T> classOfConfig;

        private Builder(@NonNull Class<T> classOfConfig) {
            this.classOfConfig = classOfConfig;
        }

        @NonNull
        public RemoteConfigSettings<T> build() {
            check();
            return new RemoteConfigSettings<T>(this);
        }

        private void check() {
            Utilities.requireNonNull(classOfConfig, RemoteConfigMessages.NOT_VALID_CLASS);
        }
    }

    static <T> Builder<T> newBuilder(@NonNull Class<T> classOfConfig) {
        return new Builder<>(classOfConfig);
    }
}