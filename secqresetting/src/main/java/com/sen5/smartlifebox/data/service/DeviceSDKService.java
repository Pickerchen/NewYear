/*
 * Copyright (c) belongs to sen5.
 */

package com.sen5.smartlifebox.data.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import nes.ltlib.utils.AppLog;
import com.sen5.smartlifebox.data.event.CameraEvent;

import org.greenrobot.eventbus.EventBus;

import hsl.p2pipcam.nativecaller.DeviceSDK;


/**
 * Created by wanglin on 2016/10/28.
 * 用于初始化DeviceSDK，响应DeviceSDK状态的服务
 */
public class DeviceSDKService extends Service {

//    private static final String URL = "EBGJEBBBKAJIGEJLEPGCFAEPHCMCHMNFGJEABDCGBOJGLJLACAAKCBOMHBLNJHKCAKMHLEDAODMNANCBJHNMICAI";

    @Override
    public void onCreate() {
        super.onCreate();
        AppLog.e("DeviceSDKService-------onCreate");
        int initialize = DeviceSDK.initialize("");
        AppLog.e("-------------------initialize = " + initialize);
        DeviceSDK.setCallback(DeviceSDKService.this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppLog.e("DeviceSDKService-------onStartCommand");
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return new MyBinder();
    }


    public class MyBinder extends Binder {


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            DeviceSDK.unInitialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        AppLog.e("DeviceSDKService-------onDestroy");
    }

    /*************************
     * JNI回调
     **************************/
    public void CallBack_Event(long UserID, long nType) {
        if (nType == 100) {
            AppLog.i("Camera 在线");

            CameraEvent event = new CameraEvent(CameraEvent.CAMERA_CONNECTED);
            event.setUserId(UserID);
            event.setStatus(nType);
            EventBus.getDefault().post(event);
        } else if (nType == 0) {

            CameraEvent event = new CameraEvent(CameraEvent.CAMERA_CONNECTING);
            event.setUserId(UserID);
            event.setStatus(nType);
            EventBus.getDefault().post(event);
        } else if (nType == 101) {

            CameraEvent event = new CameraEvent(CameraEvent.CAMERA_CONNECT_ERROR);
            event.setUserId(UserID);
            event.setStatus(nType);
            EventBus.getDefault().post(event);
        } else if (nType == 10) {

            CameraEvent event = new CameraEvent(CameraEvent.CAMERA_CONNECT_TIME_OUT);
            event.setUserId(UserID);
            event.setStatus(nType);
            EventBus.getDefault().post(event);
        } else if (nType == 11) {

            CameraEvent event = new CameraEvent(CameraEvent.CAMERA_DISCONNECT);
            event.setUserId(UserID);
            event.setStatus(nType);
            EventBus.getDefault().post(event);
        }else if (nType == 9){
            CameraEvent event = new CameraEvent(CameraEvent.CAMERA_NO_ONLINE);
            event.setUserId(UserID);
            event.setStatus(nType);
            EventBus.getDefault().post(event);
        }
    }

    public void CallBack_SetParam(long UserID, long nType, int nResult) {
        AppLog.i("CallBack_SetParam 事件回调");
        if (nResult == 1) {
            if (nType == 0x2702) {//重命名成功
                CameraEvent event = new CameraEvent(CameraEvent.CAMERA_RENAME_SUCCESS);
                event.setUserId(UserID);
                EventBus.getDefault().post(event);
            }
        }

    }

    public void CallBack_SnapShot(long UserID, byte[] buff, int len) {

    }

    public void CallBack_GetParam(long UserID, long nType, String param) {


    }

    public void VideoData(long UserID, byte[] VideoBuf, int h264Data, int nLen,
                          int Width, int Height, int time) {

    }

    public void callBackAudioData(long nUserID, byte[] pcm, int size) {

    }

    public void CallBack_RecordFileList(long UserID, int filecount, String fname,
                                        String strDate, int size) {
    }

    public void CallBack_P2PMode(long UserID, int nType) {
    }

    public void CallBack_RecordPlayPos(long userid, int pos) {
    }

    public void CallBack_VideoData(long nUserID, byte[] data, int type, int size) {

    }

    public void CallBack_AlarmMessage(long UserID, int nType) {

    }

    public void CallBack_RecordPicture(long UserID, byte[] buff, int len) {
    }

    public void CallBack_RecordFileListV2(long UserID, String param) {

    }

    public void CallBack_TfPicture(long UserID, byte[] buff, int len) {
    }

    public void CallBack_RGB(long UserID, byte[] data, int i, int j, int k) {

    }

}
