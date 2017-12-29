package com.ipcamera.main.cuswidget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ImageView;

public class MyImageView extends ImageView{
	private boolean mInterceptKeyDown = true;
	private boolean mInterceptKeyUp = false;
	private boolean mInterceptKeyLeft = false;
	private boolean mInterceptKeyRight = false;
	
	private int mNextDownId; 
	private int mNextUpId; 
	private int mNextLeftId; 
	private int mNextRightId;
	
	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void setInterceptKeyDown(boolean interceptKeyDown){
		this.mInterceptKeyDown = interceptKeyDown;
	}
	public void setInterceptKeyUp(boolean interceptKeyUp){
		this.mInterceptKeyUp = interceptKeyUp;
	}
	public void setInterceptKeyLeft(boolean interceptKeyLeft){
		this.mInterceptKeyLeft = interceptKeyLeft;
	}
	public void setInterceptKeyRight(boolean interceptKeyRight){
		this.mInterceptKeyRight = interceptKeyRight;
	}
	
	public void setInterceptKey(boolean down, boolean up, boolean left, boolean right){
		this.mInterceptKeyDown = down;
		this.mInterceptKeyUp = up;
		this.mInterceptKeyLeft = left;
		this.mInterceptKeyRight = right;
	}
	
//	public static int 
	
	public void setNextFocusDownIdMy(int id){
		this.mNextDownId = id;
	}
	
	
	public void setNextFocusUpIdMy(int id){
		this.mNextUpId = id;
	}
	
	public void setNextFocusLeftIdMy(int id){
		this.mNextLeftId = id;
	}
	
	public void setNextFocusRightIdMy(int id){
		this.mNextRightId = id;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if(mInterceptKeyDown){
				return true;
			}else{
				if(mNextDownId != 0){
					
					this.setNextFocusDownId(mNextDownId);
				}
			}
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			if(mInterceptKeyUp){
				return true;
			}else{
				if(mNextUpId != 0){
					
					this.setNextFocusUpId(mNextUpId);
				}
			}
			
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if(mInterceptKeyLeft){
				return true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if(mInterceptKeyRight){
				return true;
			}
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

}
