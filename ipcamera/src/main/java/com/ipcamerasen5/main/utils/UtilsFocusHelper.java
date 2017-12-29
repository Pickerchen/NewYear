package com.ipcamerasen5.main.utils;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class UtilsFocusHelper {
	private static final String TAG = UtilsFocusHelper.class.getSimpleName();

	private static void setFocusCycle1(List<?> listView){

	}

	/**
	 * 设置列表内view的焦点按照列表的顺序  左右循环
	 * @param listView
	 */
	public static void setFocusCycle(List<View> listView) {
		if (null == listView) {
			return;
		}

		int size = listView.size();
		for (int i = 0; i < size; i++) {
			View view = listView.get(i);
			int j = i + 1;
			View view2 = null;

			if (j < size) {
				view2 = listView.get(j);
			} else {
				view2 = listView.get(0);
			}

			if (null != view && view2 != null) {

				view.setNextFocusRightId(view2.getId());
			}

			if (null != view && view2 != null) {

				view2.setNextFocusLeftId(view.getId());
			}
		}

	}


	/**
	 * 设置列表内view的焦点按照列表的顺序  左右循环
	 * @param listView
	 */
	public static void setFocusCycleNew(List<ViewGroup> listView) {
		if (null == listView) {
			return;
		}

		int size = listView.size();
		int focusCount = 0;
		for (int i = 0; i < size; i++) {
			boolean focused = listView.get(i).isFocusable();
			if(!focused){
				break;
			}else{
				focusCount = i + 1;
			}
		}

		size = focusCount;
//		LogUtil.e("------------size = " + size);

		for (int i = 0; i < size; i++) {
			View view = listView.get(i);
			int j = i + 1;
			View view2 = null;

			if (j < size) {
				view2 = listView.get(j);
			} else {
				view2 = listView.get(0);
			}

			if (null != view && view2 != null) {

				view.setNextFocusRightId(view2.getId());
			}

			if (null != view && view2 != null) {

				view2.setNextFocusLeftId(view.getId());
			}
		}

	}

	/**
	 * 设置两个view焦点 循环
	 * @param viewFirst
	 * @param viewLast
	 */
	public static void setFocusCycle(View viewFirst, View viewLast){
		if(viewFirst ==null || viewLast == null){
			Log.e(TAG, "----------setFocusCycle " + (viewFirst ==null)+ " || "+ (viewLast == null));
			return;
		}
		viewFirst.setNextFocusLeftId(viewLast.getId());
		viewLast.setNextFocusRightId(viewFirst.getId());
	}

}
