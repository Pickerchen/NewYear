package com.ipcamera.main.utils;

import android.graphics.Typeface;
import android.widget.TextView;

/**
 * @fileName UtilsTypeface.java
 * @describe 
 * @author jiangyicheng
 * @date 2016-03-22
 */
public enum UtilsTypeface {

	TYPEFACE_ROBOTO_LIGHT(PATH.TYPEFACE_ROBOTO_LIGHT_TTF);

	private interface PATH {
		public final String TYPEFACE_ROBOTO_LIGHT_TTF = "fonts/Roboto-Light.ttf";
	};

	private String mFace;

	UtilsTypeface() {

	};

	UtilsTypeface(String face) {
		mFace = face;
	};

	public void setTypeface(TextView view) {
		// 字体
		Typeface typeface = Typeface.createFromAsset(view.getContext()
				.getAssets(), mFace);
		// Typeface typeface = Typeface.createFromAsset(context.getAssets(),
		// "fonts/Roboto-Light.ttf");
		// Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),
		// "DroidSans.ttf.ttf");
		view.setTypeface(typeface);
	}

}
