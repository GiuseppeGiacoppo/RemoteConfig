package me.giacoppo.remoteconfig;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

final class SingleExecutor<T> {
    Single<T> execute(Callable<T> callable) {
        return Single.fromCallable(callable).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
