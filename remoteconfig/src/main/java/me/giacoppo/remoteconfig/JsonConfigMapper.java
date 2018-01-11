package me.giacoppo.remoteconfig;

import me.giacoppo.remoteconfig.mapper.ConfigMapper;

public class JsonConfigMapper<T> implements ConfigMapper<T> {
    private final Class<T> classOfConfig;

    public JsonConfigMapper(Class<T> classOfConfig) {
        this.classOfConfig = classOfConfig;
    }

    @Override
    public String toString(T config) {
        return Utilities.Json.to(config);
    }

    @Override
    public T fromString(String s) {
        return Utilities.Json.from(s,classOfConfig);
    }
}
