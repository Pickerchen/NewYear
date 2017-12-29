package com.ipcamera.main.smarthome;

import com.ipcamera.main.utils.MySharePre;




/**
 * Created by jiangyicheng on 2017/1/13.
 */

public class PreferencesUtilsFactory {
    private static final String PRE_TIME_GAP = "timegap";

    public static void saveTimeGap(String timeGap){
        MySharePre.setSharePreString(PRE_TIME_GAP, timeGap);
    }

    public static String getTimeGap(String defaulttimeGap){
        return MySharePre.getSharePreString(PRE_TIME_GAP, defaulttimeGap);
    }

    public static String getTimeGap(){
        return  getTimeGap("101010");
    }
}
