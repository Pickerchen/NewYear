package com.sen5.launchertools.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class Sen5TextView extends TextView {
	
	private Context mContext;

	public Sen5TextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	public Sen5TextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public Sen5TextView(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	private void init() {
		Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
		setTypeface(typeface);
	}

    @Override
    public boolean isFocused() {
        return true;
    }
}
