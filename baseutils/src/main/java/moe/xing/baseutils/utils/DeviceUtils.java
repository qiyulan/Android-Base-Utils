package moe.xing.baseutils.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import moe.xing.baseutils.Init;

/**
 * Created by hehanbo on 16-8-23.
 * <p>
 * 设备相关
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class DeviceUtils {
    protected static final String UUID = "uuid";

    protected static final String FILE_NAME = "user_info";

    /**
     * 获取设备唯一安装 ID
     * @return 设备的唯一安装 ID
     */
    public static String getUUID() {
        Context context = Init.getApplication();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String uuid = sp.getString(UUID, "");
        if (TextUtils.isEmpty(uuid)) {
            uuid = java.util.UUID.randomUUID().toString().replaceAll("-", "");
            sp.edit().putString(UUID, uuid).apply();
        }
        return uuid;
    }

}
