package com.ipcamerasen5.record.common;

import android.text.TextUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Date;

import nes.ltlib.utils.LogUtils;

/**
 * Created by chenqianghua on 2017/4/7.
 */

public class DateUtil {

    private static final String DATE_TIME_PATTERN1 = "yyyyMMddHHmmss";

    private static final String DATE_TIME_PATTERN2 = "yyyy-MM-dd HH:mm";


    private static DateTimeFormatter dateTimeFormatter1 = DateTimeFormat.forPattern(DATE_TIME_PATTERN1);

    private static DateTimeFormatter dateTimeFormatter2 = DateTimeFormat.forPattern(DATE_TIME_PATTERN2);

    /**
     * 获取当前时间的DateTime对象
     *
     * @return
     */
    public static DateTime getCurrentDateTime() {

        DateTime dateTime = new DateTime(System.currentTimeMillis());

        return dateTime;
    }

    /**
     * 获取Date : yyyy-MM-dd HH:mm:ss
     *
     * @param datetime
     * @return
     */
    public static Date parseDateTime1(String datetime) {

        if (TextUtils.isEmpty(datetime)) {

            return null;
        }

        DateTime dateTime = null;

        try {
            dateTime = DateTime.parse(datetime, dateTimeFormatter1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (dateTime != null) {
            return dateTime.toDate();
        }

        return null;

    }

    /**
     * 获取Date : yyyy-MM-dd HH:mm
     *
     * @param datetime
     * @return
     */
    public static Date parseDateTime2(String datetime) {

        if (TextUtils.isEmpty(datetime)) {

            return null;
        }

        DateTime dateTime = null;

        try {
            dateTime = DateTime.parse(datetime, dateTimeFormatter2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (dateTime != null) {
            return dateTime.toDate();
        }

        return null;
    }

    /**
     * 格式化输出日期: 2016-1-16 13:04:11
     *
     * @param date
     * @return
     */
    public static String formatDateTime1(Date date) {

        if (date == null) {
            return null;
        }

        DateTime dateTime = new DateTime(date);

        return dateTime.toString(DATE_TIME_PATTERN1);
    }

    /**
     * 格式化输出日期: 2016116130411
     *
     * @param instant
     * @return
     */
    public static String formatDateTime1(long instant) {

        DateTime dateTime = new DateTime(instant);

        return dateTime.toString(DATE_TIME_PATTERN1);
    }

    /**
     * 格式化输出日期: 2016-1-16 13:04
     *
     * @param date
     * @return
     */
    public static String formatDateTime2(Date date) {

        if (date == null) {
            return null;
        }

        DateTime dateTime = new DateTime(date);

        return dateTime.toString(DATE_TIME_PATTERN2);
    }

    /**
     * 格式化输出日期: 2016-1-16 13:04:11
     *
     * @param instant
     * @return
     */
    public static String formatDateTime2(long instant) {

        DateTime dateTime = new DateTime(instant);

        return dateTime.toString(DATE_TIME_PATTERN2);
    }

    /**
     * 获取当前的时分:返回int数组
     * @return
     */
    public static int[] getHourAndMinuteInt(){
        int[] times = new int[2];
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        times[0] = hour;
        int minutes = calendar.get(Calendar.MINUTE);
        times[1] = minutes;
        return times;
    }
    public static String getHourAndMinuteString(){
        String time;
        Calendar mCalendar = Calendar.getInstance();
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);

        int minutes = mCalendar.get(Calendar.MINUTE);
        if (minutes < 10){
            time = hour+":"+"0"+minutes;
        }
        else {
            time = hour+":"+minutes;
        }
        return time;
    }

    /**
     * 以日期命名的fileName转成时间
     * @param fileName
     * @return  8/4/2017
     */
    public static String fileNameToDate(String fileName){
        String time = fileName.replace(".mp4","");
        String time2 = time.substring(0,8);
        LogUtils.e("fileNameToDate","fileNameToDate is"+time2);
        String year = time2.substring(0,4);
        String month = time2.substring(4,6);
        String day = time2.substring(6,8);
        String returnString = day+"/"+month+"/"+year;
        return returnString;
    }

    /**
     * 当前毫秒值转为闹钟结构体
     * @return
     */
    public static String currentTimeToClock(){
        String structure;
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
//        LogUtils.e("currentTimeToClock","day is "+day);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        LogUtils.e("currentTimeToClock","hour is "+hour);
        int minute = calendar.get(Calendar.MINUTE);
//        LogUtils.e("currentTimeToClock","minute is "+minute);
        String minutes = ""+minute;
        String hours = hour+"";
        if (day == 0){
            day = 7;
        }
        else {
            day -= 1;
        }
        if (minute<10){
            minutes = "0"+minute;
        }
        structure = day+hours+minutes;
//        LogUtils.e("currentTimeToClock","return is "+structure);
        return (structure);
    }

    //获取此刻距离24点整的时间间距
    public static int getDurationTo24(){
        int retrue_int = 0;
        Calendar mCalendar = Calendar.getInstance();
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minutes = mCalendar.get(Calendar.MINUTE);
        int hour_duration = 24 - hour;
        int minutes_duration = 0 - minutes;
        retrue_int = hour_duration * 3600000+minutes_duration*60*1000;
        LogUtils.e("getDurationTo24","duration is "+retrue_int+"hour is "+hour+"minutes is "+minutes);
        return retrue_int;
    }

}