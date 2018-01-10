package me.giacoppo.remoteconfig;

import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Logger {
    private int logLevel;

    private Logger() {
        logLevel = RELEASE;
    }

    static void setLogLevel(@Level int level) {
        Holder.logger.logLevel = level;
    }

    public static void log(@Level int logLevel, String message) {
        if (logLevel>=Holder.logger.logLevel)
            Log.d("RemoteConfig", message);
    }

    public static final int RELEASE = 0x0;
    public static final int DEBUG = 0x1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DEBUG,RELEASE})
    @interface Level {}

    private static final class Holder {
        static Logger logger = new Logger();
    }
}
