package moe.xing.baseutils.network.cookies;

import java.util.ArrayList;
import java.util.List;

import moe.xing.baseutils.Init;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * 自动管理Cookies
 */
public class MyCookiesManager implements CookieJar {
    private static final PersistentCookieStore cookieStore = new PersistentCookieStore(Init.getApplication());

    /**
     * 清空所有的 Cookies
     */
    public static void clearAllCookies() {
        cookieStore.removeAll();
    }

    /**
     * 在回复中获取cookies并储存
     */
    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
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
     * 获取对应的cookies
     */
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
