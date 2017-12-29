package com.ipcamerasen5.main.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by jiangyicheng on 2017/1/17.
 */

public class Sen5Button extends Button{
    public Sen5Button(Context context) {
        super(context);
        init();
    }

    public Sen5Button(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Sen5Button(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public Sen5Button(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();

    }

    private void init(){
        // 字体
//        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
//		Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "DroidSans.ttf.ttf");
//        setTypeface(typeface);
    }
}