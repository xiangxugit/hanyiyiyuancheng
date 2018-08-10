package com.xhh.ysj.utils;

import android.text.TextUtils;
import android.util.Log;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/4/28 0028.
 */

public class TimeUtils {
    private static final String TAG = "TimeUtils";

    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    public static Date strToDateLong(String strDate) {
        LogUtils.d(TAG, "strToDateLong: strDate = " + strDate);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        return formatter.parse(strDate, pos);
    }

    /**
     * 某个时间段的结束时间是否是在当前时间以后
     * @param beginTime 上架日期（HH:mm:ss）
     * @param endTime 下架日期（HH:mm:ss）
     * @param curTimeStr 当前时间（yyyy-MM-dd HH:mm:ss），由getCurrentTime获得
     * @return
     */
    public static boolean isFuturePeriod(String beginTime, String endTime, String curTimeStr) {
        LogUtils.d(TAG, "isFuturePeriod: beginTime = " + beginTime + ", endTime = " + endTime
                + ", curTimeStr = " + curTimeStr);
        if (TextUtils.isEmpty(beginTime) || TextUtils.isEmpty(endTime)  || TextUtils.isEmpty(curTimeStr)) {
            LogUtils.d(TAG, "isFuturePeriod: 时间段或当前时间为空");
            return false;
        }
        if (8 != beginTime.length() || 8 != endTime.length()) {
            LogUtils.d(TAG, "isFuturePeriod: 开始时间或结束时间格式错误");
            return false;
        }
        return isFutureTime(endTime, curTimeStr);
    }

    /**
     * 某个上下架时间段的结束时间是否是在当前日期当前或以后
     * @param upDate 上架日期（yyyy-MM-dd）
     * @param downDate 下架日期（yyyy-MM-dd）
     * @param curTimeStr 当前时间（yyyy-MM-dd HH:mm:ss），由getCurrentTime获得
     * @return
     */
    public static boolean isFutureSchedule(String upDate, String downDate, String curTimeStr) {
        LogUtils.d(TAG, "isFutureTime: upDate = " + upDate + ", downDate = " + downDate
                + ", curTimeStr = " + curTimeStr);
        if (TextUtils.isEmpty(upDate) || TextUtils.isEmpty(downDate) || TextUtils.isEmpty(curTimeStr)) {
            LogUtils.d(TAG, "isFutureTime: 时间段或当前时间为空");
            return false;
        }
        if (10 != upDate.length() || 10 != downDate.length()) {
            LogUtils.d(TAG, "isFutureTime: 上下日期或下架日期格式错误");
            return false;
        }
        return isAvailableDate(downDate, curTimeStr);
    }

    /**
     * 某个特定时间是否是在当前时间以后
     * @param spTime 某个特定时间（HH:mm:ss）
     * @param curTimeStr 当前时间（yyyy-MM-dd HH:mm:ss），由getCurrentTime获得
     * @return
     */
    private static boolean isFutureTime(String spTime, String curTimeStr) {
        if (TextUtils.isEmpty(spTime) || TextUtils.isEmpty(curTimeStr)) {
            LogUtils.d(TAG, "isFutureTime: 特定时间或当前时间为空");
            return false;
        }
        if (8 != spTime.length() || 19 != curTimeStr.length()) {
            LogUtils.d(TAG, "isFutureTime: 时间格式错误");
            return false;
        }

        boolean isFuture = false;
        Date currentTime;
        currentTime = strToDateLong(curTimeStr);

        // 将当前日期付给时间段
        spTime = curTimeStr.substring(0, 11) + spTime;

        Date endDate = strToDateLong(spTime);
        if (null != endDate) {
            isFuture = endDate.getTime() > currentTime.getTime();
        }
        return isFuture;
    }

    /**
     * 某个特定日期是否是在当前时间的日期当天或者以后
     * @param spDate 某个特定时间（yyyy-MM-dd）
     * @param curTimeStr 当前时间（yyyy-MM-dd HH:mm:ss），由getCurrentTime获得
     * @return
     */
    private static boolean isAvailableDate(String spDate, String curTimeStr) {
        if (TextUtils.isEmpty(spDate) || TextUtils.isEmpty(curTimeStr)) {
            LogUtils.d(TAG, "isFutureTime: 特定时间或当前时间为空");
            return false;
        }
        if (10 != spDate.length() || 19 != curTimeStr.length()) {
            LogUtils.d(TAG, "isFutureTime: 时间格式错误");
            return false;
        }

        boolean isFuture = false;
        Date currentTime;
        currentTime = strToDateLong(curTimeStr);

        // 将当前时刻付给时间段
        spDate = spDate + curTimeStr.substring(10, 19);

        Date endDate = strToDateLong(spDate);
        if (null != endDate) {
            isFuture = endDate.getTime() >= currentTime.getTime();
        }
        return isFuture;
    }

    /**
     * 当前时间是否在播放时间段内
     * @param beginTime 播放开始时间（HH:mm:ss）
     * @param endTime 播放结束时间（HH:mm:ss）
     * @param curTimeStr 当前时间（yyyy-MM-dd HH:mm:ss），由getCurrentTime获得
     * @return
     */
    private static boolean isCurrentTimeInPeriod(String beginTime, String endTime, String curTimeStr) {
        LogUtils.d(TAG, "isFutureTime: beginTime = " + beginTime + ", endTime = " + endTime
                + ", curTimeStr = " + curTimeStr);
        if (TextUtils.isEmpty(beginTime) || TextUtils.isEmpty(endTime) || TextUtils.isEmpty(curTimeStr)) {
            LogUtils.d(TAG, "isFutureTime: 时间段或当前时间为空");
            return false;
        }
        if (8 != beginTime.length() || 8 != endTime.length()) {
            LogUtils.d(TAG, "isFutureTime: 时间段格式错误");
            return false;
        }
        return isSameTime(beginTime, curTimeStr) ||
                (isFutureTime(endTime,curTimeStr) && !isFutureTime(beginTime, curTimeStr));
    }

    /**
     * 当前时间是否在播放日期段内
     * @param upDate 播放开始日期（yyyy:MM:dd）
     * @param downDate 播放结束日期（yyyy:MM:dd）
     * @param curTimeStr 当前时间（yyyy-MM-dd HH:mm:ss），由getCurrentTime获得
     * @return
     */
    private static boolean isCurrentDateInSchedule(String upDate, String downDate, String curTimeStr) {
        LogUtils.d(TAG, "isCurrentDateInSchedule: upDate = " + upDate + ", downDate = " + downDate
                + ", curTimeStr = " + curTimeStr);
        if (TextUtils.isEmpty(upDate) || TextUtils.isEmpty(downDate) || TextUtils.isEmpty(curTimeStr)) {
            LogUtils.d(TAG, "isFutureTime: 上架时间或下架时间或当前时间为空");
            return false;
        }
        if (10 != upDate.length() || 10 != downDate.length()) {
            LogUtils.d(TAG, "isFutureTime: 上架时间或下架时间格式错误");
            return false;
        }
        return isSameDay(upDate, curTimeStr) ||
                (isAvailableDate(downDate, curTimeStr) && !isAvailableDate(upDate, curTimeStr));
    }

    public static boolean isCurrentDateTimeInPlan(String upDate, String downDate,
                                                  String beginTime, String endTime,
                                                  String curTimeStr) {
        return isCurrentDateInSchedule(upDate, downDate, curTimeStr) &&
                isCurrentTimeInPeriod(beginTime, endTime, curTimeStr);
    }


    /**
     * 某个特定时间是否是在当前时间的当天或者以后
     * @param spDate 某个特定时间（yyyy-MM-dd HH:mm:ss）
     * @param curTimeStr 当前时间（yyyy-MM-dd HH:mm:ss），由getCurrentTime获得
     * @return
     */
    public static boolean isAvailDate(String spDate, String curTimeStr) {
        if (TextUtils.isEmpty(spDate) || TextUtils.isEmpty(curTimeStr)) {
            LogUtils.d(TAG, "isFutureTime: 特定时间或当前时间为空");
            return false;
        }
        if (19 != spDate.length() || 19 != curTimeStr.length()) {
            LogUtils.d(TAG, "isFutureTime: 时间格式错误");
            return false;
        }

        boolean isFuture = false;
        Date currentTime;
        currentTime = strToDateLong(curTimeStr);

        Date endDate = strToDateLong(spDate);
        if (null != endDate) {
            LogUtils.d(TAG, "isFutureTime: Date为空！");
            isFuture = endDate.getTime() >= currentTime.getTime();
        }
        return isFuture;
    }

    /**
     * 当前日期和特定日期是否是同一个日期
     * @param spDate 某个特定时间（yyyy-MM-dd）
     * @param curTimeStr 当前时间（yyyy-MM-dd HH:mm:ss），由getCurrentTime获得
     * @return
     */
    private static boolean isSameDay(String spDate, String curTimeStr ) {
        if (TextUtils.isEmpty(spDate) || TextUtils.isEmpty(curTimeStr)) {
            LogUtils.d(TAG, "isFutureTime: 特定时间或当前时间为空");
            return false;
        }
        if (10 != spDate.length() || 19 != curTimeStr.length()) {
            LogUtils.d(TAG, "isFutureTime: 时间格式错误");
            return false;
        }
        return spDate.equals(curTimeStr.substring(0, 10));
    }

    /**
     * 当前时间和特定时间是否是同一个时间
     * @param spTime 某个特定时间（HH:mm:ss）
     * @param curTimeStr 当前时间（yyyy-MM-dd HH:mm:ss），由getCurrentTime获得
     * @return
     */
    private static boolean isSameTime(String spTime, String curTimeStr) {
        if (TextUtils.isEmpty(spTime) || TextUtils.isEmpty(curTimeStr)) {
            LogUtils.d(TAG, "isFutureTime: 特定时间或当前时间为空");
            return false;
        }
        if (8 != spTime.length() || 19 != curTimeStr.length()) {
            LogUtils.d(TAG, "isFutureTime: 时间格式错误");
            return false;
        }
        return spTime.equals(curTimeStr.substring(11, 19));
    }

}
