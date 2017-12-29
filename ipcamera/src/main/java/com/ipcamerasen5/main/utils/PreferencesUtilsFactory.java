package com.ipcamerasen5.main.utils;

/**
 * Created by jiangyicheng on 2017/1/13.
 */

public class PreferencesUtilsFactory {
    private static final String PRE_TIME_GAP = "timegap";
    private static final String PRE_LastTime_DID = "lastdid";

    public static void saveTimeGap(String timeGap){
        MySharePre.setSharePreString(PRE_TIME_GAP, timeGap);
    }

    public static String getTimeGap(String defaulttimeGap){
        return MySharePre.getSharePreString(PRE_TIME_GAP, defaulttimeGap);
    }

    public static String getTimeGap(){
        return  getTimeGap("101010");
    }

    public static String getLastTimeDID(){
        return MySharePre.getSharePreString(PRE_LastTime_DID, "");
    }

    public static void setLastTimeDID(String did){
        MySharePre.setSharePreString(PRE_LastTime_DID, did);
    }
}
