package com.sen5.secure.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by zhoudao on 2017/6/15.
 */

public class TestService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TestService---", "-----onCreate------");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("TestService---", "-----onStartCommand------");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

        Log.e("TestService---", "-----onDestroy------");
        super.onDestroy();
    }
}
