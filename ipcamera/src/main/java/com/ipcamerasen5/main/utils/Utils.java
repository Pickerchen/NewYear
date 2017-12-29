package com.ipcamerasen5.main.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.litesuits.common.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * Created by jiangyicheng on 2016/10/27.
 */

public class Utils {

    public static String getFirstRuningApp(Context context){
        String processName = null;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if(null != am){

            processName = am.getRunningAppProcesses().get(0).processName;
        }
        return processName;
    }

    public static void startActivityByComponentName(Context context,
                                                    ComponentName component) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        try {
            intent.setComponent(component);
            context.startActivity(intent);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static int getLastTimePlayPosition(String did){
//        try {
//            List<String> list = FileUtils.readLines(new File(CAMERA_DEVICE_FILE_PATH));
//            if(null != list){
//                int count = list.size();
//                for (int i = 0; i < count; i++) {
//                    String[] split = list.get(i).replaceAll("##", "").replaceAll("-", "").split("#");
//                    String str = split[0];
//                    if(!TextUtils.isEmpty(str) && str.equals(did)){
//                        return i;
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return 0;
    }


    private static final String ACTION_CLOSE_ALL = "com.close.camera.float.window";
    public static void closeCameraFloatWindow(Context context){
        Intent intent = new Intent();
        intent.setAction(ACTION_CLOSE_ALL);
        context.sendBroadcast(intent);
    }

    /**
     * 获取设备名称+客户代码
     *
     * @param context
     * @return
     */
    public static String getProductName(Context context) {

        String productName = "";
//        Sen5ServiceManager serviceManager = (Sen5ServiceManager) context
//                .getSystemService("sen5_service");
//        if (null != serviceManager) {
//            productName = serviceManager.getProductName();
//        }
        return productName;
    }
    /**
     * 获取客户代码
     *
     * @param context
     * @return
     */
    public static String getCustomerName(Context context) {

        String productName = "";
//        Sen5ServiceManager serviceManager = (Sen5ServiceManager) context
//                .getSystemService("sen5_service");
//        if (null != serviceManager) {
//            productName = serviceManager.getCustomerName();
//        }
        return productName;
    }

    public static boolean isME031(Context context){
        if("ME031".equals(getCustomerName(context))){
            return true;
        }
        return false;
    }

}
