package me.giacoppo.remoteconfig;

import android.webkit.URLUtil;

import com.google.gson.Gson;


class Utilities {
    static void requireNonNull(Object o) {
        requireNonNull(o, "Non-null required");
    }

    static void requireNonNull(Object o, String message) {
        if (o == null)
            throw new IllegalArgumentException(message);
    }

    static class Network {
        static boolean isValidUrl(String url) {
            return URLUtil.isValidUrl(url);
        }
    }

    static class Json {
        static <T> T from(String json, Class<T> c) {
            return Holder.INSTANCE.fromJson(json, c);
        }

        static String to(Object o) {
            return Holder.INSTANCE.toJson(o);
        }

        private static final class Holder {
            static final Gson INSTANCE = new Gson();
        }
    }
}
