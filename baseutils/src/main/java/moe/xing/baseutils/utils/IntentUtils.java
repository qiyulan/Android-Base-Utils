package moe.xing.baseutils.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.app.Fragment;

import java.util.List;

import moe.xing.baseutils.Init;

/**
 * Created by Hehanbo on 2016/7/21 0021.
 */

public class IntentUtils {

    /**
     * 安全的调用外部 intent
     *
     * @param intent 需要使用其他应用的 intent
     * @return {@value = true} 安全的打开了
     * {@value = false} 没有应用可以打开此 intent
     */
    public static boolean startIntent(Intent intent) {
        return rawStartIntent(intent, Init.getApplication());
    }

    public static boolean startIntent(Intent intent, Activity activity) {
        return rawStartIntent(intent, activity);
    }

    private static boolean rawStartIntent(Intent intent, Context context) {
        if (isSafe(intent)) {
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * 判断外部 intent 调用是否安全
     *
     * @param intent 需要使用其他应用的 intent
     * @return {@value = true} 可以安全打开
     * {@value = false} 没有应用可以打开此 intent
     */
    public static boolean isSafe(Intent intent) {
        PackageManager packageManager = Init.getApplication().getPackageManager();
        return intent.resolveActivity(packageManager) != null;
    }


    /**
     * 获取接受 intent 的应用的图标
     */
    public static List<ResolveInfo> getIntentAppIcon(Intent intent, Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.queryIntentActivities(intent, 0);
    }


    /**
     * 安全的调用外部 intent
     *
     * @param intent      需要使用其他应用的 intent
     * @param requestCode 请求码
     * @return {@value = true} 安全的打开了
     * {@value = false} 没有应用可以打开此 intent
     */
    public static boolean startIntentForResult(Intent intent, Activity activity, int requestCode) {
        if (!isSafe(intent)) {
            return false;
        }
        activity.startActivityForResult(intent, requestCode);
        return true;
    }

    /**
     * 安全的调用外部 intent
     *
     * @param intent      需要使用其他应用的 intent
     * @param requestCode 请求码
     * @return {@value = true} 安全的打开了
     * {@value = false} 没有应用可以打开此 intent
     */
    public static boolean startIntentForResult(Intent intent, Fragment fragment, int requestCode) {
        if (!isSafe(intent)) {
            return false;
        }
        fragment.startActivityForResult(intent, requestCode);
        return true;
    }
}
