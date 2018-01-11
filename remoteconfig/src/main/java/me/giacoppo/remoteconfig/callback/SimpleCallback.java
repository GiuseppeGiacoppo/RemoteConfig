package me.giacoppo.remoteconfig.callback;

import me.giacoppo.remoteconfig.RemoteResource;

/**
 * Base callback
 */

public abstract class SimpleCallback implements RemoteResource.Callback {
    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
    }
}
