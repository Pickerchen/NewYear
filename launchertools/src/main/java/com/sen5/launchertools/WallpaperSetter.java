package com.sen5.launchertools;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

/**
 * Created by yaojiaxu on 2017/3/3 0003.
 * ClassName: WallpaperSetter
 * Description:
 */

public class WallpaperSetter {

    private Context mContext = null;
    private WallpaperManager mWallpaperManager = null;
    private View mView = null;

    public WallpaperSetter(Context context, View mainView) {
        mContext = context;
        mWallpaperManager = WallpaperManager.getInstance(context);
        mView = mainView;
        setWallpaperAsBackground();
    }



    public void registerWallpaperChangeListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SET_WALLPAPER);
        filter.addAction(Intent.ACTION_WALLPAPER_CHANGED);
        mContext.registerReceiver(mWallpaperChangeReceiver, filter);
    }

    public void unregisterWallpaperChangeListener() {
        mContext.unregisterReceiver(mWallpaperChangeReceiver);
    }

    public void setWallpaperAsBackground() {
        Drawable drawable = mWallpaperManager.getDrawable();
        if (null != drawable && null != mView) {
            mView.setBackground(drawable);
        }
    }

    public void setWallpaper(int resourceId) {
        try {
            mWallpaperManager.setResource(resourceId);
        } catch (Exception e) {
            Log.d(TAG, "-------setWallpaper from resource failed!-----");
            e.printStackTrace();
        }
    }

    public void setWallpaper(Bitmap bitmap) {
        try {
            mWallpaperManager.setBitmap(bitmap);
        } catch (Exception e) {
            Log.d(TAG, "-------setWallpaper from bitmap failed!-----");
            e.printStackTrace();
        }
    }

    private BroadcastReceiver mWallpaperChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SET_WALLPAPER) ||
                    intent.getAction().equals(Intent.ACTION_WALLPAPER_CHANGED)) {
                setWallpaperAsBackground();
            }
        }
    };

    private static final String TAG = WallpaperSetter.class.getSimpleName();
}
