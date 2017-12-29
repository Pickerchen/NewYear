package com.ipcamera.main.cuswidget;

import com.ipcamera.main.utils.DLog;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

/**
 * @author jiangyicheng
 *自定义ListView 上下左右键响应状态，用来满足电视上android UI的焦点移动需
 */
public class Sen5ListView extends ListView {
	private boolean mBoolDown = false;
	private boolean mBoolUp = false;
	private boolean mBoolLeft;
	private boolean mBoolRight;
	private static final String TAG = Sen5ListView.class.getSimpleName();

	public Sen5ListView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		setOnKeyListener(new View.OnKeyListener() {
//
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//				//				Log.e("AAA", "-------------------"+getSelectedItemPosition());
//				if (event.getAction() == KeyEvent.ACTION_DOWN
//						&& keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
//					setListViewPageDown(Sen5ListView.this);
//					return true;
//				} else if (event.getAction() == KeyEvent.ACTION_DOWN
//						&& keyCode == KeyEvent.KEYCODE_PAGE_UP) {
//					setListViewPageUp(Sen5ListView.this);
//					return true;
//				}
//				return false;
//			
//			}
//		});
	}
	
	/**
	 * 设置ListView 向上 翻页 
	 * @param listView
	 */
	public static void setListViewPageUp(ListView listView){
		int lastVisiblePosition = listView.getLastVisiblePosition();
		int firstVisiblePosition = listView.getFirstVisiblePosition();
		int selectedItemPosition = listView.getSelectedItemPosition();
		int showItems = (lastVisiblePosition - firstVisiblePosition);
		Log.d(TAG, "lastVisiblePosition == "+lastVisiblePosition
				+"::firstVisiblePosition == "+firstVisiblePosition
				+"::selectedItemPosition == "+selectedItemPosition
				+"::showItems == "+showItems);
		if (selectedItemPosition + 1 == lastVisiblePosition) {
			showItems += 1;
		}
		// if(getLastVisiblePosition()+1 == getCount() &&
		// getSelectedItemPosition() > getFirstVisiblePosition()){
		// showItems += 2;
		// }
		if ((selectedItemPosition + 1 == lastVisiblePosition 
				&& selectedItemPosition + 1 == showItems)
				|| lastVisiblePosition > showItems
				&& lastVisiblePosition <= (showItems * 2 - 2)) {
			listView.setSelectionFromTop(0, 3);

		} else if (lastVisiblePosition <= showItems) {

			listView.setSelectionFromTop(listView.getCount() - 1, 3);
		} else {

			listView.setSelectionFromTop(firstVisiblePosition
					- showItems + 1, 3);
		}
	}
	
	/**
	 * 设置ListView 向上 翻页 
	 * @param listView
	 */
	public static void setListViewPageDown(ListView listView){
		int lastVisiblePosition = listView.getLastVisiblePosition();
		int firstVisiblePosition = listView.getFirstVisiblePosition();
		int selectedItemPosition = listView.getSelectedItemPosition();
		int showItems = (lastVisiblePosition - firstVisiblePosition);
		Log.d(TAG, "lastVisiblePosition == "+lastVisiblePosition
				+"::firstVisiblePosition == "+firstVisiblePosition
				+"::selectedItemPosition == "+selectedItemPosition
				+"::showItems == "+showItems);
		if (selectedItemPosition + 1 == lastVisiblePosition) {
			showItems -= 1;
		}
		if (firstVisiblePosition + 1 + showItems >= listView.getCount()) {

			// setSelectionFromTop(getCount() - 1, 3);
			listView.setSelectionFromTop(0, 3);

		}
		// else if(getSelectedItemPosition() == (getCount()-1)){
		//
		// setSelectionFromTop(0, 3);
		//
		// }
		else {
			listView.setSelectionFromTop(firstVisiblePosition
					+ showItems + 1, 3);

		}
	}
	/**
	 * 设置向下循环
	 * @param bool true 表示向下循环 ，false 表示关闭循环
	 */
	public void setFocusDownRecycle(boolean bool) {
		mBoolDown = bool;
	}

	/**
	 * 获取向下循环�?
	 * @return 返回true 表示循环 ，false 表示关闭循环
	 */
	public boolean getFocusDownRecycle() {
		return mBoolDown;
	}

	/**
	 * 设置向上循环
	 * @param bool true 表示循环 ，false 表示关闭循环
	 */
	public void setFocusUpRecycle(boolean bool) {
		mBoolUp = bool;
	}

	/**
	 * 获取向上循环
	 * @return 返回true 表示循环 ，false 表示关闭循环
	 */
	public boolean getFocusUpRecycle() {
		return mBoolUp;
	}
	/**
	 * 设置左键  是否有响
	 * @param bool true表示不响应， false表示响应
	 */
	public void setFocusLeftStop(boolean bool){
		mBoolLeft = bool;
	}
	/**
	 * 获取左键响应�?
	 * @return 返回 true表示不响应， false表示响应
	 */
	public boolean getFocusLeftStop(){
		return mBoolLeft;
	}
	/**
	 * 设置右键是否有响
	 * @param bool true表示不响应， false表示响应
	 */
	public void setFocusRightStop(boolean bool){
		mBoolRight = bool;
	} 
	/**
	 * 获取右键响应�?
	 * @return 返回true表示不响应， false表示响应
	 */
	public boolean getFocusRightStop(){
		return mBoolRight;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && mBoolDown) {
			if (getCount() != 0) {
				View lastChildAt = getChildAt(getChildCount() - 1);
				if (null != lastChildAt) {
					Log.e(TAG, "last child position 1= "
							+ (getCount() - 1));
					if (lastChildAt.isSelected()) {
						Log.i(TAG, "last child position 2= "
								+ (getCount() - 1));
						this.setSelection(getCount() - 1, mBoolDown,
								keyCode);
						return true;
					}
				}
			}
		}else if (keyCode == KeyEvent.KEYCODE_DPAD_UP && mBoolUp) {
			if (getCount() != 0) {
				View firstChildAt = getChildAt(0);
				if (null != firstChildAt) {
					if (firstChildAt.isSelected()) {
						Log.e(TAG, "first child position = " + 0);
						this.setSelection(0, mBoolUp, keyCode);
						return true;
					}
				}
			}
		}else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT && mBoolLeft){
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && mBoolRight){
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_MENU){
			DLog.e("-----------------KeyEvent.KEYCODE_MENU");
			
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void setSelection(int position) {
		super.setSelection(position);
	}

	private void setSelection(int position, boolean bool, int keycode) {
		if (position == getCount() - 1 && bool
				&& KeyEvent.KEYCODE_DPAD_DOWN == keycode) {
			position = 0;
		}
		if (position == 0 && bool && KeyEvent.KEYCODE_DPAD_UP == keycode) {
			position = getCount() - 1;
		}
		Log.i(TAG, "next child position = " + position);
		super.setSelection(position);
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}
	
//	private void up(ListView list){
//		int showItems = (list.getLastVisiblePosition() - 
//				list.getFirstVisiblePosition());
//		if (list.getFirstVisiblePosition() + 1 <= showItems && list.getSelectedItemPosition() != 0) {
//			
//			list.setSelectionFromTop(0, 3);
//			
//		} else if(list.getSelectedItemPosition() == 0){
//			
//			list.setSelectionFromTop(list.getCount() -1, 3);
//		}else {
//
//			list.setSelectionFromTop(list.getFirstVisiblePosition()
//					- showItems + 1, 3);
//		}
//	}
	
//	private void down(ListView list){
//		int showItems = (list.getLastVisiblePosition() - list.getFirstVisiblePosition());
//		if (list.getFirstVisiblePosition() + 1 + showItems >= list.getCount() &&
//				list.getSelectedItemPosition() != (list.getCount()-1)) {
//			
//			list.setSelectionFromTop(list.getCount() - 1, 3);
//			
//		}else if(list.getSelectedItemPosition() == (list.getCount()-1)){
//			
//			list.setSelectionFromTop(0, 3);
//			
//		}else {
//			list.setSelectionFromTop(list.getFirstVisiblePosition()
//					+ showItems + 1, 3);
//
//		}
//	}
	
}
