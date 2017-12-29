package com.ipcamerasen5.main.utils;

import android.text.TextUtils;

import java.io.File;

/**
 * Created by jiangyicheng on 2016/10/31.
 */

public class UtilsFile {
    public static boolean mkdir(String strPath){
        boolean isOK = false;
        File file = new File(strPath);
        if(!file.exists()){
            isOK = file.mkdirs();
        }
        return isOK;
    }

    //    System.load("data/app/com.sen5.klauncher-2/lib/arm/libPPPP_API.so");
//	System.load("data/app/com.sen5.klauncher-2/lib/arm/libipc.so");


    public static String getLibFilePath(String libName){
        File file = new File("data/app/");
        if(null != file && file.exists()){
            File[] listFiles = file.listFiles();
            if(null != listFiles){
                int len = listFiles.length;
                for (int i = 0; i < len; i++) {
                    String path = listFiles[i].getAbsolutePath();
                    if(!TextUtils.isEmpty(path) && path.contains("com.sen5.klauncher")){
                        return path + File.separator + "lib/arm/" + libName;
                    }
                }
            }
        }
        return null;
    }
}
