package com.sen5.secure.launcher.base;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.EthernetManager;
import android.net.wifi.WifiManager;

import com.eric.xlee.lib.utils.LogUtil;
import com.facebook.stetho.Stetho;
import com.ipcamerasen5.main.MainApplication;
import com.sen5.secure.launcher.BuildConfig;
import com.sen5.secure.launcher.R;
import com.sen5.secure.launcher.data.interf.SecureCallback;
import com.sen5.secure.launcher.utils.AdapterConstantUtil;
import com.sen5.secure.launcher.utils.CustomToast;
import com.sen5.secure.launcher.workspace.CalculateView;
import com.sen5.secure.launcher.workspace.IconCache;
import com.sen5.smartlifebox.SecQreSettingApplication;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.bugly.crashreport.CrashReport;

import org.litepal.LitePalApplication;

import nes.ltlib.LTSDKManager;
import nes.ltlib.utils.AppLog;

import static com.sen5.smartlifebox.data.p2p.P2PModel.ACTION_IPCAMERA_KEY_FLOAT;
import static com.sen5.smartlifebox.data.p2p.P2PModel.ACTION_RECEIVER_CLOSE_SERVICE;
import static com.sen5.smartlifebox.data.p2p.P2PModel.ACTION_SMARTHOME_KEY_CODE;


/**
 * Created by zhoudao on 2017/5/18.
 */

public class LauncherApplication extends LitePalApplication{

    private static String TAG = LauncherApplication.class.getSimpleName();

    public static final boolean DEBUG = BuildConfig.DEBUG;


    private static boolean sIsScreenLarge;
    public LauncherModel mModel = null;
    public IconCache mIconCache;
    public static Context mContext;
    public static String activityName = "MainActivity";


//    public static boolean isSleep = false;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        final int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        sIsScreenLarge = screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
                || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;

        CalculateView.getInstance().init(this);
        initModel();
//        initStetho();


        //初始化SecQre setting
        SecQreSettingApplication qreSettingApplication = new SecQreSettingApplication(this);

        qreSettingApplication.setupLeakCanary();


        //初始化IPC
        LTSDKManager.getInstance().initLT(this);

        //初始化Bugly
        initBugly();

        //初始化IPCamera
        MainApplication mainApplication = new MainApplication(getApplicationContext());

        AppLog.init();
    }


    private void initBugly(){
        CrashReport.initCrashReport(getApplicationContext(), "30a42a29dc", true);
    }


    public static boolean isScreenLarge() {
        return sIsScreenLarge;
    }


    private void initStetho() {
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this)).build());
    }


    /**
     * 广播事件初始化
     */
    private void initModel() {
        mIconCache = new IconCache(this);
        mModel = new LauncherModel(this, mIconCache);
        // 一键打开application
        IntentFilter filter = new IntentFilter(LauncherModel.ACTION_LAUNCH_APP);
        registerReceiver(mModel, filter);
        // 对application进行增删改
        filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        registerReceiver(mModel, filter);
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        registerReceiver(mModel, filter);
        // datetime
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
//        filter.addAction(Intent.ACTION_USER_SWITCHED);
        registerReceiver(mModel, filter);
        // 网络状态改变广播
        filter = new IntentFilter();
        // filter.addAction("android.net.wifi.RSSI_CHANGED");
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        // filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        // 以太网
//        filter.addAction(EthernetManager.ETH_STATE_CHANGED_ACTION);

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mModel, filter);
        // usb
        filter = new IntentFilter();
        filter.setPriority(1000);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        // filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        // filter.addAction(Intent.ACTION_MEDIA_SHARED);
        // filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        // filter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        // filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        // filter.addAction(Intent.ACTION_MEDIA_CHECKING);
        // filter.addAction(Intent.ACTION_MEDIA_EJECT);
        // filter.addAction(Intent.ACTION_MEDIA_NOFS);
        // filter.addAction(Intent.ACTION_MEDIA_BUTTON);
        // filter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        filter.addDataScheme("file");
        registerReceiver(mModel, filter);
        // Resolution需要的广播
//        filter = new IntentFilter();
        filter.addAction(AdapterConstantUtil.ACTION_HDMI_HW_PLUGGED);
        filter.addAction(AdapterConstantUtil.ACTION_HDMI_MODE_CHANGED);
//        filter.addAction(WindowManagerPolicy.ACTION_HDMI_HW_PLUGGED);
//        filter.addAction(WindowManagerPolicy.ACTION_HDMI_MODE_CHANGED);
        // filter.addAction("action.show.dialog");
        registerReceiver(mModel, filter);
        // home键广播(com.amlogic.dvbplayer.homekey)
        filter = new IntentFilter();
        filter.addAction("com.amlogic.dvbplayer.homekey");
        registerReceiver(mModel, filter);

        filter = new IntentFilter();
        filter.addAction(AdapterConstantUtil.ACTION_CAMERA_CHANGED);
        registerReceiver(mModel, filter);

        filter = new IntentFilter();
        filter.addAction(ACTION_IPCAMERA_KEY_FLOAT);
        registerReceiver(mModel, filter);


        filter = new IntentFilter();
        filter.addAction(ACTION_RECEIVER_CLOSE_SERVICE);
        registerReceiver(mModel, filter);


        //推送10s视频广播
        filter = new IntentFilter();
        filter.addAction("com.sen5.process.camera.video");
        registerReceiver(mModel, filter);

        //注册待机广播接收
        filter = new IntentFilter();
        filter.addAction("com.amlogic.dvbplayer.powerkey");
        registerReceiver(mModel, filter);

        //注册安防按键广播
        filter = new IntentFilter();
        filter.addAction(ACTION_SMARTHOME_KEY_CODE);
        registerReceiver(mModel,filter);



    }


    /**
     * LauncherModel的响应事件
     *
     * @param activity
     * @return
     */
    public LauncherModel setLauncher(LauncherModel.Callbacks activity, SecureCallback callback) {
        mModel.initialize(activity,callback);
        return mModel;
    }

    /**
     * 按照包名启动App
     *
     * @param strPackage 包名
     * @param isHint     失败了是否需要提示
     */
    public void launchApp(String strPackage, boolean isHint) {

        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(strPackage);
        try {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            if (isHint) {
                new CustomToast(this, getString(R.string.open_app_failed));
            }
        }
    }

    /**
     * 按照包名和类名启动App
     *
     * @param strPackage 包名
     * @param strClass   类名
     * @param isHint     失败了是否需要提示
     */
    public void launchApp(String strPackage, String strClass, boolean isHint) {
        ComponentName componentName = new ComponentName(strPackage, strClass);
        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(componentName);
        try {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);
        } catch (Exception e) {
            AppLog.i(TAG+ "Open the application failed!::" + e.toString());
            if (isHint) {
                new CustomToast(this, getString(R.string.open_app_failed));
            }
        }
    }


}
