package me.giacoppo.remoteconfig.app;

import android.app.Application;

import me.giacoppo.remoteconfig.locals.MigrationConflict;
import me.giacoppo.remoteconfig.remotes.HttpGETRemoteRepository;
import me.giacoppo.remoteconfig.RemoteConfig;
import me.giacoppo.remoteconfig.RemoteConfigSettings;
import me.giacoppo.remoteconfig.locals.SharedPreferencesLocalRepository;
import me.giacoppo.remoteconfig.app.messages.MessagesConfig;
import me.giacoppo.remoteconfig.core.CacheStrategy;


public class RemoteConfigurationApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RemoteConfig.initialize(
                RemoteConfig.Initializer.newBuilder(this)
                        .setDeveloperMode(true)
                        .build()
        );

        RemoteConfig.of(MessagesConfig.class).initialize(
                new RemoteConfigSettings.Builder<MessagesConfig>()
                        .setInternalRepository(SharedPreferencesLocalRepository.create(this, MessagesConfig.class, MigrationConflict.MERGE_WITH_DEFAULT))
                        .setRemoteRepository(HttpGETRemoteRepository.create(MessagesConfig.class, "http://demo0672984.mockable.io/messages.json"))
                        .setCacheStrategy(CacheStrategy.NO_CACHE)
                        .build()
        );
    }
}
