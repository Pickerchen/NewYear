package com.sen5.launchertools;


import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sen5.launchertools.bean.LauncherToolsConfig;
import com.sen5.launchertools.notification.NotificationPopupWindow;
import com.sen5.launchertools.weather.WeatherView;
import com.sen5.launchertools.widget.ClearMemoryView;
import com.sen5.launchertools.widget.ScaleItemView;
import com.sen5.launchertools.widget.Sen5DateTimeView;
import com.sen5.launchertools.widget.Sen5TextView;


/**
 * @author JesseYao
 * @version 2016 2016年12月9日 上午10:47:17 ClassName：ToolsFragment.java Description：
 */
public class ToolsFragment extends Fragment
        implements OnFocusChangeListener, OnClickListener, NotificationPopupWindow.NewNoticeCallback,
        Handler.Callback, GeneralUtils.OnPhoneStateChangeListener {

    private ClearMemoryView mClearMemoryView;

    private ScaleItemView btnCleanMemory;
    private ScaleItemView btnFilebrowser;
    private ScaleItemView btnReboot;
    private ScaleItemView btnStandby;
    private ScaleItemView btnNetwork;
    private ScaleItemView btnBluetooth;
    private ScaleItemView btnSecqerSettings;
    private ScaleItemView btnBackupRestore;
    private ScaleItemView btnSettings;
    private ScaleItemView btnNotification;
    private ScaleItemView btnDownloadCenter;
    private ScaleItemView btnSpeedTest;
    private ScaleItemView btnWallpaper;
    private ScaleItemView btnSkin;

    private ImageView imgFilebrowser;
    private ImageView imgReboot;
    private ImageView imgStandby;
    private ImageView imgNetwork;
    private ImageView imgBluetooth;
    private ImageView imgSecqerSettings;
    private ImageView imgBackupRestore;
    private ImageView imgSettings;
    private ImageView imgNotification;
    private ImageView imgDownloadCenter;
    private ImageView imgSpeedTest;
    private ImageView imgWallpaper;
    private ImageView imgSkin;

    private Sen5TextView tvCleanMemory;
    private Sen5TextView tvUsbSdCard;
    private Sen5TextView tvNetwork;
    private Sen5TextView tvBluetooth;
    private Sen5TextView tvDownloadCenter;
    private Sen5TextView tvNotification;
    private Sen5TextView tvReboot;
    private Sen5TextView tvStandby;
    private Sen5TextView tvBackupRestore;
    private Sen5TextView tvSpeedTest;
    private Sen5TextView tvSettings;
    private Sen5TextView tvSecqerSettings;
    private Sen5TextView tvSkin;

    private WeatherView mWeatherView;
    private Sen5DateTimeView mDateTimeView;
    private ImageView mLogo;
    private RelativeLayout mRlTools;

    private View mNoticeHint;
    private ImageView imgMobileNetType;

    private Context mContext = null;
    private NotificationPopupWindow mNotificationWindow;

    private AlertDialogClickListener mAlertDialogListener;
    private AlertDialog.Builder mRebootStandbyAlert;

    private View mView = null;

    private int mTempPhoneType = -1;
    private int mTempSignalLevel = -1;

    //    private LauncherToolsConfig mConfig;
    private Handler mHandler;

    private String mButtomTheme = THEME_GREY;

    private enum BUTTON_TAG {
        CLEANUP,
        SKIN,
        FILEBROWSER,
        REBOOT,
        STANDBY,
        NETWORK,
        BLUETOOTH,
        BACKUPRESTORE,
        SETTINGS,
        NOTIFICATION,
        DOWNLOAD,
        SPEEDTEST,
        SECQRE,
        WALLPAPER
    }

    public ToolsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mHandler = new Handler(this);
        initNotification();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.tools_layout, container, false);
        initViews(mView);
        initPhoneStateListner();
        return mView;
    }

    private void initPhoneStateListner() {
        if (GeneralUtils.isHardwareSupportLTE()) {
            GeneralUtils.setMobileNetworkListener(mContext, this);
        }
    }

    public void setConfig(final String configPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                mConfig = GeneralUtils.readLauncherToolsConfig(configPath);
                mHandler.sendEmptyMessage(MSG_READ_CONFIG_DONE);
            }
        }).start();
    }

    private void initConfig() {
//        if (null == mConfig) {
//            return;
//        }
//        mWeatherView.setVisibility(mConfig.isWeatherVisibility() ? View.VISIBLE : View.INVISIBLE);
//        mDateTimeView.setVisibility(mConfig.isDateTimeVisibility() ? View.VISIBLE : View.INVISIBLE);
//        mRlTools.setVisibility(mConfig.isToolsVisibility() ? View.VISIBLE : View.INVISIBLE);
//        mLogo.setVisibility(mConfig.isLogoVisibility() ? View.VISIBLE : View.INVISIBLE);
//
//        mWeatherView.setWeatherTextColor(mConfig.getWeatherAndDateTimeColor());
//        mDateTimeView.setDateTimeTextColor(mConfig.getWeatherAndDateTimeColor());

        mWeatherView.setVisibility(View.INVISIBLE);
        mDateTimeView.setVisibility(View.INVISIBLE);
        mRlTools.setVisibility(View.VISIBLE);
        mLogo.setVisibility(View.INVISIBLE);
//
//        mWeatherView.setWeatherTextColor(mConfig.getWeatherAndDateTimeColor());
//        mDateTimeView.setDateTimeTextColor(mConfig.getWeatherAndDateTimeColor());


        //设置天气背景的颜色
//        mWeatherView.setWeatherBackgroundDrawable(Color.parseColor(mConfig.getWeatherBackgroundColor()),
//                Color.parseColor(mConfig.getWeatherTempColor()));

        //设置logo
//        if (null != mConfig.getLogo() && !"".equals(mConfig.getLogo())) {
//        	mLogo.setImageBitmap(BitmapFactory.decodeFile(mConfig.getLogo()));
//        }

        //设置丸子的主题
        setButtonTheme(THEME_BLUE);

//        if (null != mConfig.getToolsButtonInVisible() && !"".equals(mConfig.getToolsButtonInVisible())) {
//        String[] buttonVisibilitySettings = {mConfig.getToolsButtonInVisible().trim().split(",");
        String[] buttonVisibilitySettings = {};
        for (int i = 0; i < buttonVisibilitySettings.length; i++) {
            if (buttonVisibilitySettings[i].equals(ConstantsUtils.CLEAR)) {
                btnCleanMemory.setVisibility(View.GONE);
            } else if (buttonVisibilitySettings[i].equals(ConstantsUtils.FILEBROWSER)) {
                btnFilebrowser.setVisibility(View.GONE);
            } else if (buttonVisibilitySettings[i].equals(ConstantsUtils.NETWORK)) {
                btnNetwork.setVisibility(View.GONE);
            } else if (buttonVisibilitySettings[i].equals(ConstantsUtils.BLUETOOTH)) {
                btnBluetooth.setVisibility(View.GONE);
            } else if (buttonVisibilitySettings[i].equals(ConstantsUtils.DOWNLOAD)) {
                btnDownloadCenter.setVisibility(View.GONE);
            } else if (buttonVisibilitySettings[i].equals(ConstantsUtils.NOTIFICATION)) {
                btnNotification.setVisibility(View.GONE);
            } else if (buttonVisibilitySettings[i].equals(ConstantsUtils.BACKUP_RESTORE)) {
                btnBackupRestore.setVisibility(View.GONE);
            } else if (buttonVisibilitySettings[i].equals(ConstantsUtils.REBOOT)) {
                btnReboot.setVisibility(View.GONE);
            } else if (buttonVisibilitySettings[i].equals(ConstantsUtils.STANDBY)) {
                btnStandby.setVisibility(View.GONE);
            } else if (buttonVisibilitySettings[i].equals(ConstantsUtils.SETTINGS)) {
                btnSettings.setVisibility(View.GONE);
            } else if (buttonVisibilitySettings[i].equals(ConstantsUtils.SPEED_TEST)) {
                btnSpeedTest.setVisibility(View.GONE);
            } else if (buttonVisibilitySettings[i].equals(ConstantsUtils.SECQER_SETTINGS)) {
                btnSecqerSettings.setVisibility(View.GONE);
            } else if (buttonVisibilitySettings[i].equals(ConstantsUtils.WALLPAPER)) {
                btnWallpaper.setVisibility(View.GONE);
            }
        }
    }
//    }

    @Override
    public void onResume() {
        super.onResume();

        GeneralUtils.registerPhoneStateListener();

        IntentFilter intentFilter = new IntentFilter();
        //Bluetooth
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mStatusReceiver, intentFilter);
        //FlieBrowser
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addDataScheme("file");
        //mContext.registerReceiver(mStatusReceiver, intentFilter);
        //Net
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConstantsUtils.ACTION_WIFI_RSSI_CHANGED);
        intentFilter.addAction(ConstantsUtils.ACTION_ETH_STATE_CHANGED_805);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mStatusReceiver, intentFilter);

        if (null != mNotificationWindow) {
            mNotificationWindow.registerNotificationReceiver();
        } else {
            initNotification();
            mNotificationWindow.registerNotificationReceiver();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GeneralUtils.unregisterPhoneStateListener();
        mContext.unregisterReceiver(mStatusReceiver);
        if (null != mNotificationWindow) {
            mNotificationWindow.unregisterNotificationReciver();
        }
    }

    private void initViews(View view) {
        mWeatherView = (WeatherView) view.findViewById(R.id.weather_view);
        mDateTimeView = (Sen5DateTimeView) view.findViewById(R.id.datetime);
        mLogo = (ImageView) view.findViewById(R.id.iv_customer_logo);
        mRlTools = (RelativeLayout) view.findViewById(R.id.rl_tools_bar);

        //设置清理内存图标按钮
        btnCleanMemory = (ScaleItemView) view.findViewById(R.id.img_clean_memory);
        mClearMemoryView = (ClearMemoryView) btnCleanMemory.findViewById(R.id.img_tools_item_icon);
        tvCleanMemory = (Sen5TextView) btnCleanMemory.findViewById(R.id.tv_tools_item_name);
        btnCleanMemory.setNextFocusLeftId(R.id.img_settings);

        //设置换肤
        btnSkin = (ScaleItemView) view.findViewById(R.id.img_skin);
        imgSkin = (ImageView) btnSkin.findViewById(R.id.img_tools_item_icon);
        tvSkin = (Sen5TextView) btnSkin.findViewById(R.id.tv_tools_item_name);

        //设置外部存储按钮
        btnFilebrowser = (ScaleItemView) view.findViewById(R.id.img_usb_sd);
        imgFilebrowser = ((ImageView) btnFilebrowser.findViewById(R.id.img_tools_item_icon));
        tvUsbSdCard = (Sen5TextView) btnFilebrowser.findViewById(R.id.tv_tools_item_name);

        //设置网络按钮
        btnNetwork = (ScaleItemView) view.findViewById(R.id.img_net);
        imgNetwork = ((ImageView) btnNetwork.findViewById(R.id.img_tools_item_icon));
        imgMobileNetType = (ImageView) btnNetwork.findViewById(R.id.img_network_type);
        tvNetwork = (Sen5TextView) btnNetwork.findViewById(R.id.tv_tools_item_name);

        //设置蓝牙按钮（默认设置关闭图标）
        btnBluetooth = (ScaleItemView) view.findViewById(R.id.img_bluetooth);
        imgBluetooth = ((ImageView) btnBluetooth.findViewById(R.id.img_tools_item_icon));
        tvBluetooth = (Sen5TextView) btnBluetooth.findViewById(R.id.tv_tools_item_name);

        //设置下载中心
        btnDownloadCenter = (ScaleItemView) view.findViewById(R.id.img_dowload);
        imgDownloadCenter = ((ImageView) btnDownloadCenter.findViewById(R.id.img_tools_item_icon));
        tvDownloadCenter = (Sen5TextView) btnDownloadCenter.findViewById(R.id.tv_tools_item_name);

        //设置通知中心按钮
        btnNotification = (ScaleItemView) view.findViewById(R.id.img_notification);
        imgNotification = ((ImageView) btnNotification.findViewById(R.id.img_tools_item_icon));
        tvNotification = (Sen5TextView) btnNotification.findViewById(R.id.tv_tools_item_name);
        mNoticeHint = view.findViewById(R.id.v_notification_hint);

        //设置重启按钮
        btnReboot = (ScaleItemView) view.findViewById(R.id.img_reboot);
        imgReboot = ((ImageView) btnReboot.findViewById(R.id.img_tools_item_icon));
        tvReboot = (Sen5TextView) btnReboot.findViewById(R.id.tv_tools_item_name);

        //设置待机按钮
        btnStandby = (ScaleItemView) view.findViewById(R.id.img_power);
        imgStandby = ((ImageView) btnStandby.findViewById(R.id.img_tools_item_icon));
        tvStandby = (Sen5TextView) btnStandby.findViewById(R.id.tv_tools_item_name);

        //设置壁纸设置按钮
        btnWallpaper = (ScaleItemView) view.findViewById(R.id.img_wallpaper);
        imgWallpaper = (ImageView) btnWallpaper.findViewById(R.id.img_tools_item_icon);

        //设置备份应用按钮
        btnBackupRestore = (ScaleItemView) view.findViewById(R.id.img_backup_restore);
        imgBackupRestore = ((ImageView) btnBackupRestore.findViewById(R.id.img_tools_item_icon));
        tvBackupRestore = (Sen5TextView) btnBackupRestore.findViewById(R.id.tv_tools_item_name);

        //设置网速测试按钮
        btnSpeedTest = (ScaleItemView) view.findViewById(R.id.img_speed_test);
        imgSpeedTest = ((ImageView) btnSpeedTest.findViewById(R.id.img_tools_item_icon));
        tvSpeedTest = (Sen5TextView) btnSpeedTest.findViewById(R.id.tv_tools_item_name);

        //设置安防设置按钮
        btnSecqerSettings = (ScaleItemView) view.findViewById(R.id.img_secqer_settings);
        imgSecqerSettings = ((ImageView) btnSecqerSettings.findViewById(R.id.img_tools_item_icon));
        tvSecqerSettings = (Sen5TextView) btnSecqerSettings.findViewById(R.id.tv_tools_item_name);

        //设置Settings
        btnSettings = (ScaleItemView) view.findViewById(R.id.img_settings);
        imgSettings = ((ImageView) btnSettings.findViewById(R.id.img_tools_item_icon));
        tvSettings = (Sen5TextView) btnSettings.findViewById(R.id.tv_tools_item_name);
        btnSettings.setNextFocusRightId(R.id.img_clean_memory);

        btnCleanMemory.setTag(BUTTON_TAG.CLEANUP);
        btnSkin.setTag(BUTTON_TAG.SKIN);
        btnFilebrowser.setTag(BUTTON_TAG.FILEBROWSER);
        btnNetwork.setTag(BUTTON_TAG.NETWORK);
        btnBluetooth.setTag(BUTTON_TAG.BLUETOOTH);
        btnDownloadCenter.setTag(BUTTON_TAG.DOWNLOAD);
        btnNotification.setTag(BUTTON_TAG.NOTIFICATION);
        btnReboot.setTag(BUTTON_TAG.REBOOT);
        btnStandby.setTag(BUTTON_TAG.STANDBY);
        btnBackupRestore.setTag(BUTTON_TAG.BACKUPRESTORE);
        btnSpeedTest.setTag(BUTTON_TAG.SPEEDTEST);
        btnSecqerSettings.setTag(BUTTON_TAG.SECQRE);
        btnSettings.setTag(BUTTON_TAG.SETTINGS);
        btnWallpaper.setTag(BUTTON_TAG.WALLPAPER);

        btnCleanMemory.setOnClickListener(this);
        btnSkin.setOnClickListener(this);
        btnFilebrowser.setOnClickListener(this);
        btnNetwork.setOnClickListener(this);
        btnBluetooth.setOnClickListener(this);
        btnDownloadCenter.setOnClickListener(this);
        btnNotification.setOnClickListener(this);
        btnReboot.setOnClickListener(this);
        btnStandby.setOnClickListener(this);
        btnBackupRestore.setOnClickListener(this);
        btnSpeedTest.setOnClickListener(this);
        btnSecqerSettings.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnWallpaper.setOnClickListener(this);

        btnCleanMemory.setOnFocusChangeListener(this);
        btnSkin.setOnFocusChangeListener(this);
        btnFilebrowser.setOnFocusChangeListener(this);
        btnNetwork.setOnFocusChangeListener(this);
        btnBluetooth.setOnFocusChangeListener(this);
        btnDownloadCenter.setOnFocusChangeListener(this);
        btnNotification.setOnFocusChangeListener(this);
        btnReboot.setOnFocusChangeListener(this);
        btnStandby.setOnFocusChangeListener(this);
        btnBackupRestore.setOnFocusChangeListener(this);
        btnSpeedTest.setOnFocusChangeListener(this);
        btnSecqerSettings.setOnFocusChangeListener(this);
        btnSettings.setOnFocusChangeListener(this);
        btnWallpaper.setOnFocusChangeListener(this);

        mAlertDialogListener = new AlertDialogClickListener();

        setButtonTheme(mButtomTheme);
        setButtonTitle();
        setButtonStatus();
        setButtonVisibility();
    }

    public void setButtonTheme(String buttonTheme) {
        mButtomTheme = buttonTheme;
        if (THEME_COLOR.equals(buttonTheme)) {
            imgFilebrowser.setImageResource(R.drawable.ic_filebrowser_color);
            imgMobileNetType.setImageResource(R.drawable.level_mobile_type_color);
            imgNetwork.setImageResource(R.drawable.level_network_signal_color);
            imgDownloadCenter.setImageResource(R.drawable.ic_download_color);
            imgBluetooth.setImageResource(R.drawable.level_bluetooth_status_color);
            imgNotification.setImageResource(R.drawable.ic_notification_color);
            imgReboot.setImageResource(R.drawable.ic_reboot_color);
            imgSettings.setImageResource(R.drawable.ic_settings_color);
            imgBackupRestore.setImageResource(R.drawable.ic_backup_restore_color);
            imgSecqerSettings.setImageResource(R.drawable.ic_settings_color);
            imgSpeedTest.setImageResource(R.drawable.ic_speedtest_color);
            imgStandby.setImageResource(R.drawable.ic_power_white);
            imgWallpaper.setImageResource(R.drawable.ic_wallpaper_color);

            btnFilebrowser.setBackgroundResource(R.drawable.bg_circle_1_n);
            btnNetwork.setBackgroundResource(R.drawable.bg_circle_1_n);
            btnDownloadCenter.setBackgroundResource(R.drawable.bg_circle_1_n);
            btnBluetooth.setBackgroundResource(R.drawable.bg_circle_1_n);
            btnNotification.setBackgroundResource(R.drawable.bg_circle_1_n);
            btnReboot.setBackgroundResource(R.drawable.bg_circle_1_n);
            btnSettings.setBackgroundResource(R.drawable.bg_circle_1_n);
            btnBackupRestore.setBackgroundResource(R.drawable.bg_circle_1_n);
            btnSecqerSettings.setBackgroundResource(R.drawable.bg_circle_1_n);
            btnSpeedTest.setBackgroundResource(R.drawable.bg_circle_1_n);
            btnStandby.setBackgroundResource(R.drawable.bg_circle_1_n);
            btnWallpaper.setBackgroundResource(R.drawable.bg_circle_1_n);
        } else if (THEME_BLUE.equals(buttonTheme)) {
            imgSkin.setImageResource(R.drawable.ic_palette_skin);
            imgFilebrowser.setImageResource(R.drawable.ic_filebrowser_white);
            imgMobileNetType.setImageResource(R.drawable.level_mobile_type_white);
            imgNetwork.setImageResource(R.drawable.level_network_signal_white);
            imgDownloadCenter.setImageResource(R.drawable.ic_download_white);
            imgBluetooth.setImageResource(R.drawable.level_bluetooth_status_white);
            imgNotification.setImageResource(R.drawable.ic_notification_white);
            imgReboot.setImageResource(R.drawable.ic_reboot_white);
            imgSettings.setImageResource(R.drawable.ic_settings_white);
            imgBackupRestore.setImageResource(R.drawable.ic_backup_restore_white);
            imgSecqerSettings.setImageResource(R.drawable.ic_sec_qre);
            imgSpeedTest.setImageResource(R.drawable.ic_speedtest_white);
            imgStandby.setImageResource(R.drawable.ic_power_white);
            imgWallpaper.setImageResource(R.drawable.ic_wallpaper_color);

            if (SHOW_BUTTON_HINT) {
                btnSkin.setBackgroundResource(R.drawable.selector_tools_item_background_3);
                btnCleanMemory.setBackgroundResource(R.drawable.selector_tools_item_background_3);
                btnFilebrowser.setBackgroundResource(R.drawable.selector_tools_item_background_3);
                btnNetwork.setBackgroundResource(R.drawable.selector_tools_item_background_3);
                btnDownloadCenter.setBackgroundResource(R.drawable.selector_tools_item_background_3);
                btnBluetooth.setBackgroundResource(R.drawable.selector_tools_item_background_3);
                btnNotification.setBackgroundResource(R.drawable.selector_tools_item_background_3);
                btnReboot.setBackgroundResource(R.drawable.selector_tools_item_background_3);
                btnSettings.setBackgroundResource(R.drawable.selector_tools_item_background_3);
                btnBackupRestore.setBackgroundResource(R.drawable.selector_tools_item_background_3);
                btnSecqerSettings.setBackgroundResource(R.drawable.selector_tools_item_background_3);
                btnSpeedTest.setBackgroundResource(R.drawable.selector_tools_item_background_3);
                btnStandby.setBackgroundResource(R.drawable.selector_tools_item_background_3);
                btnWallpaper.setBackgroundResource(R.drawable.selector_tools_item_background_3);
            } else {
                btnFilebrowser.setBackgroundResource(R.drawable.selector_tools_item_background_1);
                btnNetwork.setBackgroundResource(R.drawable.selector_tools_item_background_1);
                btnDownloadCenter.setBackgroundResource(R.drawable.selector_tools_item_background_1);
                btnBluetooth.setBackgroundResource(R.drawable.selector_tools_item_background_1);
                btnNotification.setBackgroundResource(R.drawable.selector_tools_item_background_1);
                btnReboot.setBackgroundResource(R.drawable.selector_tools_item_background_1);
                btnSettings.setBackgroundResource(R.drawable.selector_tools_item_background_1);
                btnBackupRestore.setBackgroundResource(R.drawable.selector_tools_item_background_1);
                btnSecqerSettings.setBackgroundResource(R.drawable.selector_tools_item_background_1);
                btnSpeedTest.setBackgroundResource(R.drawable.selector_tools_item_background_1);
                btnStandby.setBackgroundResource(R.drawable.selector_tools_item_background_1);
                btnWallpaper.setBackgroundResource(R.drawable.selector_tools_item_background_1);
            }
        } else {
            mButtomTheme = THEME_GREY;
            imgFilebrowser.setImageResource(R.drawable.ic_filebrowser_white);
            imgMobileNetType.setImageResource(R.drawable.level_mobile_type_white);
            imgNetwork.setImageResource(R.drawable.level_network_signal_white);
            imgDownloadCenter.setImageResource(R.drawable.ic_download_white);
            imgBluetooth.setImageResource(R.drawable.level_bluetooth_status_white);
            imgNotification.setImageResource(R.drawable.ic_notification_white);
            imgReboot.setImageResource(R.drawable.ic_reboot_white);
            imgSettings.setImageResource(R.drawable.ic_settings_white);
            imgBackupRestore.setImageResource(R.drawable.ic_backup_restore_white);
            imgSecqerSettings.setImageResource(R.drawable.ic_settings_white);
            imgSpeedTest.setImageResource(R.drawable.ic_speedtest_white);
            imgStandby.setImageResource(R.drawable.ic_power_white);
            imgWallpaper.setImageResource(R.drawable.ic_wallpaper_color);
        }
    }

    private void setButtonTitle() {
        tvCleanMemory.setText(R.string.tools_clearup);
        tvUsbSdCard.setText(R.string.tools_filebrowser);
        tvNetwork.setText(R.string.tools_network);
        tvBluetooth.setText(R.string.tools_bluetooth);
        tvDownloadCenter.setText(R.string.tools_download_center);
        tvNotification.setText(R.string.tools_notification_center);
        tvReboot.setText(R.string.tools_reboot);
        tvStandby.setText(R.string.tools_standby);
        tvBackupRestore.setText(R.string.tools_backup_restore);
        tvSpeedTest.setText(R.string.tools_speed_test);
        tvSettings.setText(R.string.tools_settings);
        tvSecqerSettings.setText(R.string.tools_secqer_settings);
        tvSkin.setText(R.string.tools_skin);
    }

    private void initNotification() {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.notification, null);
        mNotificationWindow = new NotificationPopupWindow(mContext, view,
                getResources().getDimensionPixelOffset(R.dimen.dimen_notification_window_width),
                LinearLayout.LayoutParams.MATCH_PARENT, true);
        mNotificationWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mNotificationWindow.setAnimationStyle(R.style.animation_notification_center);
        mNotificationWindow.setOutsideTouchable(true);
        mNotificationWindow.setNewNoticeCallback(this);
    }

    private void setButtonStatus() {
        setNetworkStatus();
        setBluetoothStatus();
        setExternalStatus();
    }

    private void setExternalStatus() {
//        if (!GeneralUtils.isExternalStorageMounted(mContext)) {
////        if (!(GeneralUtils.isSDCardMounted(mContext) ||
////        		GeneralUtils.isUSBMounted(mContext))) {
//            btnFilebrowser.setVisibility(View.GONE);
//        } else {
//            btnFilebrowser.setVisibility(View.VISIBLE);
//        }
    }

    private void setBluetoothStatus() {
        //init bluetooth
        if (GeneralUtils.isBluetoothEnable(mContext, getString(R.string.no_bluetooth))) {
            imgBluetooth.setImageLevel(1);
        } else {
            imgBluetooth.setImageLevel(0);//蓝牙未打开
        }
    }

    private void setNetworkStatus() {
        //init network
        if (GeneralUtils.isEthernetConnected(mContext)) { //以太网连接,imageLevel = 4;
            imgNetwork.setImageLevel(4);
            imgMobileNetType.setImageLevel(10); //不显示2G、3G等网络类型
        } else if (GeneralUtils.isWifiConnected(mContext)) { //wifi连接
            int level = GeneralUtils.getWifiRssi(mContext);
            switch (level) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 5:
                    imgNetwork.setImageLevel(level);
                    imgMobileNetType.setImageLevel(10); //不显示2G、3G等网络类型
                    break;
                default:
                    imgNetwork.setImageLevel(3);
                    imgMobileNetType.setImageLevel(10); //不显示2G、3G等网络类型
                    break;
            }
        } else if (GeneralUtils.isHardwareSupportLTE()) {
            setPhoneNetStatus(mTempPhoneType, mTempSignalLevel);
        } else {
            imgNetwork.setImageLevel(5); //没有网络连接, imageLevel = 5;
            imgMobileNetType.setImageLevel(10); //不显示2G、3G等网络类型
        }
    }

    /**
     * 在有4G模块或2G等移动网络模块的情况下，设置图标信号等级的显示
     *
     * @param netType
     * @param level
     */
    private void setPhoneNetStatus(int netType, int level) {
        if (netType < 0 || level < 0) {
            imgNetwork.setImageLevel(5);
            imgMobileNetType.setImageLevel(10); //不显示2G、3G等网络类型
            return;
        }
        imgMobileNetType.setImageLevel(netType);
        imgNetwork.setImageLevel(level);
    }

    /**
     * 设置需要显示的功能按键
     */
    private void setButtonVisibility() {
        btnBackupRestore.setVisibility(GeneralUtils.isApkInstalled(mContext,
                ConstantsUtils.PACKAGE_BACKUP_RESTORE) ? View.VISIBLE : View.GONE);
        btnBluetooth.setVisibility(GeneralUtils.isHardwareSupportBluetooth(mContext) ?
                View.VISIBLE : View.GONE);
        btnFilebrowser.setVisibility(GeneralUtils.isApkInstalled(mContext, ConstantsUtils.PACAKGE_FILEBROWSER_805) ||
                GeneralUtils.isApkInstalled(mContext, ConstantsUtils.PACKAGE_FILEBROWSER_905) ||
                GeneralUtils.isApkInstalled(mContext, ConstantsUtils.PACKAGE_FILEBROWSER_XIAOBAI) ? View.VISIBLE : View.GONE);
        btnCleanMemory.setVisibility(SHOW_CLEAN_MEMORY_BUTTON ? View.VISIBLE : View.GONE);
        btnNetwork.setVisibility(GeneralUtils.isHardwareSupportNetwork(mContext) ?
                View.VISIBLE : View.GONE);
        btnCleanMemory.setVisibility(SHOW_CLEAN_MEMORY_BUTTON ? View.VISIBLE : View.GONE);
        btnNotification.setVisibility(SHOW_NOTIFICATION_BUTTON ? View.VISIBLE : View.GONE);
        btnReboot.setVisibility(SHOW_REBOOT_BUTTON ? View.VISIBLE : View.GONE);
//        btnSecqerSettings.setVisibility(GeneralUtils.isApkInstalled(mContext,
//                ConstantsUtils.PACKAGE_SECQER_SETTINGS) ? View.VISIBLE : View.GONE);

        //将SecQre Setting 合到 Launcher 里面
        btnSecqerSettings.setVisibility(View.VISIBLE);

        btnStandby.setVisibility(SHOW_STANDBY_BUTTON ? View.VISIBLE : View.GONE);
        btnDownloadCenter.setVisibility(GeneralUtils.isApkInstalled(mContext,
                ConstantsUtils.PACKAGE_DOWNLOAD) ? View.VISIBLE : View.GONE);
        btnSpeedTest.setVisibility((SHOW_SPEED_TEST_BUTTON && GeneralUtils.isApkInstalled(mContext,
                ConstantsUtils.PACKAGE_SPEEDTEST)) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View arg0) {
        switch ((BUTTON_TAG) arg0.getTag()) {
            case CLEANUP:
                mClearMemoryView.startClearApps();
                break;
            case FILEBROWSER:
                GeneralUtils.lunchFileBrowser(mContext);
                break;
            case NETWORK:
                if (GeneralUtils.isHardwareSupportLTE()
                        && mTempPhoneType > -1 && mTempSignalLevel > -1
                        && !GeneralUtils.isEthernetConnected(mContext)
                        && !GeneralUtils.isWifiConnected(mContext)) {
                    GeneralUtils.lunchMobileNetworkSettings(mContext);
                } else {
                    GeneralUtils.lunchNetworkSettings(mContext);
                }
                break;
            case BLUETOOTH:
                GeneralUtils.lunchBluetoothSettings(mContext, getString(R.string.no_bluetooth));
                break;
            case DOWNLOAD:
                GeneralUtils.lunchDownloadCenter(mContext);
                break;
            case NOTIFICATION:
                if (null == mNotificationWindow) {
                    initNotification();
                }
                if (null != mNotificationWindow && !mNotificationWindow.isShowing()) {
                    mNotificationWindow.showAtLocation(mView, Gravity.START, 0, 0);
                    mNoticeHint.setVisibility(View.INVISIBLE);
                }
                break;
            case REBOOT:
                showRebootStandbyAlert(BUTTON_TAG.REBOOT);
                break;
            case STANDBY:
                showRebootStandbyAlert(BUTTON_TAG.STANDBY);
                break;
            case BACKUPRESTORE:
                GeneralUtils.lunchBackupRestore(mContext, getString(R.string.no_backuprestore));
                break;
            case SECQRE:
                GeneralUtils.lunchSecqreSettings(mContext);
                break;
            case SETTINGS:
                GeneralUtils.lunchSettings(mContext);
                break;
            case SPEEDTEST:
                GeneralUtils.lunchSpeedTest(mContext);
                break;
            case WALLPAPER:
                GeneralUtils.lunchWallpaperSettings(mContext, getString(R.string.no_wallpaper_settings));
                break;

            case SKIN:
                Intent intent = new Intent();
                intent.setAction("com.sen5.intent.action.SET_WALLPAPER");
                getActivity().startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean focused) {
        switch ((BUTTON_TAG) view.getTag()) {
            case CLEANUP:
                setViewVisibility(tvCleanMemory, focused);
                break;
            case FILEBROWSER:
                setViewVisibility(tvUsbSdCard, focused);
                break;
            case NETWORK:
                setViewVisibility(tvNetwork, focused);
                break;
            case BLUETOOTH:
                setViewVisibility(tvBluetooth, focused);
                break;
            case DOWNLOAD:
                setViewVisibility(tvDownloadCenter, focused);
                break;
            case NOTIFICATION:
                setViewVisibility(tvNotification, focused);
                break;
            case REBOOT:
                setViewVisibility(tvReboot, focused);
                break;
            case STANDBY:
                setViewVisibility(tvStandby, focused);
                break;
            case BACKUPRESTORE:
                setViewVisibility(tvBackupRestore, focused);
                break;
            case SECQRE:
                setViewVisibility(tvSecqerSettings, focused);
                break;
            case SETTINGS:
                setViewVisibility(tvSettings, focused);
                break;
            case SPEEDTEST:
                setViewVisibility(tvSpeedTest, focused);
                break;

            case SKIN:
                setViewVisibility(tvSkin, focused);
                break;
            default:
                break;
        }
    }

    private void setViewVisibility(View view, boolean focused) {
        if (!SHOW_BUTTON_HINT) {
            return;
        }
        if (focused) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void newNoticeReceived() {
        if (null != mNotificationWindow && !mNotificationWindow.isShowing()) {
            mNoticeHint.setVisibility(View.VISIBLE);
        }
    }

    private void showRebootStandbyAlert(BUTTON_TAG tag) {
        if (null == mRebootStandbyAlert) {
            if (Build.VERSION.SDK_INT <= 19) {
                mRebootStandbyAlert = new AlertDialog.Builder(mContext);
            } else {
                mRebootStandbyAlert = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Light_Dialog_Alert);
            }
            mRebootStandbyAlert.create();
        }
        if (null == mAlertDialogListener) {
            mAlertDialogListener = new AlertDialogClickListener();
        }
        if (BUTTON_TAG.REBOOT == tag) {
            mAlertDialogListener.setTag(tag);
            mRebootStandbyAlert.setMessage(R.string.dialog_reboot_message)
                    .setTitle(R.string.dialog_reboot_title)
                    .setPositiveButton(android.R.string.ok, mAlertDialogListener)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setCancelable(true)
                    .show();
        } else if (BUTTON_TAG.STANDBY == tag) {
            mAlertDialogListener.setTag(tag);
            mRebootStandbyAlert.setMessage(R.string.dialog_standby_message)
                    .setTitle(R.string.dialog_standby_title)
                    .setPositiveButton(android.R.string.ok, mAlertDialogListener)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setCancelable(true)
                    .show();
        }
    }

    BroadcastReceiver mStatusReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            Log.d(TAG, TAG + ".....:" + action);
            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)
                    || action.equals(ConstantsUtils.ACTION_WIFI_RSSI_CHANGED)
                    || action.equals(ConstantsUtils.ACTION_ETH_STATE_CHANGED_805)
                    || action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                setNetworkStatus();
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int newState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -75);
                int oldState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, -75);
//                Log.d("TAG", "===========================newState = " + newState
//                        + "::::oldState = " + oldState);
                setBluetoothStatus();
            } else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(action) ||
                    Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
                setExternalStatus();
            }
        }
    };

    public void setButtonVisibility(int option, boolean show) {
        switch (option) {
            case REBOOT_BUTTON:
                SHOW_REBOOT_BUTTON = show;
                break;
            case STANDBY_BUTTON:
                SHOW_STANDBY_BUTTON = show;
                break;
            case NOTIFICATION_BUTTON:
                SHOW_NOTIFICATION_BUTTON = show;
                break;
            case CLEAN_MEMORY_BUTTON:
                SHOW_CLEAN_MEMORY_BUTTON = show;
                break;
            case SPEED_TEST_BUTTON:
                SHOW_SPEED_TEST_BUTTON = show;
                break;
        }
        setButtonVisibility();
    }

    /**
     * 设置天气控件是否可见
     *
     * @param visibility
     */
    public void setWeatherViewVisibility(int visibility) {
        mWeatherView.setVisibility(visibility);
    }

    /**
     * 设置天气和时间的字体颜色
     *
     * @param color
     */
    public void setWeatherAndDateTimeColor(String color) {
        mWeatherView.setWeatherTextColor(color);
        mDateTimeView.setDateTimeTextColor(color);
    }

    /**
     * 设置天气和时间的字体颜色
     *
     * @param color
     */
    public void setWeatherAndDateTimeColor(int color) {
        mWeatherView.setWeatherTextColor(color);
        mDateTimeView.setDateTimeTextColor(color);
    }

    /**
     * 设置天气背景的颜色
     *
     * @param color
     */
    public void setWeatherBackgroundColor(String color) {
        mWeatherView.setWeatherBackgroundColor(color);
    }

    /**
     * 设置天气的背景色
     *
     * @param color
     */
    public void setWeatherBackgroundColor(int color) {
        mWeatherView.setWeatherBackgroundColor(color);
    }

    /**
     * 设置DateTimeView是否可见
     *
     * @param visibility
     */
    public void setDateTimeViewVisibility(int visibility) {
        mDateTimeView.setVisibility(visibility);
    }

    /**
     * 设置客制化Logo是否可见
     *
     * @param visibility
     */
    public void setLogoVisibility(int visibility) {
        mLogo.setVisibility(visibility);
    }

    /**
     * 设置客制化logo
     *
     * @param logo
     */
    public void setLogoImage(Bitmap logo) {
        mLogo.setImageBitmap(logo);
    }
    public void setShowButtonHint(boolean showButtonHint) {
        SHOW_BUTTON_HINT = showButtonHint;
    }

    @Override
    public void OnPhoneStateChange(int networkClass, int signalStrength) {
        Log.d(TAG, "-----OnPhoneStateChange: networkClass = " + networkClass + "  signalStrength = " + signalStrength);
        int classLevel = 0;
        if (networkClass > -1 && networkClass < 4) {
            classLevel = networkClass;
        }
        signalStrength = signalStrength + 6; //在实际强度上+6，用于设置
        mTempPhoneType = classLevel;
        mTempSignalLevel = signalStrength;
        setNetworkStatus();
    }

    private class AlertDialogClickListener implements DialogInterface.OnClickListener {
        private BUTTON_TAG tag;

        public void setTag(BUTTON_TAG tag) {
            this.tag = tag;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (BUTTON_TAG.REBOOT == tag) {
                GeneralUtils.rebootSystem(mContext);
            } else if (BUTTON_TAG.STANDBY == tag) {
                GeneralUtils.standbySystem();
            } else {
//                Log.d(TAG, "------Reboot or Standby failed! AlertDialog tag is null!------");
            }
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_READ_CONFIG_DONE:
                initConfig();
                break;
            default:
                break;
        }
        return false;
    }

    public static final int REBOOT_BUTTON = 0x01;
    public static final int STANDBY_BUTTON = 0x02;
    public static final int NOTIFICATION_BUTTON = 0x03;
    public static final int CLEAN_MEMORY_BUTTON = 0x04;
    public static final int SPEED_TEST_BUTTON = 0x05;

    public static final String THEME_COLOR = "color";
    public static final String THEME_GREY = "grey";
    public static final String THEME_BLUE = "blue";

    public static final int CLEAR = 0x013;//"clear";
    public static final int FILEBROWSER = 0x014;//"filebrowser";
    public static final int NETWORK = 0x015;//"network";
    public static final int BLUETOOTH = 0x016;//"bluetooth";
    public static final int DOWNLOAD = 0x017;//"download";
    public static final int NOTIFICATION = 0x018;//"notification";
    public static final int BACKUP_RESTORE = 0x019;//"backup";
    public static final int REBOOT = 0x020;//"reboot";
    public static final int STANDBY = 0x021;//"standby";
    public static final int SETTINGS = 0x022;//"settings";
    public static final int SPEED_TEST = 0x023;//"speedtest";
    public static final int SECQER_SETTINGS = 0x024;//"SecqerSettings";

    private boolean SHOW_REBOOT_BUTTON = true;
    private boolean SHOW_STANDBY_BUTTON = true;
    private boolean SHOW_NOTIFICATION_BUTTON = true;
    private boolean SHOW_CLEAN_MEMORY_BUTTON = true;
    private boolean SHOW_SPEED_TEST_BUTTON = true;
    private boolean SHOW_BUTTON_HINT = true;

    private static final int MSG_READ_CONFIG_DONE = 0x80;

    private static final String TAG = ToolsFragment.class.getSimpleName();
}
