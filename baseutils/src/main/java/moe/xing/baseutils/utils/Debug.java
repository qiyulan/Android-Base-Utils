package moe.xing.baseutils.utils;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Qi Xingchen on 16-10-24.
 */

public class Debug {
    /**
     * 设置 Stetho ( application )
     */
    public static void addStethoInApp(Application application) {
        Stetho.initializeWithDefaults(application);
    }
}
