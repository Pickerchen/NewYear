package com.ipcamera.main.utils;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ipcamera.main.R;
import com.ipcamera.main.cuswidget.MyBottomScaleView;

public class UtilsFingerPrint {
	public static Drawable getPlayFailDefaultDrawable(Context context, String did, Drawable defaultDrawable){
		Drawable loadingDefaultPic = UtilsCapture.getLoadingDefaultPic(context, did);
		if(loadingDefaultPic == null){
			loadingDefaultPic = defaultDrawable;
		}
		return loadingDefaultPic;
	}
	
	public static void setMenuTitle(String[] stringArray, List<MyBottomScaleView> list){
		if(list == null || null == stringArray){
			return;
		}else{
			
			int length = stringArray.length;
			int size = list.size();
			for (int i = 0; i < size; i++) {
				String string = "";
				if(length > i){
					string = stringArray[i];
				}
				if(!TextUtils.isEmpty(string)){
					MyBottomScaleView myBottomScaleView = list.get(i);
					int childCount = myBottomScaleView.getChildCount();
					if(childCount>1){
						
						View childAt = myBottomScaleView.getChildAt(1);
						if(childAt != null && childAt instanceof TextView){
								((TextView)childAt).setText(string);
						}
					}
				}
			}
		}
	}
}
