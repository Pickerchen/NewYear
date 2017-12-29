package com.sen5.launchertools;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

/**
 * @author JesseYao 
 * @version 2016年9月14日上午10:16:26
 * ClassName: LowMemoryCleanThread.java 
 * Description: Clean-up Memory when system in low memory status
 */
public class LowMemoryCleanThread extends Thread {

	/**
	 * is in low memory status
	 */
	private boolean mIsLowMemoryClean = true;
	
	private Handler mHandler = null;
	
	private Context mContext = null;
	
	
	public LowMemoryCleanThread(Context context, boolean isLowMemoryClean, Handler handler){
		mContext = context;
		mHandler = handler;
		mIsLowMemoryClean = isLowMemoryClean;
	}
	
	public void setIsLowMemoryClean(boolean isLowMemoryClean) {
		mIsLowMemoryClean = isLowMemoryClean;
	}
	
	@Override
	public void run() {
		while (mIsLowMemoryClean) {
			SystemClock.sleep(500);//check status every 500ms
			if (null != mContext && null != mHandler && GeneralUtils.isLowMemory(mContext)) {
				Log.d(TAG, "-------------low memory, start clean-up memory-----------");
				mHandler.sendEmptyMessageDelayed(CleanerService.MSG_CLEAN_BACKGROUND, 100);
			}
		}
	}
	
	private static final String TAG = LowMemoryCleanThread.class.getSimpleName();
}
