/**
 * 文件名：WifiApAdmin.java
 * 版权：Copyright 2002-2007 Sen5 Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：wanglin
 * 修改时间：2016年7月26日
 * 修改单号：
 * 修改内容：
 */

package com.sen5.smartlifebox.common.utils;

/**
 * @author wanglin
 *
 */

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import nes.ltlib.utils.AppLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/** 
 * 打开、关闭wifi热点
 * 
 */  
public class WifiApAdmin {

    private static  boolean mDefaultWifiEnable;//标识wifi默认状态

    public static void startWifiAp(Context context, String ssid, String passwd) {
        final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //开启wifi热点之前，需要先关闭wifi；开启wifi之前，需要先关闭WIFI热点
        if (wifiManager.isWifiEnabled()) {
            mDefaultWifiEnable = true;
            wifiManager.setWifiEnabled(false);
        }else {
            mDefaultWifiEnable = false;
        }

        //关闭Wifi热点
        closeWifiAp(context);

        Method method1;
        try {
            method1 = wifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);
            WifiConfiguration netConfig = new WifiConfiguration();

            netConfig.SSID = ssid;
            netConfig.preSharedKey = passwd;

            netConfig.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            netConfig.allowedKeyManagement
                    .set(WifiConfiguration.KeyMgmt.WPA_PSK);
            netConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            netConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            netConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.CCMP);
            netConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.TKIP);

            method1.invoke(wifiManager, netConfig, true);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            AppLog.e("Wifi 热点关闭异常：IllegalArgumentException!");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            AppLog.e("Wifi 热点关闭异常：IllegalAccessException!");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            AppLog.e("Wifi 热点关闭异常：InvocationTargetException!");
        } catch (SecurityException e) {
            e.printStackTrace();
            AppLog.e("Wifi 热点关闭异常：SecurityException!");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            AppLog.e("Wifi 热点关闭异常：NoSuchMethodException!");
        }

        //检测wifi热点是否打开
        MyCheckTimer timerCheck = new MyCheckTimer() {  
              
            @Override
            public void doTimerCheckWork() {  
                if (isWifiApEnabled(wifiManager)) {
                    AppLog.e("isWifiApEnabled : true" );
                    this.exit();  
                } else {
                    AppLog.e("isWifiApEnabled : false" );
                }  
            }  
  
            @Override
            public void doTimeOutWork() {  
                this.exit();
            }  
        };  
        timerCheck.start(15, 1000);
    }
  
    public static void closeWifiAp(Context context) {
        final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (isWifiApEnabled(wifiManager)) {
            try {
                Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);

                WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);

                Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method2.invoke(wifiManager, config, false);

                AppLog.e("Wifi 热点关闭成功");

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                AppLog.e("Wifi 热点关闭异常：NoSuchMethodException!");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                AppLog.e("Wifi 热点关闭异常：IllegalArgumentException!");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                AppLog.e("Wifi 热点关闭异常：IllegalAccessException!");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                AppLog.e("Wifi 热点关闭异常：InvocationTargetException!");
            }
        }

        //还原wifi状态
        wifiManager.setWifiEnabled(mDefaultWifiEnable);
    }
  
    public static boolean isWifiApEnabled(WifiManager wifiManager) {
        try {  
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);  
            return (Boolean) method.invoke(wifiManager);
  
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            AppLog.e("检测Wifi 热点使能异常：NoSuchMethodException!");
        } catch (Exception e) {
            e.printStackTrace();
            AppLog.e("检测Wifi 热点使能异常!");
        }  
  
        return false;  
    }  
  
    /**
     * 用于检测wifi热点是否成功开启
     * @author wanglin
     *
     */
    public static abstract class MyCheckTimer {
        private int mCount = 0;  
        private int mTimeOutCount = 1;  
        private int mSleepTime = 1000; // 1s  
        private boolean mExitFlag = false;  
        private Thread mThread = null;
          
        /** 
         * Do not process UI work in this. 
         */  
        public abstract void doTimerCheckWork();  
          
        public abstract void doTimeOutWork();  
          
        public MyCheckTimer() {  
            mThread = new Thread(new Runnable() {
                  
                @Override
                public void run() {  
                    // TODO Auto-generated method stub  
                    while (!mExitFlag) {  
                        mCount++;  
                        if (mCount < mTimeOutCount) {  
                            doTimerCheckWork();  
                            try {  
                                mThread.sleep(mSleepTime);  
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block  
                                e.printStackTrace();  
                                exit();  
                            }  
                        } else {  
                            doTimeOutWork();  
                        }  
                    }  
                }  
            });  
        }  
          
        /** 
         * start 
         * @param timeOutCount  How many times will check?
         * @param sleepTime ms, Every check sleep time. 
         */  
        public void start(int timeOutCount, int sleepTime) {  
            mTimeOutCount = timeOutCount;  
            mSleepTime = sleepTime;  
              
            mThread.start();  
        }  
          
        public void exit() {  
            mExitFlag = true;  
        }  
          
    } 
}  