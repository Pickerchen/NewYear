package com.ipcamera.main.smarthome;




import java.util.TimeZone;

import com.ipcamera.main.utils.DLog;


/**
 * Created by jiangyicheng on 2017/1/13.
 */

public class UtilsCameraTime {
    public static boolean getIsNewCurrentTimeGap() {
        String currentTimeGap = TimeZone.getDefault().getRawOffset() + "";

        if(isTimeGapChange()){
            PreferencesUtilsFactory.saveTimeGap(currentTimeGap);
            return true;
        }
        return false;
    }

    public static String getNewCurrentTimeGap(){
        return PreferencesUtilsFactory.getTimeGap();
    }

    private static boolean isTimeGapChange(){
        String currentTimeGap = TimeZone.getDefault().getRawOffset() + "";
        String timeGap = PreferencesUtilsFactory.getTimeGap();
        DLog.e("-------------标准时间差是："+currentTimeGap + "  timeGap = " + timeGap);
        if (null != timeGap && !timeGap.equals(currentTimeGap)) {
        	DLog.e("-------------标准时间差是："+currentTimeGap);
            return true;
        }
        return false;
    }
}
