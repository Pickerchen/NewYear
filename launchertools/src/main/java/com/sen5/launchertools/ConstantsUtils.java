package com.sen5.launchertools;

/**
 * @author JesseYao
 * @version 2016年9月14日上午10:04:46 ClassName: ConstantsUtils.java 
 */
public class ConstantsUtils {

	public static final String ALLOW_BACKGRAOUND_APP_LIST = "/system/etc/allowbackgroundapp.list";
	public static final String PERSIST_SYS_CLRMEM_INTERVAL_SEC = "persist.sys.clrmem.interval.sec";
	
	public static final String PACKAGE_BACKUP_RESTORE = "com.sen5.backuprestore";
	public static final String ACTIVITY_BACKUP_RESTORE = "com.sen5.backuprestore.activity.MainActivity";
	
	public static final String PACKAGE_SECQER_SETTINGS = "com.sen5.smartlifebox.ui.main.MainActivity";

    public static final String PACKAGE_SEN5_CORE_SERVICE = "com.senfive.CoreService";
	
	/**
	 * 需要依次启动的Settings包名及Activity
	 */
	public static final String PACKAGE_TV_SETTINGS = "com.android.tv.settings";
	public static final String ACTIVITY_TV_SETTINGS = "com.android.tv.settings.MainSettings";
	public static final String PACKAGE_SEN5_SETTINGS = "com.sen5.settings";
	public static final String ACTIVITY_SEN5_SETTINGS = "com.sen5.settings.Settings";
	public static final String PACKAGE_LINKMATE_SETTINGS = "com.sen5.linkmate.settings";
	public static final String ACTIVITY_LINKMATE_SETTINGS = "com.sen5.linkmate.settings.SettingsApplication";
	public static final String PACKAGE_SETTINGS = "com.android.settings";
	public static final String ACTIVITY_SETTINGS = "com.android.settings.Settings";
	
	/**
	 * 需要一次启动的蓝牙设置
	 */
	public static final String ACTION_BLUETOOTH_SETTINGS = "android.settings.BLUETOOTH_SETTINGS";
	
	/**
	 * 需要依次启动的NetworkSettings
	 */
	public static final String ACTIVITY_TV_NETWORKSETTINGS = "com.android.tv.settings.connectivity.NetworkActivity";
	public static final String ACTIVITY_LINKMATE_NETWORKSETTINGS = "com.sen5.linkmate.settings.connectivity.NetworkActivity";
	public static final String TYPE_START_SETTING4 = "LaunchType";
	public static final String TYPE_SETTING_WIFI = "WIFI";
	public static final String TYPE_SETTING_ETHERNET = "Ethernet";
	public static final String ACTION_WIFI_SETTINGS = "android.settings.WIFI_SETTINGS";
	public static final String ACTION_ETHERNET_SETTINGS = "android.settings.ETHERNET_SETTINGS";
	
	/**
	 * 文件管理器包名
	 */
	public static final String PACKAGE_FILEBROWSER_905 = "com.droidlogic.FileBrower";
	public static final String PACAKGE_FILEBROWSER_805 = "com.fb.FileBrower";
    public static final String PACKAGE_FILEBROWSER_XIAOBAI = "com.xiaobaifile.tv";
	
	/**
	 * 下载中心包名
	 */
	public static final String PACKAGE_DOWNLOAD = "com.android.providers.downloads.ui";
	public static final String ACTIVITY_DOWNLOAD = "com.android.providers.downloads.ui.DownloadList";
	
    public static final String PACKAGE_DOWNLOAD_ANDROID_N = "com.android.documentsui";
    public static final String ACTIVITY_DOWNLOAD_ANDORID_N = "com.android.documentsui.FilesActivity";
	/**
	 * Sen5 reboot action  
	 */
	public static final String ACTION_REBOOT_SEN5 = "com.senfive.launcher.action.reboot";
	
	public static final String PACKAGE_GOOGLE_PALY_STROE = "com.android.vending";
	public static final String ACTIVITY_GOOGLE_PLAY_STROE = "com.google.android.finsky.activities.MainActivity";
	
	/**
	 * sen5 service
	 */
	public static final String SEN5_SERVICE = "sen5_service";
	public static final int MAX_SERVICES = 100;

    /**
     * WIFI信号强度变化广播
     */
    public static final String ACTION_WIFI_RSSI_CHANGED = "android.net.wifi.RSSI_CHANGED";
    /**
     * 以太网连接变化广播
     */
    public static final String ACTION_ETH_STATE_CHANGED_805 = "android.net.ethernet.ETH_STATE_CHANGED";

    /**
     * 第三方测速软件包名
     */
    public static final String PACKAGE_SPEEDTEST = "org.zwanoo.android.speedtest";

    /**
     * tools配置文件的文件名（固定的），路径由不同launcher去设置
     */
    public static final String LUANCHERTOOLS_FILE_NAME = "LauncherToolsConfig";
    
    /**
     * 系统预安装完成的广播
     */
    public static final String ACTION_SEN5_PREINSTALL_FINISH = "com.sen5.preinstall.finish";

    /**
     * 用于检查是否带有移动网络模块
     *
     * 4g、3g、2g 三种类型的属性值
     */
    public static final String RO_SEN5_HAS_MODEMTYPE = "ro.sen5.has.modemtype";


    /**
     * 配置文件的配置名
     */
    public static final String CLEAR = "clear";
    public static final String FILEBROWSER = "filebrowser";
    public static final String NETWORK = "network";
    public static final String MOBILE_NETWORK = "4G";
    public static final String BLUETOOTH = "bluetooth";
    public static final String DOWNLOAD = "download";
    public static final String NOTIFICATION = "notification";
    public static final String BACKUP_RESTORE = "backup";
    public static final String REBOOT = "reboot";
    public static final String STANDBY = "standby";
    public static final String SETTINGS = "settings";
    public static final String SPEED_TEST = "speedtest";
    public static final String SECQER_SETTINGS = "secqer";
    public static final String WALLPAPER = "wallpaper";

    public static final String THEME_COLOR = "color";
	public static final String THEME_GREY = "grey";
	public static final String THEME_BLUE = "blue";

    public static final int SIGNAL_STRENGTH_NONE_OR_UNKNOWN = 0;
    public static final int SIGNAL_STRENGTH_POOR = 1;
    public static final int SIGNAL_STRENGTH_MODERATE = 2;
    public static final int SIGNAL_STRENGTH_GOOD = 3;
    public static final int SIGNAL_STRENGTH_GREAT = 4;
    public static final String NETWORK_TYPE_2G = "2G";
    public static final String NETWORK_TYPE_3G = "3G";
    public static final String NETWORK_TYPE_4G = "4G";
    public static final String NETWORK_TYPE_UNKNOW = "unknow";

}
