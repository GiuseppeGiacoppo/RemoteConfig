package me.giacoppo.remoteconfig;

import java.io.IOException;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import me.giacoppo.remoteconfig.core.IRemoteRepository;
import me.giacoppo.remoteconfig.network.HttpException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

final class HttpGetResourceModule<T> implements IRemoteRepository<T> {
    private final Class<T> classOfConfig;
    private final String url;
    private final OkHttpClient client;

    public HttpGetResourceModule(Class<T> classOfConfig, String url, OkHttpClient client) {
        this.classOfConfig = classOfConfig;
        this.url = url;
        this.client = client;
    }

    @Override
    public Single<T> fetch() {
        return Single.create(new SingleOnSubscribe<T>() {
            @Override
            public void subscribe(final SingleEmitter<T> emitter) throws Exception {
                client.newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        emitter.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful())
                            emitter.onError(new HttpException(response.code(),response.message()));
                        else {
                            T fetched = Utilities.Json.from(response.body().string(),classOfConfig);
                            emitter.onSuccess(fetched);
                        }
                    }
                });
            }
        });
    }
}
