package com.ipcamerasen5.main.utils;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jiangyicheng on 2016/11/7.
 */

public class UtilsTimeFormat {

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
