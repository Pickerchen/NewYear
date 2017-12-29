package com.ipcamera.main.control;

import android.app.Service;
import android.content.Context;
import android.os.SystemProperties;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.ipcamera.main.service.FingerPrintViewService;
import com.ipcamera.main.utils.DLog;
import com.ipcamera.main.utils.Utils;

public class UIFloatViewControl {
	private static final String TAG = UIFingerPrintControl.class.getSimpleName();
	/**
	 * 满屏
	 */
	private static final int SPLIT_SCREEN_FULL = 1;
	
	/**
	 * 4分屏
	 */
	private static final int SPLIT_SCREEN_FOUR = 4;
	/**
	 * 8分屏
	 */
	private static final int SPLIT_SCREEN_nine = 8;
	
	/**
	 * 16分屏
	 */
	private static final int SPLIT_SCREEN_SIXTEEN = 16;
	private WindowManager windowManager;
	private LayoutParams layoutParams;
	private boolean mShowFingerView;
	private Context mContext;
	private View mView;
	
	public UIFloatViewControl(Context context){
		mContext = context;
		windowManager = (WindowManager) mContext
				.getSystemService(Service.WINDOW_SERVICE);
		layoutParams = new WindowManager.LayoutParams();
		layoutParams.type = LayoutParams.TYPE_SYSTEM_ALERT
				| LayoutParams.TYPE_SYSTEM_OVERLAY;
	}
	
	/**
	 * 设置需要显示的UI view
	 * @methodName setView
	 * @function 
	 * @param view
	 */
	public void setView(View view){
		mView = view;
	}
	
	public void setViewMeasure(int width, int height){
		layoutParams.width = width;
		layoutParams.height = height;
	}
	

	/**
	 * 显示FingerPrint View
	 * @修改
	 * @param location 位置
	 * @param offsetX  X偏移
	 * @param offsetY  Y偏移
	 * @param alpha    透明
	 */
	public void showView(int location, int offsetX, int offsetY, float alpha) {
		DLog.e(TAG + "------------setViewLocation = " + location);
		layoutParams.gravity = Utils.getLocation(location);
		layoutParams.x = offsetX;
		layoutParams.y = offsetY;
//		layoutParams.width = 
//		layoutParams.height = 
		layoutParams.alpha = 1f;
		// layoutParams.horizontalMargin = 10;
		// layoutParams.verticalMargin = 5;
		refresh(0);
//		setSplitScreen(mContext, SPLIT_SCREEN_FOUR);
	}
	
	public void setSplitScreen(Context context, int splitScreen, boolean refreshUI){
		int[] screenWH = Utils.getScreenWH(context);
		screenWH[0] = screenWH[0]/(int)Math.sqrt(splitScreen);
		screenWH[1] = screenWH[1]/(int)Math.sqrt(splitScreen);
		setViewMeasure(screenWH[0], screenWH[1]);
		if(refreshUI){
			refresh(1);
		}
	}
	
	public void setSplitScreen(Context context, float widthScreen, boolean refreshUI){
		int[] screenWH = Utils.getScreenWH(context);
		screenWH[0] = (int)(screenWH[0]/widthScreen);
		screenWH[1] = (int)(screenWH[1]/widthScreen);
		setViewMeasure(screenWH[0], screenWH[1]);
		if(refreshUI){
			refresh(2);
		}
	}
	
	/**
	 * 
	 * 刷新小窗体的位置
	 */
	private void refresh(int tag) {
		DLog.e(TAG, "-------------POP:refresh" + mShowFingerView + "  tag = " + tag);
		// view.setFocusable(true);
		if (mShowFingerView) {
			
			windowManager.updateViewLayout(mView, layoutParams);
		} else {
			windowManager.addView(mView, layoutParams);
			mShowFingerView = true;
		}
		SystemProperties.set(FingerPrintViewService.CAMERA_FLOAT_PLAY_FLAG, "true");
		DLog.e(TAG, "--------------POP:refresh FINISH");
	}
	
	public void removeView() {
		DLog.i(TAG, "------------------removeView mShowFingerView = " + mShowFingerView);
		SystemProperties.set(FingerPrintViewService.CAMERA_FLOAT_PLAY_FLAG, "false");
		if (mShowFingerView) {
			windowManager.removeView(mView);
			mShowFingerView = false;
		}
	}
	
	public void clearFocus(){
		layoutParams.flags |= LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE
				| LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		refresh(3);
	}
	
	public void requestFocus(){
		layoutParams.flags = LayoutParams.FLAG_ALT_FOCUSABLE_IM;
		refresh(4);
	}
}
