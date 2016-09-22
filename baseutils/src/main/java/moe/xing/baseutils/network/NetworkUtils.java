package moe.xing.baseutils.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import moe.xing.baseutils.Init;

/**
 * Created by Hehanbo on 2016/7/26 0026.
 * <p>
 * 网络帮助类
 */

public class NetworkUtils {

    /**
     * 判断网络是否可用
     */
    public static Boolean isNetworkReachable() {
        ConnectivityManager cm = (ConnectivityManager) Init.getApplication()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo current = cm.getActiveNetworkInfo();
        return current != null && (current.isAvailable());
    }
}
