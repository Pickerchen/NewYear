package com.ipcamera.main.service;

import com.ipcamera.main.control.CameraProviderFControl;
import com.ipcamera.main.control.CameraSourceControl;
import com.ipcamera.main.control.LTCameraControl;
import com.ipcamera.main.control.UIFloatViewControl;
import com.ipcamera.main.utils.DLog;
import com.ipcamera.main.utils.MySharePre;
import com.ipcamera.main.utils.MySharePreDID;
import com.ipcamera.main.view.FingerPrint;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import java.util.List;

import hsl.p2pipcam.entity.EntityCameraStatus;
import nes.ltlib.LTSDKManager;

/**
 * @author jiangyicheng
 * @项目�?Satellite
 * @公司
 * @创建时间 2015�?1�?4�?下午4:50:41
 * @类说�?
 */
public class FingerPrintViewService extends Service {
    private static final String TAG = "---888---";
    /**
     * Home键点击的Action
     */
    public static final String ACTION_KEYCODE_HOME = "com.amlogic.dvbplayer.homekey";


    public static final String ACTION_IPCAMERA_KEY_FLOAT = "com.sen5.process.camera.key";
    /**
     * 携带参数 key = CameraDID ；value = “DID的具体值，string类型”
     */
    public static final String ACTION_IPCAMERA_CHANGE = "com.sen5.smartlife.camerachange";

    private static final String ACTION_CLOSE_ALL = "com.close.camera.float.window";


    private FingerPrint mFingerPrint;
    private LTCameraControl ltCameraControl;

    private MyBroadcastReceiver mMyBroadcastReceiver;

    private static final String RECEIVER_MAC = "receiver_mac";
    private static final String RECEIVER_POSITION = "receiver_position";
    public static final String FLAG_SHOW_FLOAT_VIEW = "flag_show_view";
    public static final String CAMERA_FLOAT_PLAY_FLAG = "sys.camera.floatplay";
    public int cameraType = 1;

    private List<EntityCameraStatus> mAllCameraStatus;
    private CameraProviderFControl mCameraProviderFControl;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        DLog.e(TAG + "onCreate: ");
        DLog.d(TAG + "DVB SPEED", "POP:onCreate");
        super.onCreate();
        MySharePre.initMySharePre(this);
        MySharePreDID.initMySharePre(this);
        DLog.i(TAG + "onCreate: init UI");

//		initView(); // 初始化UI
        registerReceiverHome();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DLog.d(TAG + "onStartCommand");
        // mDvb = DVBSApplication.getDVBClient();
        DLog.d(TAG + "DVB SPEED", "onStartCommand: FINISH");
        if (null != intent) {
            Bundle extras = intent.getExtras();
            if (null != extras) {
                String cameraDefaultMac = extras.getString(RECEIVER_MAC, "");
                String cameraDefaultPosition = extras.getString(RECEIVER_POSITION, "");
                boolean isShowView = extras.getBoolean(FLAG_SHOW_FLOAT_VIEW, false);

                mCameraProviderFControl = new CameraProviderFControl(getApplicationContext(), new Handler());
                mAllCameraStatus = mCameraProviderFControl.getAllCameraStatus();

                cameraType = CameraSourceControl.getInstance().getCameraType();

//                if (!mAllCameraStatus.isEmpty()) {
//
//                    if (!mAllCameraStatus.get(0).getDid().startsWith("SLIFE")) {
//                        cameraType = 0;
//                    } else {
//                        cameraType = 1;
//                    }
//
//                }


                DLog.i("-----------CameraType--------" + cameraType);

                long userID = extras.getLong("camera_userid", -1L);
                DLog.i("------------startStatus-------" + userID + "-----isShowView = " + isShowView);
                if (isShowView) {
                    initView(cameraDefaultMac, userID);
                }
//                DLog.i("------------startStatus-------" + userID + "-------------cameraDefaultMac = " + cameraDefaultMac + "  cameraDefaultPosition = " + cameraDefaultPosition);

                mCameraProviderFControl.unRegister();

            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        DLog.d(TAG + "DVB SPEED", "POP:onDestroy");
        super.onDestroy();
        DLog.d(TAG + "DVB SPEED", "POP:onDestroy FINISH");
        unRegisterReceiver(mMyBroadcastReceiver);
    }


    /**
     * 初始化小窗UI cameraType:0 新浪涛摄像头 1：原始摄像头
     */
    private void initView(String did, long userId) {
        if (cameraType == 1) {

            if (null == mFingerPrint) {
                mFingerPrint = new FingerPrint(this, did, userId);
                mFingerPrint.showView(2, 0, 0, 1);
            } else {
                mFingerPrint.playCameraByPosition(did);
            }
        } else if (cameraType == 0) {
            if (null == ltCameraControl) {
                ltCameraControl = new LTCameraControl(this, did);
                ltCameraControl.showView(2, 0, 0, 1);
            } else {
                ltCameraControl.playByGid(did);
            }
        }


    }

    private void registerReceiverHome() {
        mMyBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_IPCAMERA_KEY_FLOAT);
        filter.addAction(ACTION_IPCAMERA_CHANGE);
        filter.addAction(ACTION_CLOSE_ALL);
        registerReceiver(mMyBroadcastReceiver, filter);
    }

    private void unRegisterReceiver(BroadcastReceiver roadcastReceiver) {
        if (null != roadcastReceiver) {

            unregisterReceiver(roadcastReceiver);

        } else {
            DLog.e("----------------roadcastReceiver == null");
        }
    }

    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (null == intent || null == context) {
                return;
            }
            String action = intent.getAction();
            DLog.d("----------------action = " + action);
            if (ACTION_IPCAMERA_KEY_FLOAT.equals(action)) {
                DLog.d("----------------click ACTION_IPCAMERA_KEY_FLOAT key");
                if (null != mFingerPrint) {
                    mFingerPrint.requestFocus();
                }

                if (null != ltCameraControl) {
                    ltCameraControl.requestFocus();
                }

            } else if (ACTION_IPCAMERA_CHANGE.equals(action)) {
                if (null != mFingerPrint) {
                    mFingerPrint.refreshData();
                }

                if (null != ltCameraControl) {
                    ltCameraControl.refreshData();
                }
            } else if (ACTION_CLOSE_ALL.equals(action)) {
                if (null != mFingerPrint) {
                    mFingerPrint.closeAll(true);
                }


                if (null != ltCameraControl) {
                    ltCameraControl.closeAll(true);
                }
            }
        }
    }

}
