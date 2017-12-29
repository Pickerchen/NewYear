package com.sen5.launchertools;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.input.InputManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.sen5.Sen5ServiceManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.widget.Toast;

import com.sen5.launchertools.bean.LauncherToolsConfig;
import com.sen5.launchertools.xmlparser.XmlUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author JesseYao 
 * @version 2016 2016年12月7日 下午2:06:21
 * ClassName：GeneralUtil.java 
 * Description：
*/
public class GeneralUtils {
	
	private static List<String> mAllowBackgroundList = null;
	private static Sen5ServiceManager mSen5ServiceManager = null;
    private static TelephonyManager mTelephonyManager = null;
    private static PhoneStateListener mPhoneStateListener = null;
	
	/**
	 * 清理内存
	 * @param context
	 */
	public static void cleanMemory(final Context context, final CleanUpCallback callback) {
		if (null == mAllowBackgroundList || mAllowBackgroundList.size() == 0) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					mAllowBackgroundList = getAllowBackgroundList();
					clean(context, callback);
				}
			}).start();
		} else {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					clean(context, callback);
				}
			}).start();
		}
	}
	
	/**
	 * 开启自动清理内存的服务
	 * @param context
	 */
	public static void startAutoCleanUp(Context context) {
		try {
			Intent intent = new Intent(context, CleanerService.class);
			context.startActivity(intent);
		} catch (Exception e) {
			Log.e(TAG, "------start CleanerService failed!-----");
		}
	}
	
	/**
	 * 设置自动清理内存的周期
	 * @param period
	 */
	public static void setAutoCleanUpPeriod(int period) {
		SystemProperties.set(ConstantsUtils.PERSIST_SYS_CLRMEM_INTERVAL_SEC, String.valueOf(period));
	}
	
	/**
	 * 获取系统配置的允许运行于后台的应用列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static List<String> getAllowBackgroundList(){
		List<String> applist = new ArrayList<String>();
		try {
			File file = new File(ConstantsUtils.ALLOW_BACKGRAOUND_APP_LIST);
			applist.addAll(FileUtils.readLines(file));
			if(applist != null){
				int i = 0;
				for(String str : applist){
					Log.d(TAG, "Allow packName " + (i++) + " : "+ str);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return applist;
	}
	
	/**
	 * 用于判断系统是否处于低内存状态
	 * @param context
	 * @return
	 */
	public static boolean isLowMemory(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo info = new MemoryInfo();
		am.getMemoryInfo(info);
		return info.lowMemory;
	}
	
	/**
	 * 获取系统总运行内存大小 
	 * @param context
	 * @return
	 */
    public static long getTotalMemory(Context context) { 
        
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE); 
        MemoryInfo mi = new MemoryInfo(); 
        am.getMemoryInfo(mi); 
        return mi.totalMem / (1024 * 1024);
    }
	
	/**
	 * 获取系统可用内存大小
	 * @param context
	 * @return
	 */
	public static long getAvailMemory(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// mi.availMem;
		// return Formatter.formatFileSize(context, mi.availMem);
		//Log.d(TAG, "availMemory---->>>" + mi.availMem / (1024 * 1024));
		return mi.availMem / (1024 * 1024);
	}
	
	/**
	 * 获取系统已使用的内存大小 
	 * @param context
	 * @return
	 */
    public static long getUsedMemory(Context context) { 
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE); 
        MemoryInfo mi = new MemoryInfo(); 
        am.getMemoryInfo(mi); 
        return (mi.totalMem - mi.availMem) / (1024 * 1024); 
    }
    
    /**
     * 获取系统已使用内存占总内存的比例
     * @param context
     * @return
     */
    public static int getAvailMemProportion(Context context) {
    	
    	ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    	MemoryInfo mi = new MemoryInfo();
    	am.getMemoryInfo(mi);
    	int nProportion = (int)((mi.totalMem - mi.availMem)  * 100/ mi.totalMem);
    	return nProportion;
    }
	
    /**
     * 获取允许运行与后台的应用列表
     * @return
     */
	public static List<String> getAllowBackgroundAppList(){ 
		if (null != mAllowBackgroundList && mAllowBackgroundList.size() != 0) {
			return mAllowBackgroundList;
		} else {
			return getAllowBackgroundList();
		}
	}
	
	/**
	 * 清理内存
	 * @param context
	 */
	private static synchronized void clean(Context context, CleanUpCallback callback) {
		Log.d(TAG, "------------Start clean-up!-------------");
		
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infoList = am.getRunningAppProcesses();
		List<RunningServiceInfo> serviceList = am.getRunningServices(100);
		Log.d(TAG, "ListSize  = " + infoList.size() + ", serviceList = " + serviceList.size());
		long beforeMem = getAvailMemory(context);
		Log.d(TAG, "-----------before memory info : " + beforeMem);
		int count = 0;
		if (infoList != null) {
			for (int i = 0; i < infoList.size(); ++i) {
				RunningAppProcessInfo appProcessInfo = infoList.get(i);
				Log.d(TAG, "process name : " + appProcessInfo.processName);
				// importance 该进程的重要程度 分为几个级别，数值越低就越重要。
				Log.d(TAG, "importance : " + appProcessInfo.importance);

				// 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
				// 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
				if (appProcessInfo.importance > RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
					String[] pkgList = appProcessInfo.pkgList;
					for (int j = 0; j < pkgList.length; ++j) {// pkgList// 得到该进程下运行的包名
						if(!isAllowBackground(pkgList[j])){
							Log.d(TAG, "It will be killed, package name : " + pkgList[j]);
							am.killBackgroundProcesses(pkgList[j]);
							count++;
						}
					} 
				}
			}
		}

		long afterMem = getAvailMemory(context);
		Log.d(TAG, "----------- after memory info : " + afterMem);
		Log.d(TAG, TAG + ".clear " + count + " process, " + (afterMem - beforeMem) + "M");
		if (null != callback) {
			callback.cleanUp(count, afterMem - beforeMem);
		} else {
			Toast.makeText(context, "clear " + count + " process, " + (afterMem - beforeMem) + "M", Toast.LENGTH_LONG).show();
		}
		Log.d(TAG, "-------------Clean-up over!-----------------");
	}
	
	public interface CleanUpCallback {
		public void cleanUp(int processCount, long cleanMemory);
	}
	
	/**
	 * check application is allow to running in background or not 
	 * @param packageName
	 * @return
	 */
	private static boolean isAllowBackground(String packageName){
		if (null == mAllowBackgroundList || mAllowBackgroundList.size() == 0) {
			return false;
		} else if (mAllowBackgroundList.indexOf(packageName) != -1) {
			return true;
		} 
		return false;
	}
	
	/**
	 * 通过包名判断apk是否已安装
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isApkInstalled(Context context, String packageName){
    	if(null == packageName || "".equals(packageName)){
    		return false;
    	}
    	try {
    		@SuppressWarnings("unused")
			ApplicationInfo info = context.getPackageManager()
    				.getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
    		return true;
    	} catch (NameNotFoundException e) {
    		return false;
    	}
    }
	
	/**
	 * 用于判断应用是否正在运行
	 * 
	 * @param context
	 * @param packageName
	 * @return true 在运行 false 不在运行
	 */
	public static boolean isProcessRunningForPackage(Context context, String packageName) {
		boolean IsRunning = false;
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> serviceList = activityManager.getRunningAppProcesses();
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			//Log.d(TAG, TAG + ".Processes == " + serviceList.get(i).processName);
			if (packageName.equals(serviceList.get(i).processName)) {
				IsRunning = true;
				break;
			}
		}
		return IsRunning;
	}
	
	/**
	 * 用于判断某个Service是否正在运行中
	 * @param context
	 * @return 已启动服务返回true，反之还回false
	 */
	public static boolean isServiceWorking(Context context, String strServiceName) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) manager
				.getRunningServices(ConstantsUtils.MAX_SERVICES);
		for (int i = 0; i < runningService.size(); i++) {
			//Log.d(TAG, TAG + ".Servies == " + runningService.get(i).service.getClassName().toString());
			if (runningService.get(i).service.getClassName().toString()
					.equals(strServiceName)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断盒子是否有DVB功能
	 * @return 
	 */
	public static boolean hasDVB(Context context) {
		String dvb = SystemProperties.get("ro.sen5.dvb.type");
		if(dvb == null || dvb.equals("")) {
			return false;
		}
		return true;
	}
	
	/**
	 * 启动Settings
	 * @param context
	 */
	public static void lunchSettings(Context context) {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		try {
			intent.setComponent(new ComponentName(ConstantsUtils.PACKAGE_TV_SETTINGS, ConstantsUtils.ACTIVITY_TV_SETTINGS));
			context.startActivity(intent);
		} catch (Exception e) {
			Log.e(TAG, "-----Can not start tvSettings!------");
			try {
				intent.setComponent(new ComponentName(ConstantsUtils.PACKAGE_SEN5_SETTINGS, ConstantsUtils.ACTIVITY_SEN5_SETTINGS));
				context.startActivity(intent);
			} catch (Exception e1) {
				Log.e(TAG, "-----Can not start Sen5Settings!------");
				try {
					intent.setComponent(new ComponentName(ConstantsUtils.PACKAGE_LINKMATE_SETTINGS, ConstantsUtils.ACTIVITY_LINKMATE_SETTINGS));
					context.startActivity(intent);
				} catch (Exception e2) {
					Log.e(TAG, "-----Can not start LinkMateSettings!------");
					try {
						intent.setComponent(new ComponentName(ConstantsUtils.PACKAGE_SETTINGS, ConstantsUtils.ACTIVITY_SETTINGS));
						context.startActivity(intent);
					} catch (Exception e3) {
						e3.printStackTrace();
						Log.e(TAG, "-----Can not start Settings!------");
					}
				}
			}
		}
	}
	
	/**
	 * 启动蓝牙设置
	 * @param context
	 */
	public static void lunchBluetoothSettings(Context context, String noBluetoothHint) {
		if (!isHardwareSupportBluetooth(context)) {
			Log.e(TAG, "----------No Bluetooth---------");
			if (null != noBluetoothHint && !"".equals(noBluetoothHint)) {
				Toast.makeText(context, noBluetoothHint, Toast.LENGTH_SHORT).show();
			}
			return;
		}
		try {
			Intent intent = new Intent();
			intent.setAction(ConstantsUtils.ACTION_BLUETOOTH_SETTINGS);
			context.startActivity(intent);
		} catch (Exception e) {
			Log.e(TAG, "--------lunch bluetooth settings failed!-------");
			e.printStackTrace();
		}
	}
	
	/**
	 * 蓝牙是否开启
	 * @param context
	 * @param noBluetoothHint
	 * @return
	 */
	public static boolean isBluetoothEnable(Context context, String noBluetoothHint) {
		if (!isHardwareSupportBluetooth(context)) {
			Log.e(TAG, "----------No Bluetooth---------");
			if (null != noBluetoothHint && !"".equals(noBluetoothHint)) {
				Toast.makeText(context, noBluetoothHint, Toast.LENGTH_SHORT).show();
			}
			return false;
		}
		return BluetoothAdapter.getDefaultAdapter().isEnabled();
	}
	
	/**
	 * 判断蓝牙是否处于连接状态
	 * @param context
	 * @param noBluetoothHint
	 * @return
	 */
	public static boolean isBluetoothConnected(Context context, String noBluetoothHint) {
		if (!isHardwareSupportBluetooth(context)) {
			Log.e(TAG, "----------No Bluetooth---------");
			if (null != noBluetoothHint && !"".equals(noBluetoothHint)) {
				Toast.makeText(context, noBluetoothHint, Toast.LENGTH_SHORT).show();
			}
			return false;
		}
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		int a2dp = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);  
		int headset = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);     
		int health = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEALTH);
		if (a2dp == BluetoothProfile.STATE_CONNECTED ||
				headset == BluetoothProfile.STATE_CONNECTED ||
				health == BluetoothProfile.STATE_CONNECTED) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断是否有U盘或SD卡挂载
	 * @return
	 */
	public static boolean isExternalStorageMounted(Context context) {
		boolean[] bools = new boolean[2];
		StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
		boolean sdCradStatus = StorageUtils.getSDCradStatus(mStorageManager,
				StorageUtils.PLATFORM_AMLOGIC);
		boolean usbStatus = StorageUtils.getUSBStatus(mStorageManager,
				StorageUtils.PLATFORM_AMLOGIC);
		Log.e("TTT", "------MO--sdCradStatus = " + sdCradStatus
				+ ":::usbStatus = " + usbStatus);
		bools[0] = sdCradStatus;
		bools[1] = usbStatus;
		return bools[0] || bools[1];
	}
	
	/**
	 * 用于判断是否有USB设备挂载
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean isUSBMounted(Context context) {
		StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
		if (Build.VERSION.SDK_INT >= 21 ) {
//			List<VolumeInfo> volumes = storageManager.getVolumes();
//	        Collections.sort(volumes, VolumeInfo.getDescriptionComparator());
//	        for (VolumeInfo vol : volumes) {
//	            if (vol != null && vol.isMountedReadable() && vol.getType() == VolumeInfo.TYPE_PUBLIC) {
//	                DiskInfo disk = vol.getDisk();
//	                if (disk.isUsb()) {
//	                    return true;
//	                }
//	            }
//	        }
		} else {
	        File dir = new File(USB_PATH);
	        if (dir.exists() && dir.isDirectory()) {
	            if (dir.listFiles() != null) {
	                List<File> files = Arrays.asList(dir.listFiles());
	                for (File file : files) {
	                    if (file.isDirectory()) {
	                        String path = file.getAbsolutePath();
	                        if (path.startsWith(USB_PATH + "/sd") && !path.equals(SD_PATH)) {
	                            String stateStr = Environment.getStorageState(new File(path));
	                            if (stateStr.equals(Environment.MEDIA_MOUNTED)) {
	                            	return true;
	                            }
	                        }
	                    }
	                }
	            }
	        }

	        dir = new File(USB_PATH);
	        if (dir.exists() && dir.isDirectory()) {
	            if (dir.listFiles() != null) {
	                for (File file : dir.listFiles()) {
	                    if (file.isDirectory()) {
	                        String path = file.getAbsolutePath();
	                        if (path.startsWith(USB_PATH + "/sr") && !path.equals(SD_PATH)) {
	                            String stateStr = Environment.getStorageState(new File(path));
	                            if (stateStr.equals(Environment.MEDIA_MOUNTED)) {
	                            	return true;
	                            }
	                        }
	                    }
	                }
	            }
	        }
	        
	        dir = new File(SATA_PATH);
	        if (dir.exists() && dir.isDirectory()) {
	            String stateStr = Environment.getStorageState(dir);
	            if (stateStr.equals(Environment.MEDIA_MOUNTED)) {
	                return true;
	            }
	        }
		}
		return false;
	}
	
	/**
	 * 用于判断是否有SDCard设备挂载
	 * @return
	 */
	public static boolean isSDCardMounted(Context context) {
		StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
		if (Build.VERSION.SDK_INT >= 21 ) {
//			List<VolumeInfo> volumes = storageManager.getVolumes();
//	        Collections.sort(volumes, VolumeInfo.getDescriptionComparator());
//	        for (VolumeInfo vol : volumes) {
//	            if (vol != null && vol.isMountedReadable() && vol.getType() == VolumeInfo.TYPE_PUBLIC) {
//	                DiskInfo disk = vol.getDisk();
//	                if (!disk.isUsb()) {
//	                    return true;
//	                }
//	            }
//	        }
		} else {
			File dir = new File(SD_PATH);
	        if (dir.exists() && dir.isDirectory()) {
	            @SuppressWarnings("deprecation")
				String stateStr = Environment.getStorageState(dir);
	            if (stateStr.equals(Environment.MEDIA_MOUNTED)) {
	            	return true;
	            }
	        }	
		}
		return false;
	}

    /**
     * 启动移动网络设置
     */
    public static void lunchMobileNetworkSettings(Context context) {
        if (!isHardwareSupportLTE()) {
            Log.d(TAG, "---no support mobile network!---");
            return;
        }
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.setAction("android.settings.WIRELESS_SETTINGS");
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "-------lunch wireless settings failed!----------");
        }
    }
	
	/**
	 * 启动NetworkSettings
	 * @param context
	 */
	public static void lunchNetworkSettings(Context context) {
		if (!isHardwareSupportNetwork(context)) {
			Log.d(TAG, "----No support network!----");
		}
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		try {
			intent.setComponent(new ComponentName(ConstantsUtils.PACKAGE_TV_SETTINGS, ConstantsUtils.ACTIVITY_TV_NETWORKSETTINGS));
			context.startActivity(intent);
		} catch (Exception e) {
			Log.e(TAG, "-------lunch tv network settings failed!----------");
			try {
				intent.setComponent(new ComponentName(ConstantsUtils.PACKAGE_LINKMATE_SETTINGS, ConstantsUtils.ACTIVITY_LINKMATE_NETWORKSETTINGS));
				if (isEthernetConnected(context)) {
					intent.putExtra(ConstantsUtils.TYPE_START_SETTING4, ConstantsUtils.TYPE_SETTING_ETHERNET);
				} else {
					intent.putExtra(ConstantsUtils.TYPE_START_SETTING4, ConstantsUtils.TYPE_SETTING_WIFI);
				}
				context.startActivity(intent);
			} catch (Exception e1) {
				Log.e(TAG, "-------lunch linkmate network settings failed!--------");
				try {
					intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
					if (isEthernetConnected(context)) {
						intent.setAction(ConstantsUtils.ACTION_ETHERNET_SETTINGS);
					} else {
						intent.setAction(ConstantsUtils.ACTION_WIFI_SETTINGS);
					}
					context.startActivity(intent);
				} catch (Exception e2) {
					Log.e(TAG, "--------lunch network settings failed!------------");
					e2.printStackTrace();
				}
			}
		}
	}
	
	/**
     * 启动BackupRestore
     * @param context
     */
    public static void lunchBackupRestore(Context context, String failureTips) {
        if (!isApkInstalled(context, ConstantsUtils.PACKAGE_BACKUP_RESTORE)) {
            if (null != failureTips && !"".equals(failureTips)) {
                Toast.makeText(context, failureTips, Toast.LENGTH_SHORT).show();
            }
            return;
        } else {
            try {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                intent.setComponent(new ComponentName(ConstantsUtils.PACKAGE_BACKUP_RESTORE, ConstantsUtils.ACTIVITY_BACKUP_RESTORE));
                context.startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "---------cant find BackupRestore application!----");
            }
        }
    }

    /**
     * 启动BackupRestore
     * @param context
     */
    public static void lunchWallpaperSettings(Context context, String failureTips) {
        if (!isApkInstalled(context, ConstantsUtils.PACKAGE_SEN5_CORE_SERVICE)) {
            if (null != failureTips && !"".equals(failureTips)) {
                Toast.makeText(context, failureTips, Toast.LENGTH_SHORT).show();
            }
            return;
        } else {
            try {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                intent.setAction("com.sen5.intent.action.SET_WALLPAPER");
                context.startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "---------cant find wallpaper settings!----");
            }
        }
    }

	/**
	 * 在插入外部存储设备的情况下，点击图标跳转到文件管理器 
	 */
	public static void lunchFileBrowser(Context context) {
		Intent intent;
		try {
			intent = context.getPackageManager()
					.getLaunchIntentForPackage(ConstantsUtils.PACKAGE_FILEBROWSER_905);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			context.startActivity(intent);
		} catch (Exception e) {
			Log.e(TAG, "------------lunch 905_FileBrowser failed! Trying to lunch 805_FileBrowser!------");
			try {
				intent = context.getPackageManager()
						.getLaunchIntentForPackage(ConstantsUtils.PACAKGE_FILEBROWSER_805);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
				context.startActivity(intent);
			} catch (Exception e1) {
				Log.e(TAG, "--------lunch 805_FileBrowser Failed!-----------------");
                try {
                    intent = context.getPackageManager()
                            .getLaunchIntentForPackage(ConstantsUtils.PACKAGE_FILEBROWSER_XIAOBAI);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    context.startActivity(intent);
                } catch (Exception e2) {
                    Log.e(TAG, "--------lunch FileBrowser Failed!-----------------");
                    e2.printStackTrace();
                }
			}
		}
	}
	
	/**
	 * 启动SmartBox Settings
	 * @param context
	 */
	public static void lunchSecqreSettings(Context context) {
		try {
			Intent intent = new Intent();
			intent.setPackage("com.sen5.secure.launcher");
			intent.setComponent(new ComponentName(context,ConstantsUtils.PACKAGE_SECQER_SETTINGS));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			context.startActivity(intent);
		} catch (Exception e) {
			Log.e(TAG, "--------lunch SecqreSettings Failed!-----------------");
			e.printStackTrace();
		}
	}
	
	/**
	 * 启动通知中心
	 * @param context
	 */
	public static void lunchNotificationCenter(Context context) {
		
	}
	
	/**
	 * 启动网速检测工具
	 * @param context
	 */
	public static void lunchSpeedTest(Context context) {
		try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(
                    ConstantsUtils.PACKAGE_SPEEDTEST);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "--------lunch SpeedTest Failed!-----------------");
            e.printStackTrace();
        }
	}
	
	/**
	 * 启动下载中心
	 * @param context
	 */
	public static void lunchDownloadCenter(Context context) {
		try {
			Intent intent = new Intent();
			intent.setComponent(new ComponentName(ConstantsUtils.PACKAGE_DOWNLOAD,
					ConstantsUtils.ACTIVITY_DOWNLOAD));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			context.startActivity(intent);
		} catch (Exception e) {
			Log.e(TAG, "--------lunch DownloadCenter Failed!-----------------");
			try {
				Intent intent = new Intent();
				intent.setComponent(new ComponentName(ConstantsUtils.PACKAGE_DOWNLOAD_ANDROID_N, 
						ConstantsUtils.ACTIVITY_DOWNLOAD_ANDORID_N));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
				context.startActivity(intent);
			} catch (Exception e1) {
				// TODO: handle exception
				Log.e(TAG, "------lunch DownloadCenter(android_n) failed!----");
			}
		}
	}
	
	/**
	 * 启动google应用商店
	 * @param context
	 */
	public static void lunchGooglePlay(Context context) {
		try {
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			intent.setClassName(ConstantsUtils.PACKAGE_GOOGLE_PALY_STROE, 
					ConstantsUtils.ACTIVITY_GOOGLE_PLAY_STROE);
			context.startActivity(intent);
		} catch (Exception e) {
			Log.e(TAG, "------------lunch googleplay store by class name failed!--------");
			try {
				Intent intent = context.getPackageManager()
						.getLaunchIntentForPackage(ConstantsUtils.PACKAGE_GOOGLE_PALY_STROE);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
				context.startActivity(intent);
			} catch (Exception e1) {
				Log.e(TAG, "-------------no google service!---------");
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * 重启盒子
	 * @param context
	 */
	public static void rebootSystem(Context context) {
		try {
			Intent intent = new Intent(Intent.ACTION_REBOOT);
			intent.putExtra("nowait", 1);
	        intent.putExtra("interval", 1);
	        intent.putExtra("window", 0);
	        context.sendBroadcast(intent); 
		} catch (Exception e) {
			Log.e(TAG, "---------reboot failed! (ACTION_REBOOT)--------");
			try {
				Intent intent = new Intent(ConstantsUtils.ACTION_REBOOT_SEN5);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
				Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				context.sendBroadcast(intent);
			} catch (Exception e1) {
				 Log.e(TAG, "-----------reboot failed! (SEN5_REBOOT)");
				 e1.printStackTrace();
			}
		}
	}
	
	/**
	 * 盒子待机
	 */
	public static void standbySystem() {
		try {
			translationvirtualKey(KeyEvent.KEYCODE_POWER);
		} catch (Exception e) {
			Log.e(TAG, "----------standby failed!-----------");
			e.printStackTrace();
		}
	}
	
	/**
	 * 发送虚拟按键事件
	 * @param keyCode
	 */
	public static void translationvirtualKey(int keyCode) {
		long mDownTime = SystemClock.uptimeMillis();
		KeyEvent evDown = new KeyEvent(mDownTime, mDownTime,
				KeyEvent.ACTION_DOWN, keyCode, 0, 0,
				KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0
						| KeyEvent.FLAG_FROM_SYSTEM
						| KeyEvent.FLAG_VIRTUAL_HARD_KEY,
				InputDevice.SOURCE_KEYBOARD);
		InputManager.getInstance().injectInputEvent(evDown,
				InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);

		long mUpTime = SystemClock.uptimeMillis();
		KeyEvent evUp = new KeyEvent(mUpTime, mUpTime, KeyEvent.ACTION_UP,
				keyCode, 0, 0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0
						| KeyEvent.FLAG_FROM_SYSTEM
						| KeyEvent.FLAG_VIRTUAL_HARD_KEY,
				InputDevice.SOURCE_KEYBOARD);
		InputManager.getInstance().injectInputEvent(evUp,
				InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
	}
	
	/**
	 * 用于判断硬件是否支持网络连接
	 * @param context
	 * @return
	 */
	public static boolean isHardwareSupportNetwork(Context context){
		if(null == mSen5ServiceManager){
			mSen5ServiceManager = (Sen5ServiceManager) context.getSystemService(ConstantsUtils.SEN5_SERVICE);
		}
		if (null != mSen5ServiceManager) {
			if(!mSen5ServiceManager.hasEthernet() && !mSen5ServiceManager.hasWifi()) {
				return false;
			}else{
				return true;
			}
		}
		return true;
	}

	/**
	 * 用于判断硬件是否支持以太网
	 * @param context
	 * @return
	 */
	public static boolean isHardwareSupportEthernet(Context context){
		if(null == mSen5ServiceManager){
			mSen5ServiceManager = (Sen5ServiceManager) context.getSystemService(ConstantsUtils.SEN5_SERVICE);
		}
		if (null != mSen5ServiceManager) {
			if(mSen5ServiceManager.hasEthernet()) {
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	
	/**
	 * 用于判断硬件是否支持WIFI
	 * @param context
	 * @return
	 */
	public static boolean isHardwareSupportWifi(Context context){
		if(null == mSen5ServiceManager){
			mSen5ServiceManager = (Sen5ServiceManager) context.getSystemService(ConstantsUtils.SEN5_SERVICE);
		}
		if(null != mSen5ServiceManager){
			if(mSen5ServiceManager.hasWifi()) {
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	
	/**
	 * 用于判断硬件是否支持蓝牙
	 * @param context
	 * @return
	 */
	public static boolean isHardwareSupportBluetooth(Context context) {
		if(null == mSen5ServiceManager){
			mSen5ServiceManager = (Sen5ServiceManager) context.getSystemService(ConstantsUtils.SEN5_SERVICE);
		}
		if(null != mSen5ServiceManager){
			if(mSen5ServiceManager.hasBluetooth()) {
				return true;
			}else{
				return false;
			}
		}
		return false;
	}

    /**
     * 用于判断硬件是否支持4G模块
     * @return
     */
    public static boolean isHardwareSupportLTE(){
        String type = SystemProperties.get(ConstantsUtils.RO_SEN5_HAS_MODEMTYPE, "");
        if (null != type && !"".equals(type)) {
            return true;
        }
        return false;
    }

	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		} else {
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 用于判断WIFI是否连接
	 * @param context
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean isWifiConnected(Context context) {
		ConnectivityManager cManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = cManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (null != mWifi && mWifi.isConnected()) {
			return true;
		}
		return false;
	}

    /**
     * 用于判断移动网络是否连接，暂时不可用
     * @param context
     * @return
     */
	private static boolean isLteConnected(Context context) {
        return false;
    }

    /**
     * 获取wifi信号强度
     * @param context
     * @return
     */
	public static int getWifiRssi(Context context) {
		int level;
		WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int nRssi = wifiInfo.getRssi();
		if (nRssi == Integer.MAX_VALUE) {
			level = 5;
		} else {
			level = WifiManager.calculateSignalLevel(nRssi, 4);
		}
		return level;
	}
	
	/**
	 * 用于判断以太网是否连接
	 * @param context
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean isEthernetConnected(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = connectivity
					.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);

			return info.isConnected();
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	/**
	 * 删除应用的数据
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean clearApplicationData(Context context, String packageName) {
		ActivityManager am = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        boolean res = am.clearApplicationUserData(
        		packageName, new IPackageDataObserver.Stub() {
                    public void onRemoveCompleted(
                            final String mPackageName, final boolean succeeded) {
                        if (succeeded) {
                            Log.d(TAG, "--------clear application data succeeded!------");
                        } else {
                        	Log.d(TAG, "--------clear application data failed!------");
                        }
                    }
                });
        if (res) {
            return true;
        } 
        return false;
	}

    /**
     *
     * @param path
     * @return
     */
    public static LauncherToolsConfig readLauncherToolsConfig(String path) {
        if (null == path || "".equals(path)) {
            return null;
        }
        if (!path.endsWith(ConstantsUtils.LUANCHERTOOLS_FILE_NAME)) {
            if (!path.endsWith("/")) {
                path = path + "/";
            }
            path = path + ConstantsUtils.LUANCHERTOOLS_FILE_NAME;
        }
        Log.d(TAG, "Launcher config path = " + path);
        try {
            String xml = FileUtils.readFileToString(new File(path));
            Log.d(TAG, "Launcher config xml = " + xml);
            LauncherToolsConfig config = XmlUtils.xmlToBean(xml, LauncherToolsConfig.class);
            Log.d(TAG, "Launcher config = " + config);
            return config;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setMobileNetworkListener(Context context, final OnPhoneStateChangeListener listener) {
        try {
            if (null == mTelephonyManager) {
                mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            }

            mPhoneStateListener = new PhoneStateListener() {

                @Override
                public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                    super.onSignalStrengthsChanged(signalStrength);
                    listener.OnPhoneStateChange(TelephonyManager.getNetworkClass(mTelephonyManager.getNetworkType()),
                            getSignalStrengthLevel(signalStrength));
                }
            };

            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "----------setMobileNetworkListener error!-------");
        }
    }

    public static void registerPhoneStateListener () {
        if (null != mTelephonyManager && null != mPhoneStateListener && isHardwareSupportLTE()) {
            Log.d(TAG, "------------register phonestate listener---------");
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }
    }

    public static void unregisterPhoneStateListener () {
        if (null != mTelephonyManager && null != mPhoneStateListener && isHardwareSupportLTE()) {
            Log.d(TAG, "------------unregister phonestate listener---------");
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    /**
     * dBm和asu它们之间的关系是：dBm =-113+2*asu，这是google给android手机定义的特有信号单位。
     * 简单的说dBm值肯定是负数的，越接近0信号就越好，但是不可能为0的 ASU的值则相反，是正数，也是值越大越好
     * 按规定，只要城市里大于-90，农村里大于-94就是正常的，记住负数是-号后面的值越小就越大
     * 具体情况就是：-81dBm的信号比-90dBm的强，-67dBm的信号比-71dBm的强 低于-113那就是没信号了
     * @param signalStrength
     * @return
     */
    private static int getSignalStrengthLevel(SignalStrength signalStrength) {
        int phoneType = mTelephonyManager.getPhoneType();
        int dbm = signalStrength.getDbm();
        int asuLevel = signalStrength.getAsuLevel();
        int level = signalStrength.getLevel();
        switch (phoneType) {
            case TelephonyManager.PHONE_TYPE_NONE:
                Log.d(TAG, "-----------getSignalStrengthLevel:PHONE_TYPE_NONE level = " + level);
                break;
            case TelephonyManager.PHONE_TYPE_GSM:
                dbm = signalStrength.getGsmDbm();
                asuLevel = signalStrength.getGsmAsuLevel();
                level = signalStrength.getGsmLevel();
                Log.d(TAG, "-----------getSignalStrengthLevel:PHONE_TYPE_GSM level = " + level +
                        "\n dbm = " + dbm + "\n asuLevel = " + asuLevel);
                break;
            case TelephonyManager.PHONE_TYPE_CDMA:
                dbm = signalStrength.getCdmaDbm();
                asuLevel = signalStrength.getCdmaAsuLevel();
                level = signalStrength.getCdmaLevel();
                Log.d(TAG, "-----------getSignalStrengthLevel:PHONE_TYPE_CDMA level = " + level +
                        "\n dbm = " + dbm + "\n asuLevel = " + asuLevel);
                break;
            case TelephonyManager.PHONE_TYPE_SIP:
                Log.d(TAG, "-----------getSignalStrengthLevel:PHONE_TYPE_SIP level = " + level +
                        "\n dbm = " + dbm + "\n asuLevel = " + asuLevel);
                break;
            default:
                Log.d(TAG, "-----------getSignalStrengthLevel:default level = " + level +
                        "\n dbm = " + dbm + "\n asuLevel = " + asuLevel);
                break;
        }
        return level;
    }

    private static String getNetworkClass(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case 16://TelephonyManager.NETWORK_TYPE_GSM:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return ConstantsUtils.NETWORK_TYPE_2G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case 17://TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return ConstantsUtils.NETWORK_TYPE_3G;
            case TelephonyManager.NETWORK_TYPE_LTE:
            case 18://TelephonyManager.NETWORK_TYPE_IWLAN:
                return ConstantsUtils.NETWORK_TYPE_4G;
            default:
                return ConstantsUtils.NETWORK_TYPE_UNKNOW;
        }
    }

    public interface OnPhoneStateChangeListener {
        void OnPhoneStateChange(int networkClass, int signalStrength);
    }

    private static final String SD_PATH = "/storage/external_storage/sdcard1";
    private static final String USB_PATH ="/storage/external_storage";
    private static final String SATA_PATH ="/storage/external_storage/sata";
	private static final String TAG = GeneralUtils.class.getSimpleName();
}
