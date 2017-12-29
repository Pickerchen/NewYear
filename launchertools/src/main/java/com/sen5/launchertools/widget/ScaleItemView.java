package com.sen5.launchertools.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author JesseYao
 * @version 2016 2016年12月8日 下午2:45:24 ClassName：ScaleItemView.java Description：
 */
public class ScaleItemView extends LinearLayout {
	private static final float SCALE_SIZE = 1.0f;
	private static final int ANIMATOR_DURATION = 200;
	private AnimatorSet mAnimatorSet = null;

	public ScaleItemView(Context context) {
		super(context);
	}

	public ScaleItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ScaleItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		scalAnimator(gainFocus);
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
}
