package com.ipcamerasen5.main.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 统一TextView的自定义样式
 */
public class Sen5TextView extends TextView {

	public Sen5TextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public Sen5TextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public Sen5TextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
	
	private void init() {
		
		// 字体
//		Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
//		Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "DroidSans.ttf.ttf");
//		setTypeface(typeface);
	}
}
