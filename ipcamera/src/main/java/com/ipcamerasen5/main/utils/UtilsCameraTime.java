package com.ipcamerasen5.main.utils;


import nes.ltlib.utils.LogUtils;

import java.util.TimeZone;

/**
 * Created by jiangyicheng on 2017/1/13.
 */

public class UtilsCameraTime {
    /**
     * 保存当前时间
     */
    public static void saveNewCurrentTimeGap() {
        String currentTimeGap = TimeZone.getDefault().getRawOffset() + "";

        if(isTimeGapChange()){
            PreferencesUtilsFactory.saveTimeGap(currentTimeGap);
        }
    }

    public static String getNewCurrentTimeGap(){
        return PreferencesUtilsFactory.getTimeGap();
    }

    public static boolean isTimeGapChange(){
        String currentTimeGap = TimeZone.getDefault().getRawOffset() + "";
        String timeGap = PreferencesUtilsFactory.getTimeGap();
        LogUtils.e("isTimeGapChange","-------------标准时间差是："+currentTimeGap + "  timeGap = " + timeGap);
        if (null != timeGap && !timeGap.equals(currentTimeGap)) {
            LogUtils.e("isTimeGapChange","-------------标准时间差是："+currentTimeGap);
            return true;
        }
        return false;
    }
}
