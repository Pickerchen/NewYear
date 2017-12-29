package com.sen5.secure.launcher.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.GridView;

import com.eric.xlee.lib.utils.LogUtil;
import com.sen5.secure.launcher.R;

/**
 * Created by ZHOUDAO on 2017/5/22.
 */

public class ScaleGridView extends GridView {

    private Drawable bg = getContext().getDrawable(R.drawable.selector_bg_blue);
    private Rect bgRect;
    private int mSelectedPosition;
    private View mSelectedView;
    private float mScaleX = 1.2f;
    private float mScaleY = 1.2f;

    public ScaleGridView(Context context) {
        this(context, null);
    }

    public ScaleGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setClipChildren(false);
        setClipToPadding(false);
        setChildrenDrawingOrderEnabled(true);

        bgRect = new Rect();
        bg.getPadding(bgRect);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (bg == null) {
            return;
        }
        drawSelector(canvas);
    }

    private void drawSelector(Canvas canvas) {
        View view = getSelectedView();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            if(view == getChildAt(i)){
                LogUtil.e("----------------i = " + i + "  isFocused() = " + isFocused());
            }
        }
//        bringChildToFront(view);

        if (isFocused()) {
            scaleSelectedView(view);

            Rect gvVisRect = new Rect();
            this.getGlobalVisibleRect(gvVisRect);
            Rect SelectedViewRect = new Rect();
            if (mSelectedView instanceof ViewGroup) {
                mSelectedView.getGlobalVisibleRect(SelectedViewRect);

                bg.setBounds(SelectedViewRect);
                bg.draw(canvas);
            }

        }
    }


    @Override
    public void bringChildToFront(View child) {
        mSelectedPosition = indexOfChild(child);
        invalidate();
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (mSelectedPosition != AbsListView.INVALID_POSITION) {
            if (i == mSelectedPosition) {
                return childCount - 1;
            }
            if (i == childCount - 1) {
                return mSelectedPosition;
            }
        }

        return super.getChildDrawingOrder(childCount, i);
    }

    private void scaleSelectedView(final View view) {
        unScalePreView();

        ObjectAnimator objectAnimator_x = ObjectAnimator.ofFloat(view, SCALE_X, 1.0f, 1.2f);
        ObjectAnimator objectAnimator_y = ObjectAnimator.ofFloat(view, SCALE_Y, 1.0f, 1.2f);
        AnimatorSet animationSet = new AnimatorSet();
        animationSet.play(objectAnimator_x).with(objectAnimator_y);
        animationSet.start();
        animationSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mSelectedView = view;

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
//        ViewCompat.animate(mSelectedView).scaleX(1.20f).scaleY(1.20f).translationZ(1.2f).start();
//        view.setScaleX(mScaleX);
//        view.setScaleY(mScaleY);

//        view.animate().scaleY(2f).scaleX(2f).setDuration(2000).start();


//        mSelectedView.animate().scaleX(mScaleX).scaleY(mScaleY).setDuration(300).start();
    }

    private void unScalePreView() {

        if (mSelectedView != null) {
//            mSelectedView.setScaleX(1);
//            mSelectedView.setScaleY(1);
//            ViewCompat.animate(mSelectedView).scaleX(1).scaleY(1).translationZ(1).start();
//            mSelectedView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();

            ObjectAnimator objectAnimator_x = ObjectAnimator.ofFloat(mSelectedView, SCALE_X, 1.2f, 1.0f);
            ObjectAnimator objectAnimator_y = ObjectAnimator.ofFloat(mSelectedView, SCALE_Y, 1.2f, 1.0f);
            AnimatorSet animationSet = new AnimatorSet();
            animationSet.play(objectAnimator_x).with(objectAnimator_y);
            animationSet.start();
            animationSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mSelectedView = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

        }
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (!gainFocus) {
            unScalePreView();
            requestLayout();
        }


        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }


}
