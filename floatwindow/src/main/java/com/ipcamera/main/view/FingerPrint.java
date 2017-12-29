package com.ipcamera.main.view;

import hsl.p2pipcam.camera.MyRender.RenderListener;
import hsl.p2pipcam.entity.EntityCameraStatus;
import hsl.p2pipcam.entity.EntityDevice;
import hsl.p2pipcam.listener.DeviceStatusListener;

import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.ipcamera.main.R;
import com.ipcamera.main.control.CameraProviderFControl;
import com.ipcamera.main.control.DataCameraControl;
import com.ipcamera.main.control.UIFingerPrintControl;
import com.ipcamera.main.control.UIFloatViewControl;
import com.ipcamera.main.imple.ICameraStatus;
import com.ipcamera.main.service.FingerPrintViewService;
import com.ipcamera.main.smarthome.CameraControlNew;
import com.ipcamera.main.utils.DLog;
import com.ipcamera.main.utils.UtilsFingerPrint;
import com.ipcamera.main.utils.UtilsStartActivityCustom;
import com.ipcamera.main.utils.UtilsToast;
import com.ipcamera.main.utils.UtilsToastDebug;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.TextView;

@SuppressLint("RtlHardcoded")
public class FingerPrint implements OnClickListener, OnKeyListener, Callback, DeviceStatusListener,
        RenderListener {
    private static final String TAG = FingerPrint.class.getSimpleName();

    public static final int INDEX_RECORD = 0;
    //	private static final int DEFAULT_HIDE_VIEW_TIEM = 800000;
    private static final int DEFAULT_HIDE_VIEW_TIEM = 8000;

    private static final int MSG_RESET_NOTCAMERA_FLAG = 3;
    private static final int MSG_HIDE_MAIN_VIEW = 10;
    private static final int MSG_REFRESH_CAMERADEVICE_UI = 23;
    private static final int MSG_REMOVE_PB_UI = 26;
    private static final int MSG_REMOVE_PB_UI_TIME_OUT = 27;
    private static final int MSG_INIT_CAMERA_DATA = 28;
    private static final int MSG_PLAY_STREAM_CALLBACK = 29;
    private static final int MSG_LOAD_END = 30;

    private long mUserId = -1;
    private boolean isNotCameraBitmap = true;

    private View mView;
    private Handler mHandler;
    private Context mContext;
    private Resources mResources;
    private String mCameraDIDdefault;
    private Drawable mDefaultDrawable;
    private List<EntityCameraStatus> mAllCameraStatus;

    private DataCameraControl mDataCameraControl;
    private UIFloatViewControl mUIFloatViewControl;
    private UIFingerPrintControl mUIFingerPrintControl;
    private CameraProviderFControl mCameraProviderFControl;
    private List<TextView> mListTV_CameraStatus = new ArrayList<TextView>();


    public FingerPrint(Context context, String did, long userId) {
//		this.mUserId = userId;
        DLog.d(TAG + "------------new DVBFingerPrint");
        mContext = context;
        FloatDynamicWave.WAVE_START = true;
        CameraControlNew.mCameraSwitchOver = true;

        mHandler = new Handler(this);
        EventBus.getDefault().register(this);
        mResources = mContext.getResources();
        mCameraDIDdefault = did;

        mCameraProviderFControl = new CameraProviderFControl(mContext, mHandler);

        mAllCameraStatus = mCameraProviderFControl.getAllCameraStatus();
        if (null != mAllCameraStatus && mAllCameraStatus.size() > 0) {
            this.mUserId = mAllCameraStatus.get(0).getUserID();
            if (!TextUtils.isEmpty(mCameraDIDdefault)) {
                int size = mAllCameraStatus.size();
                for (int i = 0; i < size; i++) {
                    if (mAllCameraStatus.get(i).getDid().equals(mCameraDIDdefault)) {
                        this.mUserId = mAllCameraStatus.get(i).getUserID();
                    }
                }
            }
        }

        CameraControlNew.setData(mAllCameraStatus);
        initData();
        initView();
    }

    private void initData() {
        mDefaultDrawable = mContext.getDrawable(R.drawable.bg_camera_default);
    }

    private void initView() {

        mUIFingerPrintControl = new UIFingerPrintControl(mContext, new UIFingerPrint(mContext));
        mUIFingerPrintControl.setGlsListener(this);
        mUIFingerPrintControl.setOnClickListenerByMenuItem(this);
        mUIFingerPrintControl.setOnKeyListenerByMenuItem(this);
        mView = mUIFingerPrintControl.getView();
        mUIFingerPrintControl.addStatusView(mListTV_CameraStatus);

        mUIFloatViewControl = new UIFloatViewControl(mContext);
        mUIFloatViewControl.setView(mView);
        mUIFloatViewControl.setSplitScreen(mContext, 2.6f, true);

        addMyView();
    }

    private void addMyView() {

        CameraControlNew.setUserId(mUserId);
        CameraControlNew.startPlayStreamTest(CameraControlNew.STATUS_SWITCH_NONE);

        Drawable bitmapToDrawable = UtilsFingerPrint.getPlayFailDefaultDrawable(mContext, null, mDefaultDrawable);

        mUIFingerPrintControl.setVideoDefaultBg(bitmapToDrawable);

        mDataCameraControl = new DataCameraControl(mContext, this);
        mDataCameraControl.setDefaultDID(mCameraDIDdefault);
        mDataCameraControl.setICameraStatus(new MyICameraStatus());
        mDataCameraControl.setUIControlFingerPrint(mUIFingerPrintControl);

        mHandler.sendEmptyMessageDelayed(MSG_HIDE_MAIN_VIEW, DEFAULT_HIDE_VIEW_TIEM);
    }

    public void showView(int location, int offsetX, int offsetY, float alpha) {
        mUIFloatViewControl.showView(location, offsetX, offsetY, alpha);
    }

    public void requestFocus() {
        mUIFloatViewControl.requestFocus();
        mUIFingerPrintControl.showMainView(false);
    }

    private void clearFocus() {
        mUIFloatViewControl.clearFocus();
        mUIFingerPrintControl.hideMainView();
    }

    public void refreshData() {
        mAllCameraStatus = mCameraProviderFControl.getAllCameraStatus();
        CameraControlNew.setData(mAllCameraStatus);
        mDataCameraControl.refreshData();
    }

    public void playCameraByPosition(String did) {
        mDataCameraControl.playCameraByPosition(did);
    }

    public void closeAll(boolean isSendCloseBroadcast) {
        FloatDynamicWave.WAVE_START = false;
        mCameraProviderFControl.unRegister();
        CameraControlNew.closeStreamCamera(mDataCameraControl.getListCameraDevice());
        CameraControlNew.closeAll(mContext, isSendCloseBroadcast);
        closeService();
    }

    private void closeService() {
        mHandler.removeMessages(MSG_HIDE_MAIN_VIEW);
        mUIFloatViewControl.removeView();
        EventBus.getDefault().unregister(this);
        Intent intent = new Intent();
        intent.setClass(mContext, FingerPrintViewService.class);
        mContext.stopService(intent);
//		System.exit(0);

//		intent.setClass(mContext, FingerPrintViewService.class);
//		intent.putExtra(FingerPrintViewService.FLAG_SHOW_FLOAT_VIEW, false);
//		mContext.startService(intent);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int i = v.getId();
        if (i == R.id.ll_left) {
            UtilsToastDebug.showToastTest(mContext, "left");
            mDataCameraControl.switchCamera(CameraControlNew.STATUS_SWITCH_LEFT);

        } else if (i == R.id.ll_right) {
            UtilsToastDebug.showToastTest(mContext, "right");
            mDataCameraControl.switchCamera(CameraControlNew.STATUS_SWITCH_RIGHT);

        } else if (i == R.id.ll_fullscren) {
            EntityDevice cameraDeviceByPosition = mDataCameraControl.getCameraDeviceByPosition(mDataCameraControl.getPosition());
            if (null == cameraDeviceByPosition) {
                UtilsToast.makeToast(mContext, R.string.no_camera).show();
                return;
            }
            UtilsStartActivityCustom.startFullCamera(mContext, mDataCameraControl.getPosition(), cameraDeviceByPosition.DeviceID);
            closeAll(false);
            UtilsToastDebug.showToastTest(mContext, "full screen");

        } else if (i == R.id.ll_close) {
            UtilsToastDebug.showToastTest(mContext, "close");
            closeAll(true);

        } else {
        }
    }

    @Override
    public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
        // TODO Auto-generated method stub
        DLog.d("------------------onKey arg1 = " + arg1);
        mHandler.removeMessages(MSG_HIDE_MAIN_VIEW);
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_MAIN_VIEW, DEFAULT_HIDE_VIEW_TIEM);
        switch (arg1) {
            case KeyEvent.KEYCODE_BACK:
                clearFocus();
                break;

            default:
                break;
        }
        return false;
    }

    @Override
    public boolean handleMessage(Message arg0) {
        // TODO Auto-generated method stub
        switch (arg0.what) {

            case MSG_HIDE_MAIN_VIEW:
                clearFocus();
                break;

            case MSG_RESET_NOTCAMERA_FLAG:
                DLog.e("--------------isNotCameraBitmap = " + isNotCameraBitmap);
                isNotCameraBitmap = true;
                break;

            case MSG_REFRESH_CAMERADEVICE_UI:
                mDataCameraControl.startInitDevices();
                break;

            case MSG_REMOVE_PB_UI:
                int i = arg0.arg1;
//			CameraControlNew.startPlayStreamTest(i, CameraControlNew.STATUS_SWITCH_NONE);
                CameraControlNew.startPlayStreamTest(CameraControlNew.STATUS_SWITCH_NONE);
                mHandler.sendEmptyMessageDelayed(MSG_LOAD_END, 300);
                DLog.e("----------------------iii = " + i);
                break;

            case MSG_LOAD_END:
                mUIFingerPrintControl.setLoadSuccessEnd();
                break;

            case MSG_REMOVE_PB_UI_TIME_OUT:
//			glSurfaceView.setBackgroundColor(color.transparent);
                mUIFingerPrintControl.setCameraTimeOutUI();
                break;

            case MSG_INIT_CAMERA_DATA:
                mDataCameraControl.initCameraFirstUI();
                break;

            case MSG_PLAY_STREAM_CALLBACK:
                int status = arg0.arg1;
                boolean refreshCameraStatusUILeft = mDataCameraControl.refreshCameraStatusUI(status);
                mUIFingerPrintControl.refreshGLSBackground(refreshCameraStatusUILeft);
                break;

            case CameraProviderFControl.MSG_PROVIDE_DATA_CHANGE:
                if (null != mCameraProviderFControl) {
                    mAllCameraStatus = mCameraProviderFControl.getAllCameraStatus();
                    CameraControlNew.setData(mAllCameraStatus);
                    int size = mAllCameraStatus.size();
                    for (int j = 0; j < size; j++) {
                        EntityCameraStatus entityCameraStatus = mAllCameraStatus.get(j);
                        receiveDeviceStatus(entityCameraStatus.getUserID(), entityCameraStatus.getStatus());
                    }
                }
                break;

            default:
                break;
        }
        return false;
    }

    @Subscribe
    public void getMessageCameraList(List<EntityDevice> list) {

    }

    @Override
    public void receiveDeviceStatus(long userid, int status) {
        int indexOf = CameraControlNew.mListUserId.indexOf(userid);
        DLog.e("-------------userid = " + userid + "  status = " + status + "  indexOf = " + indexOf);
        Message msg = handler.obtainMessage(indexOf, status, 0);
        handler.sendMessage(msg);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //成功连接 0--12--100
            //连接后在断 11--0--5
            //无电/无网的 情况，连接走0--5
            int size = mListTV_CameraStatus.size();
            int what = msg.what;
//			if(size <= what || what < 0){
//				DLog.e("-----------mListTV_CameraStatus = " + size + "  msg.what = " + what);
//				return;
//			}

            if (mDataCameraControl.getPosition() < 0) {
                mDataCameraControl.setPosition(0);
            }

            DLog.e("-----------mListTV_CameraStatus mPosition = " + mDataCameraControl.getPosition() + "  msg.what = " + what);
            if (what != -1) {
                mDataCameraControl.refreshCameraStatusInt(what, msg.arg1);
            }

            if (what != mDataCameraControl.getPosition()) {
                if (msg.arg1 == 100) {
//					Message obtainMessage = mHandler.obtainMessage(MSG_REMOVE_PB_UI, what, 0);
//					mHandler.sendMessageDelayed(obtainMessage, 2000);
                }
            } else {
                int wArg1 = msg.arg1;
                switch (wArg1) {
                    case CameraControlNew.CAMERA_STATUS_TIME_OUT:
                    case CameraControlNew.CAMERA_STATUS_ERROR:
                    case CameraControlNew.CAMERA_STATUS_OFF_LINE:
                    case CameraControlNew.CAMERA_STATUS_ERROR_ID:
                    case CameraControlNew.CAMERA_STATUS_BREAK_OFF:
                    case CameraControlNew.CAMERA_STATUS_USER_ERROR:
                        mListTV_CameraStatus.get(0).setText(mResources.getString(R.string.status_off_line));
                        break;
                    case CameraControlNew.CAMERA_STATUS_CONNECTING:
                        mListTV_CameraStatus.get(0).setText(mResources.getString(R.string.status_off_line));
//					mListTV_CameraStatus.get(0).setText(mResources.getString(R.string.status_connecting));
                        break;
                    case CameraControlNew.CAMERA_STATUS_CONNECTED:
                        mListTV_CameraStatus.get(0).setText(mResources.getString(R.string.status));
                        Message obtainMessage = mHandler.obtainMessage(MSG_REMOVE_PB_UI, what, 0);
                        mHandler.sendMessageDelayed(obtainMessage, 800);
                        break;
                    default:
                        break;
                }
            }

        }

    };

    private class MyICameraStatus implements ICameraStatus {

        @Override
        public void refreshStatus(String str) {
            // TODO Auto-generated method stub
            mListTV_CameraStatus.get(0).setText(str);
        }

        @Override
        public void refreshMainUI(int switchDirection) {
            // TODO Auto-generated method stub
            mHandler.removeMessages(MSG_PLAY_STREAM_CALLBACK);
            Message obtainMessage = mHandler.obtainMessage(MSG_PLAY_STREAM_CALLBACK, switchDirection, 0);
            mHandler.sendMessageDelayed(obtainMessage, 500);
        }
    }

    @Override
    public void initComplete(int size, int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void takePicture(byte[] imageBuffer, int width, int height) {
        // TODO Auto-generated method stub

    }

}
