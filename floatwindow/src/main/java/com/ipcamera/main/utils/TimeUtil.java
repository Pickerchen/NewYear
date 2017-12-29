package com.ipcamera.main.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.text.format.DateFormat;

public class TimeUtil {

	/**
	 * 按HH:MM:SS格式化时�?
	 * 
	 * @param i
	 * @param isTotalTime
	 *            赋�?false即可
	 * @return
	 */
	public static String secToTime(int i, Boolean isTotalTime) {
		String retStr = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (i <= 0) {
			if (isTotalTime && i < 0)
				return "99:59:59";
			else
				return "00:00:00";
		} else {
			minute = i / 60;
			if (minute < 60) {
				second = i % 60;
				retStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
			} else {
				hour = minute / 60;
				if (hour > 99)
					return "99:59:59";
				minute = minute % 60;
				second = i % 60;
				retStr = unitFormat(hour) + ":" + unitFormat(minute) + ":"
						+ unitFormat(second);
			}
		}
		return retStr;
	}

	public static String unitFormat(int i) {
		String retStr = null;
		if (i >= 0 && i < 10)
			retStr = "0" + Integer.toString(i);
		else
			retStr = Integer.toString(i);
		return retStr;
	}

	public static String minuteToTime(int minute) {
		int hour = minute / 60;
		int min = minute % 60;
		String result = unitFormat(hour) + "Hour" + unitFormat(min) + "Min";
		return result;
	}

	/**
	 * 将分散的时间转化成毫�?
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param mintue
	 * @param seccond
	 * @return
	 */
	public static  long timeToMilliseccond(int year, int month, int day, int hour, int mintue,int seccond) {
		Calendar Calendar_sys = Calendar.getInstance();
		Calendar_sys.set(year, month, day, hour, mintue, seccond);
		return Calendar_sys.getTime().getTime();
	}
	
	/**
	 * 获取系统的日期显示格�?
	 * @param context 只设置（ 年月日），无（时分秒�?
	 * @return
	 */
	public static java.text.DateFormat getSystemDateFormat(Context context){
		java.text.DateFormat dateFormat = DateFormat.getDateFormat(context);
		return dateFormat;
	}
	
	/**
	 * 获取DTV时间 ，并按照默认格式显示 --系统日期格式+HH:mm:ss
	 * @param context
	 * @param dvbClient
	 * @return  日期+时间
	 */
	public static String getTimeFormatDefault(Context context, long time){
		Date date = new Date(time);
		java.text.DateFormat systemDateFormat = getSystemDateFormat(context);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String today = systemDateFormat.format(date) + " " + sdf.format(date);
		return today;
	}
	
	/**
	 * 获取DTV时间 ，并按照默认格式显示 --系统日期格式+自定义时间格�?
	 * @param context
	 * @param dvbClient
	 * @param timeFormat 传入的时间显示格�?
	 * @return  日期+时间
	 */
	public static String getTimeFormat(Context context, long time, String timeFormat){
		Date date = new Date(time);
		java.text.DateFormat systemDateFormat = getSystemDateFormat(context);
		SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
		String today = systemDateFormat.format(date) + " " + sdf.format(date);
		return today;
	}
}
