package com.sen5.secure.launcher.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sen5.secure.launcher.R;


/**
 * Created by zhoudao on 2017/5/12.
 */

public class Sen5TextView extends TextView {


    String[] typefacePath = {"fonts/Roboto-Light.ttf", "fonts/RobotoCondensed-Regular.ttf", "fonts/RobotoCondensed-Light.ttf"};


    private Typeface mTypeface = Typeface.createFromAsset(getContext().getApplicationContext().getAssets(), typefacePath[2]);


    public Sen5TextView(Context context) {
        super(context);
        init();
    }

    public Sen5TextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Sen5TextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Sen5TextView);
        mTypeface = Typeface.createFromAsset(getContext().getApplicationContext().getAssets(), typefacePath[typedArray.getInt(R.styleable.Sen5TextView_CustomTypeface, 2)]);
        init();
    }

    private void init() {

        setTypeface(mTypeface);
    }
}
