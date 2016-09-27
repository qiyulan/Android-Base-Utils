package moe.xing.baseutils.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Hehanbo on 2016/7/20 0020.
 * <p>
 * 日期帮助类
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class DateUtils {

    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public static final String yyyy_MM_dd = "yyyy-MM-dd";
    public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public static final String HH_mm_ss = "HH:mm:ss";
    public static final String HH_mm = "HH:mm";

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

    /**
     * 在 calendar 上添加月份
     *
     * @param calendar 被添加的 calendar
     * @param month    需要添加的月份
     */
    public static void addMonth(@NonNull Calendar calendar, int month) {
        calendar.add(Calendar.MONTH, month);
    }

    /**
     * 获取现在
     *
     * @return 现在的 date
     */
    @NonNull
    public static Date getNow() {
        return new Date();
    }

    /**
     * 获取当天,并清除时间
     *
     * @return 现在没有时间的 data
     */
    public static Date getTodayAndClearTime() {
        Calendar calendar = getCalendarAndClearTime(null);
        return calendar.getTime();
    }

    /**
     * 根据时间戳来获取日期
     *
     * @param times unix 时间戳
     * @return 时间戳对应的 date
     */
    @NonNull
    public static Date getDate(long times) {
        return new Date(times);
    }

    /**
     * 清空日期里的时间
     *
     * @param date 需要被清空时间的 date
     * @return 被清空时间后的 date
     */
    @NonNull
    public static Date clearTime(@NonNull Date date) {
        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 清空日期里的时间
     *
     * @param calendar 需要被清空时间的 calendar
     */
    public static void clearTime(@NonNull Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * 清空日期里的时间, date 为空则清除当前
     *
     * @param date 需要被清空时间的 date
     * @return 被清空时间后的 Calendar
     */
    @NonNull
    public static Calendar getCalendarAndClearTime(@Nullable Date date) {
        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * 根据date获取Calendar对象,如果 date 为空,则获取现在
     *
     * @param date 需要获得的 date
     * @return 对应的 calendar
     */
    @NonNull
    public static Calendar getCalendar(@Nullable Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        return calendar;
    }


    /**
     * 获取两个日期之间的间隔日期
     *
     * @param startDate 起点时间
     * @param endDate   终点时间
     * @return 间距
     */
    public static int betweenDateByDay(@NonNull Date startDate, @NonNull Date endDate) {
        long t1 = clearTime(startDate).getTime();
        long t2 = clearTime(endDate).getTime();
        return (int) TimeUnit.MILLISECONDS.toDays((t2 - t1));
    }

    /**
     * 格式化日期到字符串 如 date 为空 则格式化当前
     *
     * @param date    被格式化
     * @param pattern 格式化的选项
     * @return 格式化后的字符串
     */
    @NonNull
    public static String format(@Nullable Date date, @NonNull String pattern) {
        if (date == null) {
            date = new Date();
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * 格式化日期 为 yyyyMMddHHmmss ,为空格式化当前
     *
     * @param date 被格式化的时间
     * @return 格式化后的字符串
     */
    @NonNull
    public static String formatTo4yMMddHHmmss(@Nullable Date date) {
        return format(date, yyyyMMddHHmmss);
    }

    /**
     * 格式化日期格式  yyyy-MM-dd ,为空格式化当前
     *
     * @param date 被格式化的时间
     * @return 格式化后的字符串
     */
    @NonNull
    public static String formatTo4y_MM_dd(@Nullable Date date) {
        return format(date, yyyy_MM_dd);
    }

    /**
     * 格式化日期时间格式  yyyy-MM-dd HH:mm:ss为空格式化当前
     *
     * @param date 被格式化的时间
     * @return 格式化后的字符串
     */
    @NonNull
    public static String formatTo4y_MM_dd_HH_mm_ss(@Nullable Date date) {
        return format(date, yyyy_MM_dd_HH_mm_ss);
    }

    /**
     * 格式化时间格式 HH:mm:ss为空格式化当前
     *
     * @param date 被格式化的时间
     * @return 格式化后的字符串
     */
    @NonNull
    public static String formatToHH_mm_ss(@Nullable Date date) {
        return format(date, HH_mm_ss);
    }

    /**
     * 格式化时间格式 HH:mm为空格式化当前
     *
     * @param date 被格式化的时间
     * @return 格式化后的字符串
     */
    @NonNull
    public static String formatToHH_mm(@Nullable Date date) {
        return format(date, HH_mm);
    }

    /**
     * 将字符串转换为日期
     *
     * @param formatDate 被转换的字符串
     * @param pattern    转换格式
     * @return 转换后的日期
     * @throws ParseException 无法转换
     */
    @NonNull
    public static Date parse(@NonNull String formatDate, @NonNull String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());

        return sdf.parse(formatDate);

    }

    //应用级API

    /**
     * 根据年和月获取Calendar实例 日期为 1
     *
     * @param year  需要获取的年
     * @param month 需要获取的月
     * @return 得到的 Calendar
     */
    @NonNull
    public static Calendar getCalendar(int year, int month) {
        return getCalendar(year, month, 1);
    }

    /**
     * 根据年月日获取Calendar实例
     *
     * @param year  需要获取的年
     * @param month 需要获取的月
     * @param day   需要获得的日
     * @return 得到的 Calendar
     */
    @NonNull
    public static Calendar getCalendar(int year, int month, int day) {
        Calendar calendar = getCalendarAndClearTime(null);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, day);
        return calendar;
    }

    /**
     * 获取某个月份下的第一天和最后一天
     *
     * @param year  需要获取的年份
     * @param month 需要获取的月份
     * @return 起始与截至日期
     */
    @NonNull
    @Size(value = 2)
    public static Date[] getFLDatesInMonth(int year, int month) {
        Calendar calendar = getCalendar(year, month);
        Date firstDate = calendar.getTime();
        firstDate = getFirstDayOfMonth(firstDate);
        Date lastDate = calendar.getTime();
        lastDate = getLastDayOfMonth(lastDate);
        return new Date[]{firstDate, lastDate};
    }

    /**
     * 判断某一日期是否在某几个月内,不指定年份
     *
     * @param date       被判断的时间
     * @param startMonth 起始月份
     * @param endMonth   截至月份
     * @return <code>true</code> 在范围内
     * <code>false</code> 不在范围内
     */
    public static boolean isDateInMonth(@NonNull Date date, int startMonth, int endMonth) {
        Calendar calendar = getCalendar(date);
        int month = calendar.get(Calendar.MONTH) + 1;
        return month >= startMonth && month <= endMonth;
    }


    /**
     * 判断某一日期是否在某个月内,指定年份
     *
     * @param date       被判断的时间
     * @param startMonth 起始月份
     * @param endMonth   截至月份
     * @param year       年份
     * @return <code>true</code> 在范围内
     * <code>false</code> 不在范围内
     */
    public static boolean isDateInMonth(@NonNull Date date, int startMonth, int endMonth, int year) {
        Calendar calendar = getCalendar(date);
        return isDateInMonth(date, startMonth, endMonth) && year == calendar.get(Calendar.YEAR);
    }

    /**
     * 获取星期几 date 为空则获取当前
     *
     * @param date 获取周几的 date
     * @return 返回  "日", "一", "二", "三", "四", "五", "六"
     */
    @NonNull
    public static String getWeek(@Nullable Date date) {
        if (date == null) {
            date = new Date();
        }
        String[] weeks = {"", "日", "一", "二", "三", "四", "五", "六"};
        Calendar calendar = getCalendar(date);
        int d = calendar.get(Calendar.DAY_OF_WEEK);
        return weeks[d];
    }

    /**
     * 获取该月里有多少天 date 为空则获取当前
     *
     * @param date 获取月份所在的 date
     * @return 该月的天数
     */
    public static int getDaysOfMonth(@Nullable Date date) {
        if (date == null) {
            date = new Date();
        }
        Calendar calendar = getCalendar(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取该月里有多少天
     */
    public static int getDaysOfMonth(int year, int month) {
        Calendar calendar = getCalendar(year, month);
        return getDaysOfMonth(calendar.getTime());
    }

    /**
     * 获取当前月的第一天 date 为空则获取当前
     *
     * @param date 获取月份所在的 date
     * @return 第一天的 date
     */
    @NonNull
    public static Date getFirstDayOfMonth(@Nullable Date date) {
        if (date == null) {
            date = new Date();
        }
        String mm = format(date, "yyyy-MM-01");
        try {
            return parse(mm, "yyyy-MM-dd");
        } catch (ParseException ignore) {
        }
        //noinspection ConstantConditions
        return null;
    }

    /**
     * 获取当前月的最后一天
     *
     * @param date 获取月份所在的 date
     * @return 最后一天的 date
     */
    @NonNull
    public static Date getLastDayOfMonth(@Nullable Date date) {
        if (date == null) {
            date = new Date();
        }
        int maxDay = getDaysOfMonth(date);
        String mm = format(date, "yyyy-MM-" + maxDay);
        try {
            return parse(mm, "yyyy-MM-dd");
        } catch (ParseException ignore) {
        }
        //noinspection ConstantConditions
        return null;
    }
}
