package me.giacoppo.remoteconfig.core;

import java.util.concurrent.TimeUnit;

public interface CacheStrategy {
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
