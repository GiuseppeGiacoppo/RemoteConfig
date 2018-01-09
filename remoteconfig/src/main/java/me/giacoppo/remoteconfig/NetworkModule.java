package me.giacoppo.remoteconfig;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.IOException;
import java.util.Map;

import me.giacoppo.remoteconfig.network.HttpException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class NetworkModule {
    static void get(String httpUrl, Map<String, String> headers, final HttpCallback callback) {
        Request.Builder r = new Request.Builder()
                .url(httpUrl);

        if (headers != null)
            r.headers(Headers.of(headers));

        Holder.client.newCall(r.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body().string());
                } else {
                    if (!TextUtils.isEmpty(response.message()))
                        callback.onError(new HttpException(response.code(), response.message()));
                    else
                        callback.onError(new HttpException(response.code()));
                }
            }
        });
    }

    interface HttpCallback {
        void onSuccess(@Nullable String response);

        void onError(Throwable t);
    }

    private static final class Holder {
        static final OkHttpClient client = new OkHttpClient.Builder()
                .build();
    }
}
