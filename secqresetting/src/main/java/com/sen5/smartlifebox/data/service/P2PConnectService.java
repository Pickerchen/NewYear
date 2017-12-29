package com.sen5.smartlifebox.data.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import nes.ltlib.utils.AppLog;
import com.sen5.smartlifebox.data.p2p.P2PModel;

/**
 * Created by wanglin on 2017/2/7.
 */
public class P2PConnectService extends Service {

    private P2PModel mP2PModel;
    private boolean isOpen;

    @Override
    public void onCreate() {
        isOpen = true;
        mP2PModel = P2PModel.getInstance(this);
        AppLog.e("安防设备服务启动");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {

            String encryptKey = intent.getStringExtra("EncryptKey");
            //连接P2P
            if (!TextUtils.isEmpty(encryptKey) && isOpen) {
                mP2PModel.connectDevice(encryptKey);
                AppLog.e("安防设备服务");
                isOpen = false;
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        isOpen = false;
        mP2PModel.destroyP2P();
    }
}
