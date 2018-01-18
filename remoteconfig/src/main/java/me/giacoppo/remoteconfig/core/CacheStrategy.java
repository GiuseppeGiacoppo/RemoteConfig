package me.giacoppo.remoteconfig.core;

import android.support.annotation.IntRange;

import java.util.concurrent.TimeUnit;

/**
 * Determines when the library can avoid to fetch config from remote repository
 */
public interface CacheStrategy {
    /**
     * @return the max age for fetched config
     */
    @IntRange(from = 0)
    long maxAgeInMillis();

    CacheStrategy NO_CACHE = new CacheStrategy() {
        @Override
        public long maxAgeInMillis() {
            return 0;
        }
    };

    CacheStrategy DEFAULT_STRATEGY = new CacheStrategy() {
        @Override
        public long maxAgeInMillis() {
            return TimeUnit.HOURS.toMillis(4);
        }
    };
}
