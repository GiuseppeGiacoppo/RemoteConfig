package me.giacoppo.remoteconfig;

import android.webkit.URLUtil;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;


public final class Utilities {
    static void requireNonNull(Object o) {
        requireNonNull(o, "Non-null required");
    }

    static void requireNonNull(Object o, String message) {
        if (o == null)
            throw new IllegalArgumentException(message);
    }

    public static class Network {
        public static boolean isValidUrl(String url) {
            return URLUtil.isValidUrl(url);
        }
    }

    public static class Json {
        public static <T> T from(String json, Class<T> c) {
            return Holder.INSTANCE.fromJson(json, c);
        }

        public static String to(Object o) {
            return Holder.INSTANCE.toJson(o);
        }

        private static final class Holder {
            static final Gson INSTANCE = new Gson();
        }

        // hint from https://stackoverflow.com/questions/34092373/merge-extend-json-objects-using-gson-in-java
        public static String merge(String lowPriorityJson, String highPriorityJson) {
            JsonObject low = Holder.INSTANCE.fromJson(lowPriorityJson, JsonObject.class);
            JsonObject high = Holder.INSTANCE.fromJson(highPriorityJson, JsonObject.class);

            for (Map.Entry<String, JsonElement> lowPriorityEntry : low.entrySet()) {
                String lowPriorityKey = lowPriorityEntry.getKey();
                JsonElement lowPriorityValue = lowPriorityEntry.getValue();
                if (high.has(lowPriorityKey)) {
                    // manage conflict
                    JsonElement highPriorityValue = high.get(lowPriorityKey);
                    if (highPriorityValue.isJsonArray() && lowPriorityValue.isJsonArray()) {
                        JsonArray highPriorityArray = highPriorityValue.getAsJsonArray();
                        JsonArray lowPriorityArray = lowPriorityValue.getAsJsonArray();
                        //concat the arrays
                        for (int i = 0; i < lowPriorityArray.size(); i++) {
                            highPriorityArray.add(lowPriorityArray.get(i));
                        }
                    } else if (highPriorityValue.isJsonObject() && lowPriorityValue.isJsonObject()) {
                        //merge objects
                        merge(lowPriorityValue.getAsJsonObject().toString(), highPriorityValue.getAsJsonObject().toString());
                    } else {
                        //do nothing
                    }

                } else {//no conflict, add to the object
                    high.add(lowPriorityKey, lowPriorityValue);
                }
            }

            return high.getAsJsonObject().toString();
        }
    }
}
