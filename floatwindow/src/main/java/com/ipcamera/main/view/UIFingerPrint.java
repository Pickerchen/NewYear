package com.ipcamera.main.view;

import com.ipcamera.main.R;

import android.app.Service;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UIFingerPrint {
	private Context mContext;
	private RelativeLayout mView;
	private MenuFrameLayout fl_menu;
	private GLSurfaceView glSurfaceView;
	private View loadUI;
	private TextView tv_camera_status;
	public UIFingerPrint(Context context){
		mContext = context;
		LayoutInflater mLayoutInflater = (LayoutInflater) mContext
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		mView = (RelativeLayout) mLayoutInflater.inflate(
				R.layout.float_layout_finger_print, null);
		
		glSurfaceView = (GLSurfaceView)mView.findViewById(R.id.glsurfaceview);
		
		tv_camera_status = (TextView)mView.findViewById(R.id.tv_camera_status);
		loadUI = (View)mView.findViewById(R.id.load_ui);
		
		fl_menu = (MenuFrameLayout)mView.findViewById(R.id.fl_menu);
		
	}
	
	public RelativeLayout getView(){
		return mView;
	}
	
	public View getLoadUI(){
		return loadUI;
	}
	
	public GLSurfaceView getGLView(){
		return glSurfaceView;
	}
	
	public MenuFrameLayout getMenuView(){
		return fl_menu;
	}
	
	public TextView getStatusView(){
		return tv_camera_status;
	}
	
	public void setOnKeyListenerByMenuItem(OnKeyListener onKeyListener){
		fl_menu.setOnKeyListenerByMenuItem(onKeyListener);
	}
	
	public void setOnClickListenerByMenuItem(OnClickListener onClickListener){
		fl_menu.setOnClickListenerByMenuItem(onClickListener);
		
	}
}
