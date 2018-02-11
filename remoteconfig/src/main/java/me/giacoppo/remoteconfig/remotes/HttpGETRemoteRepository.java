package me.giacoppo.remoteconfig.remotes;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.Callable;

import me.giacoppo.remoteconfig.Utilities;
import me.giacoppo.remoteconfig.exceptions.HttpException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class HttpGETRemoteRepository<T> implements Callable<T> {
    private final Class<T> classOfConfig;
    private final String url;
    private final OkHttpClient client;

    private HttpGETRemoteRepository(Class<T> classOfConfig, String url) {
        this.classOfConfig = classOfConfig;
        this.url = url;
        this.client = new OkHttpClient();
    }

    public static <T> Callable<T> create(@NonNull Class<T> classOfConfig, @NonNull String url) {
        if (!Utilities.Network.isValidUrl(url))
            throw new IllegalArgumentException("Url not valid: " + url);

        return new HttpGETRemoteRepository<>(classOfConfig, url);
    }

    @Override
    public T call() throws IOException {
        Request r = new Request.Builder().url(url).build();
        Response response = client.newCall(r).execute();

        if (!response.isSuccessful())
            throw new HttpException(response.code(), response.message());
        else {
            return Utilities.Json.from(response.body().string(), classOfConfig);
        }
    }
}
