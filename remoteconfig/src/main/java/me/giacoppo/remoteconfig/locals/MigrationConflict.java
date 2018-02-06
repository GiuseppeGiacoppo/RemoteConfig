package me.giacoppo.remoteconfig.locals;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Peppe on 06/02/2018.
 */

public interface MigrationConflict {
    int ACTIVATED_ONLY = 0;
    int MERGE = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ACTIVATED_ONLY, MERGE})
    @interface Strategy {
    }

    @Strategy
    int getStrategy();


    MigrationConflict IGNORE = new MigrationConflict() {
        @Override
        public int getStrategy() {
            return MigrationConflict.ACTIVATED_ONLY;
        }
    };

    MigrationConflict MERGE_WITH_DEFAULT = new MigrationConflict() {
        @Override
        public int getStrategy() {
            return MERGE;
        }
    };
}