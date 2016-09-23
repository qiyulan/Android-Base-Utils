package moe.xing.baseutils.utils;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Hehanbo on 2016/7/20 0020.
 * <p>
 * 日期帮助类
 */

public class DateUtils {

    /**
     * 获取过去一段时间的日期 例如 7 天前
     *
     * @param pastDate 过去多长时间 为 0 是今天 为正是将来
     */
    @NonNull
    public static String getPastDateString(int pastDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar past = Calendar.getInstance();
        past.add(Calendar.DATE, -pastDate);
        return format.format(past.getTime());
    }

    /**
     * 返回 1970-1-1
     */
    public static String getUnixStart() {
        return "1970-1-1";
    }
}
