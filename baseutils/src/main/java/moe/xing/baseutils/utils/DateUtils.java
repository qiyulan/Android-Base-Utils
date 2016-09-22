package moe.xing.baseutils.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Hehanbo on 2016/7/20 0020.
 */

public class DateUtils {

    /**
     * 获取过去一段时间的日期 例如 7 天前
     *
     * @param pastDate 过去多长时间 为 0 是今天 为正是将来
     */
    public static String getPastDateString(int pastDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar past = Calendar.getInstance();
        past.add(Calendar.DATE, -pastDate);
        return format.format(past.getTime());
    }

    public static String getUnixStart() {
        return "1970-1-1";
    }
}
