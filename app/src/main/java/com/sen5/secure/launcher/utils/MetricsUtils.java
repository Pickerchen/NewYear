package com.sen5.secure.launcher.utils;
import android.app.Activity;
import android.graphics.Rect;
import android.util.DisplayMetrics;

public class MetricsUtils {
    /**
     * 获取屏幕尺寸
     *
     * @param context
     * @return
     */
    public static int[] getScreenHight(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int[] arr = {0, 0};
        arr[0] = metrics.widthPixels;
        arr[1] = metrics.heightPixels;
        return arr;
    }

    /**
     * 获取应用尺寸
     */
    public static int[] getAppScreenHight(Activity context) {
        Rect outRect = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        int[] arr = {0, 0};
        arr[0] = outRect.width();
        arr[1] = outRect.height();
        return arr;
    }
}
