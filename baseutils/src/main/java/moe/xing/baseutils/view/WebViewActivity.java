package moe.xing.baseutils.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URI;
import java.util.List;

import moe.xing.baseutils.R;
import moe.xing.baseutils.network.cookies.MyCookiesManager;
import moe.xing.baseutils.utils.LogHelper;
import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by Hehanbo on 2016/6/2 0002.
 * <p>
 * webview
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class WebViewActivity extends BaseActivity {
    public static final String URL_LOAD = "URL_LOAD";

    private WebView mWebView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        Intent intent = getIntent();
        String uri = intent.getStringExtra(URL_LOAD);
        if (TextUtils.isEmpty(uri)) {
            LogHelper.Toast("网址不存在");
            onBackPressedSupport();
            return;
        }

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        List<Cookie> cookies = new MyCookiesManager().loadForRequest(HttpUrl.get(URI.create(uri)));

        cookieManager.removeAllCookie();

        for (Cookie cookie : cookies) {

            cookieManager.setCookie(cookie.domain(), cookie.toString());
            Log.d("CookieUrl", cookie.toString());

        }

        mWebView.loadUrl(uri);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                getSupportActionBar().setTitle(view.getTitle());
            }
        });
    }
}
