package moe.xing.baseutils;

import android.app.Application;

import java.lang.ref.SoftReference;

/**
 * Created by hehanbo on 16-9-21.
 * <p>
 * 储存 application 和 debug 开关
 */

public class Init {

    private volatile static Init sInstance;
    private static boolean isDebug;
    private static String versionName;
    private static String UA_NAME;
    private SoftReference<Application> mApplication;

    private Init(Application application, boolean isDebug, String versionName, String UA_NAME) {
        mApplication = new SoftReference<>(application);
        Init.isDebug = isDebug;
        Init.versionName = versionName;
        Init.UA_NAME = UA_NAME;
    }

    public static Init getInstance(Application application, boolean isDebug, String versionName, String UA_NAME) {
        if (sInstance == null) {
            synchronized (Init.class) {
                if (sInstance == null) {
                    if (UA_NAME.equals("INSTANT_RUN")) {
                        throw new RuntimeException("version name is INSTANT_RUN,pls change it.");
                    }
                    sInstance = new Init(application, isDebug, versionName, UA_NAME);
                }
            }
        }
        return sInstance;
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static String getVersionName() {
        return versionName;
    }

    public static String getUaName() {
        return UA_NAME;
    }

    public static Application getApplication() {
        if (sInstance == null || sInstance.mApplication == null) {
            throw new RuntimeException("please init this library first");
        }
        if (sInstance.mApplication.get() == null) {
            throw new RuntimeException("application is not exits");
        }
        return sInstance.mApplication.get();
    }

}
