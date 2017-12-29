package com.sen5.secure.launcher.utils;

/**
 * Created by ZHOUDAO on 2017/5/27.
 */

public class ConstantUtils {
    public static boolean SDK_6_0 = false;

    public static boolean VERSION_905 = false;

    static{
        if(Integer.valueOf(android.os.Build.VERSION.SDK_INT) < 21){
            ConstantUtils.VERSION_905 = false;

        }else if(Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= 21){
            ConstantUtils.VERSION_905 = true;
        }

        if(Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= 23){
            ConstantUtils.SDK_6_0 = true;
        }
    }
}
