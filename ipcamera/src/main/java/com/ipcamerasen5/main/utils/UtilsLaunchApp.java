package com.ipcamerasen5.main.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;


/**
 * Created by jiangyicheng on 2017/1/12.
 */

public class UtilsLaunchApp {

    private static final String PackageName_SecQre = "com.sen5.smartlifebox";
    private static final String ClassName_SecQre = "com.sen5.smartlifebox.ui.main.MainActivity";
    /**
     * 启动下载中心
     * @param context
     */
    public static void launchSecQreSettings(Context context) {
        launcherApp(context, PackageName_SecQre, ClassName_SecQre);
    }

    public static void launcherApp(Context context, String packageName, String className){
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packageName,
                    className));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
