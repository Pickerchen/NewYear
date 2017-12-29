package com.sen5.smartlifebox;

import android.app.Application;
import android.content.Context;
import android.sen5.Sen5ServiceManager;

import nes.ltlib.utils.AppLog;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;


import java.util.ArrayList;
import java.util.List;

import nes.ltlib.data.CameraEntity;

/**
 * Created by wanglin on 2016/8/25.
 */

public class SecQreSettingApplication {

    public static final boolean DEBUG = true;

    public static SecQreSettingApplication instance;


    // TODO: 2017/2/9 待改进，不放在App中
    private static List<CameraEntity> mCameras = new ArrayList<>();//存储已经添加了的摄像头


    public static boolean mDefaultWifiEnable;//标识wifi默认是否打开


    public static boolean isSleep = false;

    public static Context mContext;

    /**
     * 是否支持wifi配置摄像头
     */
    public static boolean isSupportWIFI = false;


    public static String deviceId;


    public SecQreSettingApplication(Context context) {
        mContext = context;
        onCreate();
    }


    public void onCreate() {
        checkTrustBox();
        instance = this;
        //初始化Logger

//        mDefaultWifiEnable = ((WifiManager)getSystemService(WIFI_SERVICE)).isWifiEnabled();


    }

    private void checkTrustBox() {
        Sen5ServiceManager mSen5ServiceManager = (Sen5ServiceManager) mContext.getSystemService("sen5_service");
        mSen5ServiceManager.getProductName();
    }


    public static boolean isDefaultWifiEnable() {
        AppLog.e("WIFI 默认是否打开：" + mDefaultWifiEnable);
        return mDefaultWifiEnable;

    }

    public static List<CameraEntity> getCameras() {
        return mCameras;
    }


    public RefWatcher setupLeakCanary() {
//        if (LeakCanary.isInAnalyzerProcess(mContext)) {
//            return RefWatcher.DISABLED;
//        }
        refWatcher = LeakCanary.install((Application) mContext);
        return refWatcher;
    }


    public RefWatcher refWatcher;

    public static RefWatcher getRefWatcher() {

        return instance.refWatcher;
    }


}
