package com.sen5.secure.launcher.data.control;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.sen5.secure.launcher.R;
import com.sen5.secure.launcher.data.interf.IWallpaper;

/**
 * Created by ZHOUDAO on 2017/6/5.
 */

public class WallpaperControl implements IWallpaper {

    private Context mContext;
    private static final String ACTION_DEFAULT_WALLPAPER = "android.intent.action.WALLPAPER_CHANGED";
    private WallpaperBroadCast broadCast;
    private View wallView;

    public WallpaperControl(Context mContext, View view ) {
        this.mContext = mContext;
        this.wallView = view;

        Drawable drawable = mContext.getDrawable(R.drawable.bg_main);
        this.getDrawable(drawable);
        registerBroadCast();

    }


    private void registerBroadCast() {
        broadCast = new WallpaperBroadCast();
        IntentFilter intentFilter = new IntentFilter(ACTION_DEFAULT_WALLPAPER);
        intentFilter.addAction(Intent.ACTION_WALLPAPER_CHANGED);

        mContext.registerReceiver(broadCast, intentFilter);
    }

    public void unRegisterBroadCast() {
        if (broadCast!=null)
        mContext.unregisterReceiver(broadCast);
    }

    @Override
    public void getDrawable(Drawable d) {
        wallView.setBackground(d);
    }

    private class WallpaperBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_DEFAULT_WALLPAPER.equals(action)
                    || Intent.ACTION_WALLPAPER_CHANGED.equals(action)) {
                getDrawable(WallpaperManager.getInstance(mContext).getDrawable());
            }
        }
    }
}
