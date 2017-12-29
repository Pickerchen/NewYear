package com.sen5.secure.launcher.data.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.eric.xlee.lib.utils.LogUtil;
import com.sen5.secure.launcher.R;

import nes.ltlib.utils.AppLog;

/**
 * Created by ZHOUDAO on 2017/9/14.
 */

public class VersionsControl {

    public int getCode() {
        return code;
    }

    /**
     * 0:OTT 1: DvB
     */
    private int code;
    private WallpaperControl wallpaperControl;

    private Context context;



    public VersionsControl(int code, Context context) {
        this.code = code;
        this.context = context;
    }




    public void WallpaperControl(View view) {

        if (code == 0) {
            wallpaperControl = new WallpaperControl(context, view);

        }

    }


    private void moveBack(boolean isTrue) {
//        ((Activity) context).moveTaskToBack(isTrue);

        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(home);

    }


    public void onCreate(Intent intent) {

        if (code == 1) {
            boolean isMove = intent.getBooleanExtra("isMoveToBack", false);
            AppLog.e(" moveTaskToBack : isMove onNewIntent "+ isMove + "");
            if (isMove) {
                moveBack(true);
            }
        }
    }

    public void onBackPressed() {
        if (code == 1) {
            moveBack(false);
        }
    }

    public void onDestroy() {
        if (wallpaperControl != null) {
            wallpaperControl.unRegisterBroadCast();
        }
    }

}
