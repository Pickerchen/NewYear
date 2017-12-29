package com.sen5.secure.launcher.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;

import android.util.AttributeSet;
import android.view.View;



public  class ScaleAbleView extends Sen5TextView  {
    private static final float SCALE_SIZE = 1.2f;
    private static final int ANIMATOR_DURATION = 200;
    private AnimatorSet mAnimatorSet = null;
    private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View arg0, boolean focused) {
            scalAnimator(focused);
        }
    };

    public ScaleAbleView(Context context) {
        this(context, null);
    }

    public ScaleAbleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ScaleAbleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnFocusChangeListener(mOnFocusChangeListener);
    }



    private void scalAnimator(boolean focused) {
        if (null != mAnimatorSet && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
        }
        float scale = 1f;
        if (focused) {
            scale = SCALE_SIZE;
        } else {
            scale = 1f;
        }
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, View.SCALE_X, scale);
        animatorX.setDuration(ANIMATOR_DURATION);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, View.SCALE_Y, scale);
        animatorY.setDuration(ANIMATOR_DURATION);
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(animatorX).with(animatorY);
        mAnimatorSet.start();
    }

    @Override
    public void setOnFocusChangeListener(final OnFocusChangeListener l) {
        if (null != l && mOnFocusChangeListener != l) {
            mOnFocusChangeListener = new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View arg0, boolean focused) {
                    scalAnimator(focused);
                    if (null != l) {
                        l.onFocusChange(arg0, focused);
                    }
                }
            };
        }
        super.setOnFocusChangeListener(mOnFocusChangeListener);
    }

}
