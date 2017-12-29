package com.ipcamerasen5.main.nobuffer;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import nes.ltlib.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import hsl.p2pipcam.listener.DeviceStatusListener;

/**
 * @author jiangyicheng
 * @CLASS com.ipcamerasen5.main.nobuffer
 * @time Created at 2017/5/17 11:31.
 */

public class CameraProviderHelper {
    public static final String ACTION_RECEIVER_CLOSE_SERVICE = "com.sen5.process.camera.close.float";

    private CameraProviderFControl mCameraProviderFControl;

    public CameraProviderHelper(Context context, Handler handler){
        initCameraProvider(context, handler);
    }

    private void initCameraProvider(Context context, Handler handler){
        mCameraProviderFControl = new CameraProviderFControl(context, handler);
    }

    public void close(){
        mCameraProviderFControl.unRegister();
    }

    public List<EntityCameraStatus> getCameraData(){
        List<EntityCameraStatus> allCameraStatus = mCameraProviderFControl.getAllCameraStatus();
        return allCameraStatus;
    }

    public void refreshDataByProviderDataChange(DeviceStatusListener deviceStatusListener){
        //获取所有数据
        List<EntityCameraStatus> mAllCameraStatus = getCameraData();
        List<EntityCameraStatus> mAllVideoCameraStatus = new ArrayList<>();
        if(null != mAllCameraStatus && mAllCameraStatus.size() > 0){
            int size = mAllCameraStatus.size();
            for (int j = 0; j < size; j++) {
                LogUtils.e("refreshData","j = "+j+"mode is "+mAllCameraStatus.get(j).getDbFieldMode()+"status is "+mAllCameraStatus.get(j).getStatus());
                if (mAllCameraStatus.get(j).getDbFieldMode().equals("0")){
                    mAllVideoCameraStatus.add(mAllCameraStatus.get(j));
                }
            }
            for (int i=0; i<mAllVideoCameraStatus.size(); i++){
                EntityCameraStatus entityCameraStatus = mAllVideoCameraStatus.get(i);
                deviceStatusListener.receiveDeviceStatus(entityCameraStatus.getUserID(), entityCameraStatus.getStatus());
            }
        }
    }

    public long getCameraUserIdByPosition(int position){
        List<EntityCameraStatus> mAllCameraStatus = getCameraData();
        LogUtils.e("getCameraUserIdByPosition","mAllCameraStatus.size is "+mAllCameraStatus.size()+"position is "+position);
        List<EntityCameraStatus> mAllVideoCameraStatus = new ArrayList<>();
        if(position >= 0 && null != mAllCameraStatus && mAllCameraStatus.size() > position){
            for (int i=0; i<mAllCameraStatus.size();i++){
                EntityCameraStatus mCameraStatus = mAllCameraStatus.get(i);
                if (mCameraStatus.getDbFieldMode().equals("0")){
                    mAllVideoCameraStatus.add(mCameraStatus);
                }
            }
        }
//        LogUtils.e("getCameraUserIdByPosition","mAllVideoCameraStatus.size is "+mAllVideoCameraStatus.size()+"---"+mAllVideoCameraStatus.get(0).getUserID()+"---"+mAllVideoCameraStatus.get(1).getUserID());
        if (position >=0 && null != mAllVideoCameraStatus && mAllVideoCameraStatus.size() > position){
            return mAllVideoCameraStatus.get(position).getUserID();
        }
        return -1;
    }

    /**
     * 发送广播 通知Launcher 该客户端的使用已经结束
     * @param context
     */
    public void sendCloseBroadcast(Context context){
        Intent intent = new Intent();
        intent.setAction(ACTION_RECEIVER_CLOSE_SERVICE);
//		intent.setPackage("com.sen5.klauncher");
        context.sendBroadcast(intent);
    }

}
