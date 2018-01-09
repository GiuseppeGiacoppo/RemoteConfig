package me.giacoppo.remoteconfig.callback;

import me.giacoppo.remoteconfig.RemoteConfig;

/**
 * Base callback
 */

public abstract class SimpleCallback implements RemoteConfig.Callback {
    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
    }
}
