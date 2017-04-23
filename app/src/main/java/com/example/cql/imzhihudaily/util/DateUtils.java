package com.example.cql.imzhihudaily.util;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by CQL on 2016/10/10.
 * date 20161010
 */

public class DateUtils {

    public static String formatDate(String date) {

        int year = Integer.valueOf(date.substring(0, 4));
        int month = Integer.valueOf(date.substring(4, 6));
        int day = Integer.valueOf(date.substring(6));
        Calendar calendar = Calendar.getInstance();
        int systemYear = calendar.get(Calendar.YEAR);
        int systemMonth = calendar.get(Calendar.MONTH)+1;
        int systemDay = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month - 1, day);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String dayOfWeek = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        if("1".equals(dayOfWeek)){
            dayOfWeek ="日";
        }else if("2".equals(dayOfWeek)){
            dayOfWeek ="一";
        }else if("3".equals(dayOfWeek)){
            dayOfWeek ="二";
        }else if("4".equals(dayOfWeek)){
            dayOfWeek ="三";
        }else if("5".equals(dayOfWeek)){
            dayOfWeek ="四";
        }else if("6".equals(dayOfWeek)){
            dayOfWeek ="五";
        }else if("7".equals(dayOfWeek)){
            dayOfWeek ="六";
        }

        String mDate = month + "月" + day + "日" + " 星期" +dayOfWeek;
        if (year == systemYear && month == systemMonth && day == systemDay){
            return "今日热闻";
        }else {
            return mDate;
        }
    }
}
