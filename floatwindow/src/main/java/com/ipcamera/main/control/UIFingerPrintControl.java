package com.ipcamera.main.control;

import java.util.List;

import hsl.p2pipcam.camera.MyRender.RenderListener;
import android.R.color;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ipcamera.main.R;
import com.ipcamera.main.smarthome.CameraControlNew;
import com.ipcamera.main.utils.UtilsFingerPrint;
import com.ipcamera.main.view.UIFingerPrint;

public class UIFingerPrintControl {
	
	private Context mContext;
	private RelativeLayout mView;

	
	private UIFingerPrint mUIFingerPrint;
	private Drawable mDefaultDrawable;
	
	public UIFingerPrintControl(Context context, UIFingerPrint uiFingerPrint){
		mContext = context;
		mUIFingerPrint = uiFingerPrint;
		mView = uiFingerPrint.getView();

		mDefaultDrawable = mContext.getResources().getDrawable(R.drawable.bg_camera_default);
		
	
		
//		layoutParams.flags |= 
//				LayoutParams.FLAG_NOT_TOUCH_MODAL|
//				LayoutParams.FLAG_NOT_FOCUSABLE|
//				LayoutParams.FLAG_LAYOUT_NO_LIMITS;
//		setSplitScreen(mContext, SPLIT_SCREEN_FOUR, false);
	}
	
	public View getView(){
		return mView;
	}
	
	public void setViewDefaultDrawable(Drawable drawable){
		mDefaultDrawable = drawable;
	}
	
	public void setGlsListener(RenderListener renderListener){
		CameraControlNew.initGlsListener(mUIFingerPrint.getGLView(), renderListener);
	}
	
	public void addStatusView(List<TextView> list){
		list.add(mUIFingerPrint.getStatusView());
	}
	
	public void setVideoDefaultBg(Drawable drawable){
		if(CameraControlNew.CAMERA_USE_DEFAULT_BITMAP){
//			glSurfaceView.setBackground(mDefaultDrawable);
			mUIFingerPrint.getGLView().setBackgroundColor(color.transparent);
			mUIFingerPrint.getLoadUI().setBackground(mDefaultDrawable);
		}else{
//			glSurfaceView.setBackground(drawable);
			mUIFingerPrint.getGLView().setBackgroundColor(color.transparent);
			mUIFingerPrint.getLoadUI().setBackground(drawable);
		}
	}
	
	/**
	 * 该方法中的mPosition 为当前play 的Camera
	 * @methodName refreshGLSBackground
	 * @function 
	 * @param isOK
	 */
	public void refreshGLSBackground(boolean isOK){
		if(isOK){
			mUIFingerPrint.getGLView().setBackgroundColor(color.transparent);
		}else{
			Drawable playFailDefaultDrawable = UtilsFingerPrint.getPlayFailDefaultDrawable(mContext, "", mDefaultDrawable);
			if(CameraControlNew.CAMERA_USE_DEFAULT_BITMAP){
				mUIFingerPrint.getGLView().setBackground(mDefaultDrawable);
			}else{
				mUIFingerPrint.getGLView().setBackground(playFailDefaultDrawable);
			}
			mUIFingerPrint.getGLView().setBackgroundColor(color.transparent);
		}
	}
	
	public void setLoadSuccessEnd(){
		mUIFingerPrint.getGLView().setBackgroundColor(color.transparent);
		mUIFingerPrint.getLoadUI().setVisibility(View.GONE);
	}
	
	public void hideMainView(){
		mUIFingerPrint.getMenuView().hideView();
	}
	
	public void showMainView(boolean startAnim){
		mUIFingerPrint.getMenuView().showView(startAnim);
	}
	
	public void setCameraTimeOutUI(){
		mUIFingerPrint.getLoadUI().setVisibility(View.GONE);
		showMainView(false);
	}
	
	public void setOnKeyListenerByMenuItem(OnKeyListener onKeyListener){
		mUIFingerPrint.getMenuView().setOnKeyListenerByMenuItem(onKeyListener);
	}
	
	public void setOnClickListenerByMenuItem(OnClickListener onClickListener){
		mUIFingerPrint.getMenuView().setOnClickListenerByMenuItem(onClickListener);
		
	}
	
}
