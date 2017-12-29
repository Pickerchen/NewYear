/**
 *Effects.java[V 1.0.0]
 *classes : com.eric.xlee.lib.utils.Effects
 * Xlee Create at 2016-4-21 上午10:15:02
 */
package com.eric.xlee.lib.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * com.eric.xlee.lib.utils.Effects
 * 
 * @author Xlee <br/>
 *         create at 2016-4-21 上午10:15:02
 */
public class Effects {

    public static final int DURATION = 200;
    public static final float SCALE_FOCUS = 1.05f;
    public static final float SCALE_UNFOCUS = 1f;

    private static Interpolator sInterpolator = new AccelerateDecelerateInterpolator();

    /**
     * 焦点状态缩放动画
     * 
     * @param view
     *            执行动画的对象实例
     * @param scale
     *            缩放系数
     * @param duration
     *            动画执行的时间
     * @param type
     *            动画类型，放大或者缩小
     */
    public static AnimatorSet focusAnimation(View view, float scale, int duration) {
        if (null == view) {
            return null;
        }
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, scale);
        scaleX.setDuration(duration);
        scaleX.setInterpolator(sInterpolator);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, scale);
        scaleY.setDuration(duration);
        scaleY.setInterpolator(sInterpolator);
        AnimatorSet set = new AnimatorSet();
        set.play(scaleX).with(scaleY);
        set.start();
        return set;
    }

    public static void stopAnimatorSet(AnimatorSet set) {
        if (null != set && set.isRunning()) {
            set.cancel();
        }
    }
}
