package com.sen5.secure.myapplication;

import android.app.Application;

import com.nes.ipc.CameraManager;

/**
 * Created by ZHOUDAO on 2017/8/16.
 */

public class App extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        CameraManager.getInstance().initSDK(this);

    }
}
