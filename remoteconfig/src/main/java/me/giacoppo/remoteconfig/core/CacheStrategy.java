package me.giacoppo.remoteconfig.core;

import android.support.annotation.IntRange;

import java.util.concurrent.TimeUnit;

public interface CacheStrategy {
    @IntRange(from = 0)
    long maxAge();

    CacheStrategy NO_CACHE = new CacheStrategy() {
        @Override
        public long maxAge() {
            return 0;
        }
    };

    CacheStrategy DEFAULT_STRATEGY = new CacheStrategy() {
        @Override
        public long maxAge() {
            return TimeUnit.HOURS.toMillis(4);
        }
    };
}
