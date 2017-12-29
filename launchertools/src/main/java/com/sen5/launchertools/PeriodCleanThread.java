package com.sen5.launchertools;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

/**
 * @author JesseYao 
 * @version 2016年9月14日上午10:18:21
 * ClassName: TimeCleanThread.java 
 * Description: Thread for auto clean-up memory periodically
 */
public class PeriodCleanThread extends Thread {
	
	/**
	 * default period: 10 minutes
	 */
	private int mTimePeriod = 600000;
	private Handler mHandler = null;
	private boolean mAutoCleanAfterPeriod = true;
	
	/**
	 * 
	 * @param timePeriod unit: s
	 */
	public PeriodCleanThread(int timePeriod, Handler handler){
		if (timePeriod > 60) { //clean-up period can not be less than 60s
			mTimePeriod = timePeriod * 1000;
		} else if (timePeriod < 0) {
			mAutoCleanAfterPeriod = false;//shutdown auto clean-up if is a negative
		}
		mHandler = handler;
	}
	
	/**
	 * 
	 * @param mTimePeriod unit: s
	 */
	public void setTimePeriod(int timePeriod) {
		if(timePeriod > 60){ //clean-up period can not be less than 60s
			mTimePeriod = timePeriod * 1000;
		} else if (timePeriod < 0) {
			mAutoCleanAfterPeriod = false;//shutdown auto clean-up if is a negative
		}
	}

	@Override
	public void run() {
		while (mAutoCleanAfterPeriod) {
			Log.d(TAG, "----------time is up, start auto clean-up!---------------");
			SystemClock.sleep(mTimePeriod);
			if(null != mHandler){
				mHandler.sendEmptyMessageDelayed(CleanerService.MSG_CLEAN_BACKGROUND, 100);
			}
		}
	}
	
	private static final String TAG = PeriodCleanThread.class.getSimpleName();
}
