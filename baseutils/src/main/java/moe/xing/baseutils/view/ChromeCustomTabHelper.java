package moe.xing.baseutils.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;


/**
 * Created by Hehanbo on 2016/6/2 0002.
 * <p>
 * CCT帮助类
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ChromeCustomTabHelper {
    private static CustomTabsServiceConnection connection = new CustomTabsServiceConnection() {
        @Override
        public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private Context context;
    private boolean share = false;

    @ColorRes
    private int toolbar;

    /**
     * 打开Url 优先使用 CCT 不可用则使用 {@link WebViewActivity}
     */
    public static void openUrlAnyway(Context context, String url) {
        if (isOK(context)) {
            with(context).show(url);
        } else {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(WebViewActivity.URL_LOAD, url);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        }
    }

    public static ChromeCustomTabHelper with(Context context) {
        ChromeCustomTabHelper instance = new ChromeCustomTabHelper();
        instance.init(context);
        return instance;
    }

    /**
     * 判断CCT是否可用
     */
    public static boolean isOK(Context context) {
        return CustomTabsClient.bindCustomTabsService(context, context.getApplicationContext().getPackageName(), connection);
    }

    private void init(Context context) {
        this.context = context;
        this.toolbar = getPrimaryColor(context);
    }

    /**
     * toolbar的颜色
     */
    public ChromeCustomTabHelper toolbar(int toolbar) {
        this.toolbar = toolbar;
        return this;
    }

    /**
     * 是否允许分享
     */
    @SuppressWarnings("unused")
    public ChromeCustomTabHelper share(boolean share) {
        this.share = share;
        return this;
    }

    public void show(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        builder.setToolbarColor(ContextCompat.getColor(context, toolbar));
        if (share) builder.addDefaultShareMenuItem();
        builder.build().launchUrl((Activity) context, Uri.parse(url));
    }

    /**
     * 获取主色调
     */
    private int getPrimaryColor(Context context) {
        TypedValue typedValue = new TypedValue();
        TypedArray typedArray = context.obtainStyledAttributes(typedValue.data, new int[]{16843827});
        int accent = typedArray.getColor(0, 0);
        typedArray.recycle();
        return accent;
    }

}
