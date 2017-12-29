package com.sen5.secure.launcher.base;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.sen5.secure.launcher.data.JsonCreator;
import com.sen5.secure.launcher.data.control.CameraControl;
import com.sen5.secure.launcher.data.interf.SecureCallback;
import com.sen5.secure.launcher.data.parserxml.ItemLaunchAppData;
import com.sen5.secure.launcher.data.parserxml.ParserXMLByPull;
import com.sen5.secure.launcher.utils.AdapterConstantUtil;
import com.sen5.secure.launcher.utils.FileCtrl;
import com.sen5.secure.launcher.utils.PreserveData;
import com.sen5.secure.launcher.utils.Utils;
import com.sen5.secure.launcher.utils.UtilsControlApp;
import com.sen5.secure.launcher.workspace.AllAppsList;
import com.sen5.secure.launcher.workspace.AppInfo;
import com.sen5.secure.launcher.workspace.DeferredHandler;
import com.sen5.secure.launcher.workspace.IconCache;
import com.sen5.smartlifebox.data.p2p.P2PModel;

import java.io.ByteArrayInputStream;
import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import nes.ltlib.utils.AppLog;

import static com.sen5.smartlifebox.SecQreSettingApplication.isSleep;
import static com.sen5.smartlifebox.data.p2p.P2PModel.ACTION_IPCAMERA_KEY_FLOAT;
import static com.sen5.smartlifebox.data.p2p.P2PModel.ACTION_RECEIVER_CLOSE_SERVICE;
import static com.sen5.smartlifebox.data.p2p.P2PModel.ACTION_SMARTHOME_KEY_CODE;

public class LauncherModel extends BroadcastReceiver {
    private static final String TAG = LauncherModel.class.getSimpleName();

    /**
     * 一键启动APP的广播Action
     */
    public static final String ACTION_LAUNCH_APP = "com.sen5.action.ONEKEY_START_APP";
    /**
     * 打开AllApps的Action
     */
    private static final String SHOW_SEN5_ALL_APPS = "sen5.all.apps";
    /**
     * 一键启动APP的配置文件
     */
    private static final String FILE_PATH = "/system/etc/LaunchAPPByKey.config";
    private WeakReference<Callbacks> mCallbacks = null;

    /**
     * 安防功能回调
     */
    private WeakReference<SecureCallback> mSecureCallback = null;


    private Object mLock = new Object();

    private final LauncherApplication mApp;
    private DeferredHandler mHandler = new DeferredHandler();
    private static final HandlerThread sWorkerThread = new HandlerThread("launcher-loader");

    static {
        sWorkerThread.start();
    }

    private static final Handler sWorker = new Handler(sWorkerThread.getLooper());
    private AllAppsList mAllAppsList = null;

    private IconCache mIconCache;
    private ArrayList<ItemLaunchAppData> mDataList;
    private PreserveData mPreserveData;
    protected int mPreviousConfigMcc;
    private ArrayList<String> mHidAppList = new ArrayList<String>();
    private final String GOOGLECOUNT1 = "com.google.android.gsf.login";
    private final String GOOGLECOUNT2 = "com.google.android.apps.plus";
    private final String GOOGLE = "com.google.android.googlequicksearchbox";
    private final String GOOGLESETTING = "com.google.android.gms";

    LauncherModel(LauncherApplication app, IconCache iconCache) {


        mHidAppList = UtilsControlApp.getHideApp();

        mApp = app;
        if (null == mPreserveData) {
            mPreserveData = new PreserveData(mApp);
        }
        mDataList = initXML();
        mAllAppsList = new AllAppsList(mApp, iconCache);
        mIconCache = iconCache;
        mAllAppsList.mListApps = queryActivityApps(app);
    }

    private String[] rejectApp = {"Settings", "IPCamera", "Backup&Restore&Update", "SecQre Settings", "FileBrowser"};

    /**
     * 获取所有App
     *
     * @return
     */
    private ArrayList<AppInfo> queryActivityApps(Context context) {
        ArrayList<AppInfo> listApp = new ArrayList<AppInfo>();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> apps = null;
        apps = packageManager.queryIntentActivities(mainIntent, 0);
        for (int i = 0; i < apps.size(); i++) {
            AppInfo appInfo = new AppInfo(context.getPackageManager(), apps.get(i), mIconCache, null);
            AppLog.e("----------AppName----------" + appInfo.title);
            listApp.add(appInfo);

        }
        AppLog.i(TAG + "App Size == " + listApp.size());
        return listApp;
    }

    public ArrayList<AppInfo> getAllApps() {
        AppLog.e(TAG + "---------------------mAllAppsList.mListApps 0= " + mAllAppsList.mListApps.size());
        for (int i = 0; i < mHidAppList.size(); i++) {
            AppInfo appInfo = new AppInfo(mHidAppList.get(i), "");
            mAllAppsList.mListApps.remove(appInfo);
        }
        AppLog.e(TAG + "---------------------mAllAppsList.mListApps 1= " + mAllAppsList.mListApps.size());
        for (int i = 0; i < mAllAppsList.mListApps.size(); i++) {
            ComponentName componentName = mAllAppsList.mListApps.get(i).componentName;
            if (componentName.getPackageName().equals(GOOGLECOUNT2)) {
                mAllAppsList.mListApps.remove(i);
                // break;
            }
            if (componentName.getPackageName().equals(GOOGLESETTING)) {
                mAllAppsList.mListApps.remove(i);
                // break;
            }
        }
        AppLog.e(TAG + "---------------------mAllAppsList.mListApps 2= " + mAllAppsList.mListApps.size());
        return mAllAppsList.mListApps;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
//        AppLog.i(TAG+ "onReceive action==" + action);
        if (Intent.ACTION_PACKAGE_CHANGED.equals(action) || Intent.ACTION_PACKAGE_REMOVED.equals(action)
                || Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            // Application的增删改
//            AppLog.i(TAG+ "Change Application..");
            final String packageName = intent.getData().getSchemeSpecificPart();
            final boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);

            int op = PackageUpdatedTask.OP_NONE;

            if (packageName == null || packageName.length() == 0) {
                // they sent us a bad intent
                return;
            }

            if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
                op = PackageUpdatedTask.OP_UPDATE;
            } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                if (!replacing) {
                    op = PackageUpdatedTask.OP_REMOVE;
                }
                // else, we are replacing the package, so a PACKAGE_ADDED will
                // be sent
                // later, we will update the package at this time
            } else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                if (!replacing) {
                    op = PackageUpdatedTask.OP_ADD;
                } else {
                    op = PackageUpdatedTask.OP_UPDATE;
                }
            }

            if (op != PackageUpdatedTask.OP_NONE) {
                enqueuePackageUpdated(new PackageUpdatedTask(op, new String[]{packageName}));
            }

        } else if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(action)) {
            // First, schedule to add these apps back in.
            String[] packages = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
            enqueuePackageUpdated(new PackageUpdatedTask(PackageUpdatedTask.OP_ADD, packages));
            // Then, rebind everything.
            startLoaderFromBackground();
        } else if (Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.equals(action)) {
            String[] packages = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
            enqueuePackageUpdated(new PackageUpdatedTask(PackageUpdatedTask.OP_UNAVAILABLE, packages));
        } else if (Intent.ACTION_LOCALE_CHANGED.equals(action)) {
            // If we have changed locale we need to clear out the labels in all
            // apps/workspace.
            startLoaderFromBackground();
            mIconCache.flush();
            mAllAppsList.mListApps = queryActivityApps(mApp);
        } else if (Intent.ACTION_CONFIGURATION_CHANGED.equals(action)) {
            // Check if configuration change was an mcc/mnc change which would
            // affect app resources
            // and we would need to clear out the labels in all apps/workspace.
            // Same handling as
            // above for ACTION_LOCALE_CHANGED
            Configuration currentConfig = context.getResources().getConfiguration();
            if (mPreviousConfigMcc != currentConfig.mcc) {
                AppLog.i(TAG + "Reload apps on config change. curr_mcc:" + currentConfig.mcc + " prevmcc:"
                        + mPreviousConfigMcc);

                startLoaderFromBackground();
            }
            // Update previousConfig
            mPreviousConfigMcc = currentConfig.mcc;

        } else if (Intent.ACTION_TIME_TICK.equals(action) || Intent.ACTION_CONFIGURATION_CHANGED.equals(action)
//                || Intent.ACTION_USER_SWITCHED.equals(action)
                || WifiManager.RSSI_CHANGED_ACTION.equals(action)
                || WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)
//                || EthernetManager.ETH_STATE_CHANGED_ACTION.equals(action)
                || BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {

            Callbacks callbacks = mCallbacks != null ? mCallbacks.get() : null;
            if (callbacks != null) {
                callbacks.titleReceiveData(intent);
            }

        } else if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
//            new CustomToast(context, context.getString(R.string.device_mounted));
            Callbacks callbacks = mCallbacks != null ? mCallbacks.get() : null;
            if (callbacks != null) {
                callbacks.titleReceiveData(intent);
            }

        } else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(action)) {
//            new CustomToast(context, context.getString(R.string.device_unmounted));
            Callbacks callbacks = mCallbacks != null ? mCallbacks.get() : null;
            if (callbacks != null) {
                callbacks.titleReceiveData(intent);
            }

        } else if (action.equals(ACTION_LAUNCH_APP)) {
            // 一键启动App功能
            int keyCode = intent.getIntExtra("keycode", -1);
            String keyName = intent.getStringExtra("keyname");
            AppLog.i(TAG + "Launch app by key::" + keyName);
            launchAppByKey(keyCode, keyName);

        } else if (action.equals(AdapterConstantUtil.ACTION_HDMI_HW_PLUGGED)
                || action.equals(AdapterConstantUtil.ACTION_HDMI_MODE_CHANGED)) {
//        } else if (action.equals(WindowManagerPolicy.ACTION_HDMI_MODE_CHANGED)
//                || action.equals(WindowManagerPolicy.ACTION_HDMI_HW_PLUGGED)) {
            Callbacks callbacks = mCallbacks != null ? mCallbacks.get() : null;
            if (callbacks != null) {
                callbacks.changeHDMIMode();
            }
        } else if (action.equals("com.amlogic.dvbplayer.homekey")) {
            AppLog.i(TAG + "onReceive Home");
            Callbacks callbacks = mCallbacks != null ? mCallbacks.get() : null;
            if (callbacks != null) {
                callbacks.onHomeKey();
            }
        } else if (action.equals(AdapterConstantUtil.ACTION_CAMERA_CHANGED)) {
            String cameraId = intent.getStringExtra("CameraDID");
            int type = intent.getIntExtra("type", -1);
            Callbacks callbacks = mCallbacks != null ? mCallbacks.get() : null;
            if (callbacks != null) {
                callbacks.CameraChange(type, cameraId);
            }
        } else if (action.equals(ACTION_IPCAMERA_KEY_FLOAT)) {
            AppLog.i(TAG + "com.sen5.process.camera.key");

            if (!intent.getBooleanExtra("from_me", false)) {
                Callbacks callbacks = mCallbacks != null ? mCallbacks.get() : null;
                if (callbacks != null) {
                    callbacks.startWindow(intent.getStringExtra("receiver_mac"));
                }
            }

        } else if (action.equals(ACTION_RECEIVER_CLOSE_SERVICE)) {

            String packName = Utils.getActivityName(context);

            if (!TextUtils.isEmpty(packName) && !packName.contains("com.ipcamerasen5.main")) {
                Callbacks callbacks = mCallbacks != null ? mCallbacks.get() : null;
                if (callbacks != null) {
                    callbacks.CameraClose(intent.getStringExtra("activityName"));
                }
            }

        } else if (action.equals("com.sen5.process.camera.video")) {
            String video_path = intent.getStringExtra("video_path");
            P2PModel.getInstance(context).sendData(JsonCreator.sendVideoPath(901, video_path));

            AppLog.i("---com.sen5.process.camera.video---" + "video_path:" + video_path);


        } else if (action.equals("com.amlogic.dvbplayer.powerkey")) {

            isSleep = intent.getExtras().getBoolean("is_go_to_sleep");

            AppLog.i("com.amlogic.dvbplayer.powerkey" + "isSleep:" + isSleep);
        } else if (action.contains(ACTION_SMARTHOME_KEY_CODE)) {

            int keyCode = intent.getIntExtra("keycode", -1);

            SecureCallback secureCallback = mSecureCallback != null ? mSecureCallback.get() : null;
            if (secureCallback != null) {
                secureCallback.smartHomeKeyCode(keyCode);
            }
        } else if (Intent.ACTION_TIME_CHANGED.equals(action)
                || Intent.ACTION_TIMEZONE_CHANGED.equals(action)) {
            AppLog.e("时间改变:更新摄像头时间");
            CameraControl.getInstance().updateTime();
        }
    }

    void enqueuePackageUpdated(PackageUpdatedTask task) {
        sWorker.post(task);
    }

    /**
     * 暂时没有用
     */
    private void startLoaderFromBackground() {

        Callbacks callbacks = mCallbacks != null ? mCallbacks.get() : null;
        if (callbacks != null) {

        }
    }

    private class PackageUpdatedTask implements Runnable {
        int mOp;
        String[] mPackages;

        public static final int OP_NONE = 0;
        public static final int OP_ADD = 1;
        public static final int OP_UPDATE = 2;
        public static final int OP_REMOVE = 3; // uninstlled
        public static final int OP_UNAVAILABLE = 4; // external media unmounted

        public PackageUpdatedTask(int op, String[] packages) {
            mOp = op;
            mPackages = packages;
        }

        public void run() {
            final Context context = mApp;

            final String[] packages = mPackages;
            final int N = packages.length;
            switch (mOp) {
                case OP_ADD:
                    for (int i = 0; i < N; i++) {
                        AppLog.i(TAG + "mAllAppsList.addPackage " + packages[i]);
                        mAllAppsList.addPackage(context, packages[i]);
                    }
                    break;
                case OP_UPDATE:
                    for (int i = 0; i < N; i++) {
                        AppLog.i(TAG + "mAllAppsList.updatePackage " + packages[i]);
                        mAllAppsList.updatePackage(context, packages[i]);
                    }
                    break;
                case OP_REMOVE:
                case OP_UNAVAILABLE:
                    for (int i = 0; i < N; i++) {
                        AppLog.i(TAG + "mAllAppsList.removePackage " + packages[i]);
                        mAllAppsList.removePackage(packages[i]);
                    }
                    break;
            }

            ArrayList<AppInfo> added = null;
            ArrayList<AppInfo> removed = null;
            ArrayList<AppInfo> modified = null;

            if (mAllAppsList.added.size() > 0) {
                added = mAllAppsList.added;
                mAllAppsList.added = new ArrayList<AppInfo>();
            }
            if (mAllAppsList.removed.size() > 0) {
                removed = mAllAppsList.removed;
                mAllAppsList.removed = new ArrayList<AppInfo>();
                for (AppInfo info : removed) {
                    mIconCache.remove(info.intent.getComponent());
                }
            }
            if (mAllAppsList.modified.size() > 0) {
                modified = mAllAppsList.modified;
                mAllAppsList.modified = new ArrayList<AppInfo>();
            }

            final Callbacks callbacks = mCallbacks != null ? mCallbacks.get() : null;
            if (callbacks == null) {
                AppLog.i(TAG + "Nobody to tell about the new app.  Launcher is probably loading.");
                return;
            }

            if (added != null) {
                final ArrayList<AppInfo> addedFinal = added;
                mHandler.post(new Runnable() {
                    public void run() {
                        Callbacks cb = mCallbacks != null ? mCallbacks.get() : null;
                        if (callbacks == cb && cb != null) {
                            // jyc
                            AppLog.e(TAG + "---------------------addedFinal= " + addedFinal.size());
                            for (int i = 0; i < mHidAppList.size(); i++) {
                                AppInfo appInfo = new AppInfo(mHidAppList.get(i), "");
                                addedFinal.remove(appInfo);
                            }
                            AppLog.e(TAG + "---------------------addedFinal11= " + addedFinal.size());
                            callbacks.bindAppsAdded(addedFinal);
                        }
                    }
                });
            }
            if (modified != null) {
                final ArrayList<AppInfo> modifiedFinal = modified;
                mHandler.post(new Runnable() {
                    public void run() {
                        Callbacks cb = mCallbacks != null ? mCallbacks.get() : null;
                        if (callbacks == cb && cb != null) {

                            // jyc
                            AppLog.e(TAG + "---------------------modified= " + modifiedFinal.size());
                            for (int i = 0; i < mHidAppList.size(); i++) {
                                AppInfo appInfo = new AppInfo(mHidAppList.get(i), "");
                                modifiedFinal.remove(appInfo);
                            }
                            AppLog.e(TAG + "---------------------modified11= " + modifiedFinal.size());
                            callbacks.bindAppsUpdated(modifiedFinal);
                        }
                    }
                });
            }
            if (removed != null) {
                final boolean permanent = mOp != OP_UNAVAILABLE;
                final ArrayList<AppInfo> removedFinal = removed;
                mHandler.post(new Runnable() {
                    public void run() {
                        Callbacks cb = mCallbacks != null ? mCallbacks.get() : null;
                        if (callbacks == cb && cb != null) {
                            callbacks.bindAppsRemoved(removedFinal, permanent);
                        }
                    }
                });
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Callbacks cb = mCallbacks != null ? mCallbacks.get() : null;
                    if (callbacks == cb && cb != null) {
                        callbacks.bindPackagesUpdated();
                    }
                }
            });
        }
    }

    /**
     * 一键启动App
     *
     * @param keyCode
     * @param keyName
     */
    private void launchAppByKey(int keyCode, String keyName) {

        long dateTime = FileCtrl.getFileLastModifiedTime(FILE_PATH);
        if (null == mDataList || dateTime != mPreserveData.readLong("Sen5Launcher", "ReadXMLTime", 0L)) {
            AppLog.i(TAG + "DateTime:" + dateTime);
            mDataList = initXML();
        }
        if (null == mDataList || mDataList.size() <= 0) {
            return;
        }
        for (ItemLaunchAppData itemData : mDataList) {
            if (itemData.getKeyCode() == keyCode) {
                if (itemData.getPackageName() == null || itemData.getPackageName().equals("")) {
                    return;
                }
                if (itemData.getClassName() == null || itemData.getClassName().equals("")) {
                    return;
                }
                if (itemData.getPackageName().equals(SHOW_SEN5_ALL_APPS)) {
                    // 显示AllApps
                    Intent intent = new Intent();
                    String strAction = itemData.getClassName();
                    // com.senfive.launcher.showAllApps
                    if (null != strAction && !("".equals(strAction))) {
                        intent.setAction(itemData.getClassName());
                        // 显示all app操作

                    }
                } else {
                    mApp.launchApp(itemData.getPackageName(), itemData.getClassName(), true);
                }
                break;
            }
        }
    }


    /**
     * 初始化XML文件
     *
     * @return 返回XML的解析后列表
     */
    private ArrayList<ItemLaunchAppData> initXML() {

        ParserXMLByPull parser = new ParserXMLByPull();
        String strXML = FileCtrl.readTxtFile(FILE_PATH);
        if (strXML == null) {
            return null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(strXML.getBytes());
        try {
            // 保存读取文件的时间
            long dateTime = FileCtrl.getFileLastModifiedTime(FILE_PATH);
            mPreserveData.writeLong("Sen5Launcher", "ReadXMLTime", dateTime);
            // 返回XML的解析后列表
            return parser.parserLaunchAppItem(bais);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mPreserveData.writeLong("Sen5Launcher", "ReadXMLTime", 0L);
            return null;
        }
    }

    public static final Comparator<AppInfo> APP_NAME_COMPARATOR = new Comparator<AppInfo>() {
        public final int compare(AppInfo a, AppInfo b) {
            int result = Collator.getInstance().compare(a.title.toString(), b.title.toString());
            if (result == 0) {
                result = a.componentName.compareTo(b.componentName);
            }
            return result;
        }
    };

    public static ComponentName getComponentNameFromResolveInfo(ResolveInfo info) {
        if (info.activityInfo != null) {
            return new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
        } else {
            return new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name);
        }
    }

    /**
     * 初始化接口
     *
     * @param callbacks
     */
    public void initialize(Callbacks callbacks, SecureCallback secureCallback) {
        synchronized (mLock) {
            mCallbacks = new WeakReference<Callbacks>(callbacks);
            mSecureCallback = new WeakReference<SecureCallback>(secureCallback);
        }
    }

    /**
     * 回调接口
     *
     * @author Matt.Xie
     */
    public interface Callbacks {
        public void bindAppsAdded(ArrayList<AppInfo> apps);

        public void bindAppsUpdated(ArrayList<AppInfo> apps);

        public void bindAppsRemoved(ArrayList<AppInfo> apps, boolean permanent);

        public void bindPackagesUpdated();

        public void titleReceiveData(Intent intent);

        public void changeHDMIMode();

        public void onHomeKey();

        public void CameraChange(int type, String CameraDID);

        public void CameraClose(String activity);

        public void startWindow(String deviceID);

    }

}
