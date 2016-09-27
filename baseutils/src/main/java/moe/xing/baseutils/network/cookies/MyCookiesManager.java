package moe.xing.baseutils.network.cookies;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import moe.xing.baseutils.Init;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * 自动管理 Cookies
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class MyCookiesManager implements CookieJar {
    private static final PersistentCookieStore cookieStore = new PersistentCookieStore(Init.getApplication());

    /**
     * 清空所有的 Cookies
     */
    public static void clearAllCookies() {
        cookieStore.removeAll();
    }

    /**
     * 在回复中获取 cookies 并储存
     *
     * @param url     cookie 对应的 Url
     * @param cookies 需要被储存的 cookie
     */
    @Override
    public void saveFromResponse(@NonNull HttpUrl url, List<Cookie> cookies) {
        if (cookies != null && cookies.size() > 0) {
            for (Cookie item : cookies) {
                if (url.uri().getHost().contains("ci123")) {
                    url = HttpUrl.parse("http://ci123.com");
                }
                cookieStore.add(url, item);
            }
        }
    }

    /**
     * 获取对应的 cookies
     *
     * @param url 需要获取 cookies 的 Url
     * @return cookies 列表,如果 url 为空 则返回空列表
     */
    @NonNull
    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        if (url == null) {
            return new ArrayList<>();
        }
        if (url.uri().getHost().contains("ci123")) {
            url = HttpUrl.parse("http://ci123.com");
        }
        return cookieStore.get(url);
    }
}
