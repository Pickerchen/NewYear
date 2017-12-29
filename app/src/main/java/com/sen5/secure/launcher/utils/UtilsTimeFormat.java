package com.sen5.secure.launcher.utils;

import android.content.Context;
import android.util.Log;


import com.sen5.secure.launcher.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jiangyicheng on 2016/11/7.
 */

public class UtilsTimeFormat {

    public static String getTimeDataForSystemFormat(Context context){
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        Calendar cal =Calendar.getInstance();
        Date time = cal.getTime();
        String formatDate = dateFormat.format(time);

        StringBuffer sb = new StringBuffer();
        String string = sb.append(formatDate).toString();
//        String string = sb.append(formatDate).append("_").append(formatTime).append(formatss).toString();

        return  formatDate;
    }

    public static String getTimeWeek(Context context){
        String str = "Null";
        String[] weeks = context.getResources().getStringArray(R.array.arrays_week);
        Calendar cal =Calendar.getInstance();
        int week = cal.get(Calendar.DAY_OF_WEEK);
//        Log.d("", "----------------week = " + week);
        week -=1;
        if(null != weeks && week>=0 && week< weeks.length){
            str = weeks[week];
        }
        return  str;
    }

    public static String getTimeForSystemFormat(Context context){
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        Calendar cal =Calendar.getInstance();
        Date time = cal.getTime();
        String formatDate = dateFormat.format(time);


        DateFormat dateFormatTime = android.text.format.DateFormat.getTimeFormat(context);
        cal =Calendar.getInstance();
        time = cal.getTime();
        String formatTime = dateFormatTime.format(time);

        cal =Calendar.getInstance();
        time = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(":ss");
        String formatss = sdf.format(time).toString();
//        Log.e("AA", "------formatSS = " + formatss);

        StringBuffer sb = new StringBuffer();
        String string = sb.append(formatDate).append("_").append(formatTime).append(formatss).toString();
//        String string = sb.append(formatDate).append("_").append(formatTime).append(formatss).toString();
        String replaceAll = string.trim().replaceAll(" ", "_").replaceAll(":", "_").replaceAll("/", "_");
        return  replaceAll;
    }

}
