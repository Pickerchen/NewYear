package com.sen5.launchertools;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author JesseYao
 * @version 2016-5-3 5:55:02pm 
 * ClassName：CleanerService.java 
 * Description： 
 */
public class CleanerService extends Service implements Callback {

	private Handler mHandler = null;
	private Context mContext = null;
	private Thread mLMCThread, mPCThread;
	
	private int mTimePeriod = 0;
	private boolean mIsLowClean = true;
	
	private List<String> mAllowBackgroundList;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "----------CleanerService.onCreate----------");
		mContext = this;
		mHandler = new Handler(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "-----------CleanerService.onStartCommand-----------------");
		// register brocastReceiver for start or stop cleanerService
		IntentFilter intentFilter = new IntentFilter(ACTION_CLEAN_BACKGROUND);
		intentFilter.addAction(ACTION_START_CLEANER);
		intentFilter.addAction(ACTION_STOP_CLEANER);
		this.registerReceiver(receiver, intentFilter);
		// get clean-up period from system property
		mTimePeriod = (int) SystemProperties.getLong(ConstantsUtils.PERSIST_SYS_CLRMEM_INTERVAL_SEC, 0);
		mAllowBackgroundList = GeneralUtils.getAllowBackgroundAppList();
		
		// create a thread to clean-up memory periodically
		mPCThread = new PeriodCleanThread(mTimePeriod, mHandler);
		mPCThread.start();

		
		// create a thread to clean-up memory when system in low memory status
		mLMCThread = new LowMemoryCleanThread(mContext, mIsLowClean, mHandler);
		mLMCThread.start();
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "-------------CleanerService.onDestroy------------");
		//unregister receiver
		this.unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_CLEAN_BACKGROUND:
			clean(true);
			break;
		case MSG_STOP_SERVICE:
			Log.d(TAG, "-----stop Service------");
			stopSelf();
			break;
		case MSG_GET_CURRENT_PKG:
			//Log.d(TAG, "yjx.current pkg = " + getCurrentPkgName(mContext));
			break;

		default:
			break;
		}
		return false;
	}

	private synchronized void clean(boolean initiative) {
		Log.d(TAG, "------------Start clean-up!-------------");
		//check period is reset or not before clean-up 
		mTimePeriod = (int) SystemProperties.getLong(ConstantsUtils.PERSIST_SYS_CLRMEM_INTERVAL_SEC, 0);
		((PeriodCleanThread)mPCThread).setTimePeriod(mTimePeriod);
		
		ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infoList = am.getRunningAppProcesses();
		List<RunningServiceInfo> serviceList = am.getRunningServices(100);
		Log.d(TAG, "ListSize  = " + infoList.size() + ", serviceList = " + serviceList.size());
		long beforeMem = getAvailMemory(this);
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

		long afterMem = getAvailMemory(this);
		Log.d(TAG, "----------- after memory info : " + afterMem);
		Log.d(TAG, TAG + ".clear " + count + " process, " + (afterMem - beforeMem) + "M");
		if (initiative) {
			Toast.makeText(this, "clear " + count + " process, " + (afterMem - beforeMem) + "M", Toast.LENGTH_LONG).show();
		}
		Log.d(TAG, "-------------Clean-up over!-----------------");
	}

	/**
	 * get current available memory
	 * @param context
	 * @return
	 */
	private long getAvailMemory(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// mi.availMem;
		// return Formatter.formatFileSize(context, mi.availMem);
		Log.d(TAG, "availMemory---->>>" + mi.availMem / (1024 * 1024));
		return mi.availMem / (1024 * 1024);
	}
	
	/**
	 * check application is allow to running in background or not 
	 * @param packageName
	 * @return
	 */
	private boolean isAllowBackground(String packageName){
		if (null == mAllowBackgroundList || mAllowBackgroundList.size() == 0) {
			return false;
		} else if (mAllowBackgroundList.indexOf(packageName) != -1) {
			return true;
		} 
		return false;
	}
	

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(ACTION_CLEAN_BACKGROUND)){
				mHandler.sendEmptyMessage(MSG_CLEAN_BACKGROUND);
			}else if(intent.getAction().equals(ACTION_STOP_CLEANER)){
				mHandler.sendEmptyMessage(MSG_STOP_SERVICE);
			}
		}
	};
	
	/**
	 * get current running and visible application
	 * @param context
	 * @return
	 */
	public static String getCurrentPkgName(Context context) {
		RunningAppProcessInfo currentInfo = null;
		Field field = null;
		int START_TASK_TO_FRONT = 2;
		String pkgName = null;
		try {
			field = RunningAppProcessInfo.class.getDeclaredField("processState");
		} catch (Exception e) {
			e.printStackTrace();
		}
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appList = am.getRunningAppProcesses();
		for (RunningAppProcessInfo app : appList) {
			if (app.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				Integer state = null;
				try {
					state = field.getInt(app);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (state != null && state == START_TASK_TO_FRONT) {
					currentInfo = app;
					break;
				}
			}
		}
		if (currentInfo != null) {
			pkgName = currentInfo.processName;
		}
		return pkgName;
	}

	public static final String ACTION_CLEAN_BACKGROUND = "com.sen5.action.CLEAN";
	public static final String ACTION_START_CLEANER =  "com.sen5.action.STARTCLEANER";
	public static final String ACTION_STOP_CLEANER = "com.sen5.action.STOPCLEANER";

	public static final int MSG_CLEAN_BACKGROUND = 0x10;
	public static final int MSG_STOP_SERVICE = 0x11;
	private static final int MSG_GET_CURRENT_PKG = 0x12;
	private static final String TAG = CleanerService.class.getSimpleName();
}
