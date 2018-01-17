package me.giacoppo.remoteconfig.app;

import android.app.Application;
import android.os.Message;

import me.giacoppo.remoteconfig.HttpGETRemoteRepository;
import me.giacoppo.remoteconfig.RemoteConfig;
import me.giacoppo.remoteconfig.RemoteConfigSettings;
import me.giacoppo.remoteconfig.SharedPreferencesRepository;
import me.giacoppo.remoteconfig.app.messages.MessagesConfig;
import me.giacoppo.remoteconfig.core.CacheStrategy;


public class RemoteConfigurationApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RemoteConfig.initialize(
                RemoteConfig.Initializer.newBuilder(this)
                        .setDeveloperMode(true)
                        .setLRUCacheSize(4)
                        .build()
        );

        RemoteConfig.of(MessagesConfig.class).initialize(
                new RemoteConfigSettings.Builder<MessagesConfig>()
                        .setInternalRepository(SharedPreferencesRepository.create(this, MessagesConfig.class))
                        .setRemoteRepository(HttpGETRemoteRepository.create(MessagesConfig.class, "http://demo0672984.mockable.io/messages.json"))
                        .setCacheStrategy(CacheStrategy.NO_CACHE)
                        .build()
        );
    }
}
