package com.sen5.smartlifebox.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.sen5.smartlifebox.R;


/**
 * Created by ZHOUDAO on 2017/7/18.
 */

public class ScanView extends View {

    private Paint mPaint;
    private long time = TIME;
    private int gray = Color.parseColor("#33b5b5b5");
    private int white = Color.parseColor("#ffffff");
    private int txtBlue = Color.parseColor("#00bbd3");


    private float mViewCenterX;
    private float mViewCenterY;
    private CountDownTimer countTimer;
    private final static long TIME = 180000;


    public ScanView(Context context) {
        this(context, null);
    }

    public ScanView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }


    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);


        countTimer = new CountDownTimer(TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                time = millisUntilFinished;

                invalidate();

            }

            @Override
            public void onFinish() {
                if (listener != null) {
                    listener.onTimeFinish();
                }
            }
        };

        countTimer.start();


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float x = mViewCenterX;
        float y = mViewCenterY;


        //画灰色圆圈背景
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(gray);
        canvas.drawCircle(x, y, 50, mPaint);

        //画提示语
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(white);
        mPaint.setTextSize(30);
        String str = getContext().getString(R.string.scan_devices_tips);

        float txtX = x - getStringWidth(str) / 2;
        float txtY = y - 50 - getStringHeight(str) / 2;
        canvas.drawText(str, txtX, txtY, mPaint);

        //画倒计时和波纹

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(txtBlue);


        mPaint.setTextSize(34);


        if (time > 100000) {
            txtX = x - getStringWidth(time / 1000 + "") / 2 - getStringWidth("s");

        } else {
            txtX = x - getStringWidth(time / 1000 + "") / 2 - getStringWidth("s") / 2;

        }
        txtY = y + getStringHeight(time / 1000 + "") / 2;
        canvas.drawText(time / 1000 + "", txtX, txtY, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(white);
        mPaint.setTextSize(34);

        txtX = x + getStringWidth("s") / 2;
        txtY = y + getStringHeight("s") / 2;
        canvas.drawText("s", txtX, txtY, mPaint);


    }


    private int getStringWidth(String str) {
        return (int) mPaint.measureText(str);
    }

    private int getStringHeight(String str) {
        Rect rect = new Rect();

        mPaint.getTextBounds(str, 0, 1, rect);
        return rect.height();  //ceil() 函数向上舍入为最接近的整数。
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mViewCenterX = getWidth() / 2;
        mViewCenterY = getHeight() / 2;


    }

    public interface timeListener {
        void onTimeFinish();
    }

    public void setListener(timeListener listener) {
        this.listener = listener;
    }

    public timeListener listener;

    public void reset() {
        countTimer.cancel();

        time = TIME;
        countTimer.start();
    }

}
