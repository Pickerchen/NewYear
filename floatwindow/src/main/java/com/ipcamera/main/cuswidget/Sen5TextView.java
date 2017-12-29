package com.ipcamera.main.cuswidget;

import com.ipcamera.main.utils.UtilsTypeface;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 统一TextView的自定义样式
 */
public class Sen5TextView extends TextView {

	public Sen5TextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mContext = context;
		init();
	}

	public Sen5TextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		init();
	}

	public Sen5TextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		init();
	}
	
	private void init() {
		UtilsTypeface.TYPEFACE_ROBOTO_LIGHT.setTypeface(this);
	
	}
}
