package me.giacoppo.remoteconfig;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

final class SingleRunner<T> {
    Single<T> execute(Single<T> observable) {
        return observable.subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
