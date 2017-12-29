package com.ipcamerasen5.main.utils;

import android.content.Context;

/**
 * Created by jiangyicheng on 2016/11/3.
 */

public class UtilsDisplay {
    /**
     * 将px转为dip或dp
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {

        final float scale = context.getResources().getDisplayMetrics().density;
        return getDivRound(pxValue, scale);
        //  return (int)(pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp转为px
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {

        final float scale = context.getResources().getDisplayMetrics().density;
        return getMultRound(dipValue, scale);
        //  return (int)(dipValue * scale + 0.5f);
    }
    /**
     * 将px转为sp
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {

        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return getDivRound(pxValue, fontScale);
        //  return (int)(pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp转为px
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {

        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return getMultRound(spValue, fontScale);
        //  return (int)(spValue * fontScale + 0.5f);
    }

    /**
     * 乘法运行并四舍五入
     * @param x
     * @param y
     * @return
     */
    public static int getMultRound(float x, float y){

        return Math.round(x*y);
    }

    /**
     * 除法运行并四舍五入
     * @param dividend  被除数
     * @param divisor 除数
     * @return
     */
    public static int getDivRound(float dividend, float divisor){

        return Math.round(dividend/divisor);
    }
}
