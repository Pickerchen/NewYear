package com.ipcamera.main.utils;

/**
 * Created by ZHOUDAO on 2017/9/5.
 */

public class ClickFastUtil {

    private static long currentTime;

    public static boolean clickFast() {
        if (System.currentTimeMillis() - currentTime > 500) {

            currentTime = System.currentTimeMillis();

            return false;
        }


        return true;
    }
}
