package moe.xing.baseutils.utils;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import moe.xing.baseutils.BuildConfig;
import moe.xing.baseutils.Init;


/**
 * Created by Hehanbo on 2016/7/13 0013.
 * <p>
 * Log帮助类
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class LogHelper {

    private static final boolean LOG_ENABLE = BuildConfig.DEBUG;
    private static final boolean DETAIL_ENABLE = true;
    private static final String TAG = "sc_edu";

    private static String buildMsg(String msg) {
        StringBuilder buffer = new StringBuilder();

        if (DETAIL_ENABLE) {
            int lineNumber = 4;
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[lineNumber];
            while (stackTraceElement.getFileName().equals("LogHelper.java")
                    || stackTraceElement.getFileName().equals("BaseFragment.java")) {
                lineNumber++;
                stackTraceElement = Thread.currentThread().getStackTrace()[lineNumber];
            }

            buffer.append("[ ");
            buffer.append(Thread.currentThread().getName());
            buffer.append(": ");
            buffer.append(stackTraceElement.getFileName());
            buffer.append(": ");
            buffer.append(stackTraceElement.getLineNumber());
            buffer.append(": ");
            buffer.append(stackTraceElement.getMethodName());
        }

        buffer.append("() ] _____ ");
        if (msg != null) {
            buffer.append(msg);
        }

        return buffer.toString();
    }


    public static void v(String msg) {
        if (LOG_ENABLE && Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, buildMsg(msg));
        }
    }


    public static void d(String msg) {
        if (LOG_ENABLE && Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, buildMsg(msg));
        }
    }


    public static void i(String msg) {
        if (LOG_ENABLE && Log.isLoggable(TAG, Log.INFO)) {
            Log.i(TAG, buildMsg(msg));
        }
    }


    public static void w(String msg) {
        if (LOG_ENABLE && Log.isLoggable(TAG, Log.WARN)) {
            Log.w(TAG, buildMsg(msg));
        }
    }

    public static void w(Throwable e) {
        if (LOG_ENABLE && Log.isLoggable(TAG, Log.ERROR)) {
            Log.w(TAG, buildMsg(""), e);
        }
    }


    public static void w(String msg, Throwable e) {
        if (LOG_ENABLE && Log.isLoggable(TAG, Log.WARN)) {
            Log.w(TAG, buildMsg(msg), e);
        }
    }


    public static void e(String msg) {
        if (LOG_ENABLE && Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, buildMsg(msg));
        }
    }

    public static void e(Throwable e) {
        if (LOG_ENABLE && Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, buildMsg(""), e);
        }
    }


    public static void e(String msg, Throwable e) {
        if (LOG_ENABLE && Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, buildMsg(msg), e);
        }
    }


    /**
     * 获取不为null的字符串
     *
     * @return 原始值 如果不为 null
     * 或者 空串如果为 null
     */
    @NonNull
    private static String getNonNullString(String s) {
        return s == null ? "" : s;
    }

    public static void Snackbar(View view, Throwable t) {
        Snackbar(view, t.getMessage());
    }

    public static void Snackbar(View view, String message) {
        Snackbar(view, message, true);
    }

    /***
     * 显示 toast
     * todo 小米设备跳过显示
     */
    public static void Toast(String message) {
        try {
            Toast.makeText(Init.getApplication(), message, Toast.LENGTH_LONG).show();
        } catch (Exception ignore) {
        }
    }

    /**
     * 将错误信息告知用户
     * 先尝试使用 snackbar 不可用时使用 toast
     *
     * @param view     用于显示 snackbar toast 时所需的 view,不需要是root view
     * @param message  信息
     * @param printLog 是否打印 Log
     */

    public static void Snackbar(View view, String message, boolean printLog) {
        message = getNonNullString(message);
        final ForegroundColorSpan whiteSpan = new ForegroundColorSpan(ContextCompat.getColor(view.getContext(),
                android.R.color.white));
        SpannableStringBuilder snackbarText = new SpannableStringBuilder(message);
        snackbarText.setSpan(whiteSpan, 0, snackbarText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        if (view instanceof ScrollView || view instanceof NestedScrollView) {
            try {
                Toast(message);
            } catch (Exception ignore) {
            }
        } else {
            try {
                Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
            } catch (Exception e) {
                try {
                    Toast.makeText(view.getContext(), message, Toast.LENGTH_LONG).show();
                } catch (Exception ignore) {
                }
            }
        }
        if (printLog) {
            e(message);
        }
    }

}
