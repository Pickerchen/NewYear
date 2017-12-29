package com.ipcamera.main.control;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.ipcamera.main.R;
import com.ipcamera.main.service.FingerPrintViewService;
import com.ipcamera.main.utils.ClickFastUtil;
import com.ipcamera.main.utils.DLog;
import com.ipcamera.main.utils.UtilsStartActivityCustom;
import com.ipcamera.main.utils.UtilsToast;
import com.ipcamera.main.utils.UtilsToastDebug;
import com.ipcamera.main.view.FloatDynamicWave;
import com.ipcamera.main.view.LTCameraUI;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import hsl.p2pipcam.entity.EntityCameraStatus;
import nes.ltlib.data.LTDevice;
import nes.ltlib.LTSDKManager;

/**
 * Created by ZHOUDAO on 2017/9/4.
 */

public class LTCameraControl implements View.OnClickListener, View.OnKeyListener, Handler.Callback {

    private Context mContext;
    private CameraProviderFControl mCameraProviderFControl;
    private List<EntityCameraStatus> mAllCameraStatus;
    private List<LTDevice> mAllLtDevices;
    private static final int DEFAULT_HIDE_VIEW_TIEM = 8000;

    private static final int MSG_HIDE_MAIN_VIEW = 10;
    private static final String ACTION_RECEIVER_CLOSE_SERVICE = "com.sen5.process.camera.close.float";


    private int curPosition = -1;
    private String curDid = "";

    private LTCameraUI ltCameraUI;

    private UIFloatViewControl mUIFloatViewControl;

    private Handler mHandler;

    public LTCameraControl(Context context, String did) {
        mContext = context;

        mHandler = new Handler(this);

        mCameraProviderFControl = new CameraProviderFControl(context, mHandler);

        mAllCameraStatus = mCameraProviderFControl.getAllCameraStatus();

        mAllLtDevices = LTSDKManager.mLTDevices;

        DLog.i("LtDevices" + LTSDKManager.mLTDevices.size());

        ltCameraUI = new LTCameraUI(context);

        ltCameraUI.setMenuFrameKeyListener(this);
        ltCameraUI.setMenuFrameClickListener(this);


        mUIFloatViewControl = new UIFloatViewControl(mContext);
        mUIFloatViewControl.setView(ltCameraUI.getLtView());
        mUIFloatViewControl.setSplitScreen(mContext, 2.6f, true);


        if (TextUtils.isEmpty(did)) {
            playCameraPosition(0);
        } else {
            playByGid(did);

        }

    }


    public void playByGid(String gid) {
        if (mAllLtDevices == null || mAllLtDevices.isEmpty()) {
            return;
        }

        for (int i = 0; i < mAllLtDevices.size(); i++) {
            if (gid.equals(mAllLtDevices.get(i).getGid())) {
                playCameraPosition(i);
                return;
            }
        }
    }


    private void playCameraPosition(int i) {
        if (mAllLtDevices == null || mAllLtDevices.isEmpty()) {
            return;
        }

        if (i < 0 || i > mAllLtDevices.size() - 1) {
            return;
        }


        if (i != curPosition) {
            ltCameraUI.removeCameraView();
        } else {
            return;
        }

        LTDevice ltDevice = mAllLtDevices.get(i);
        curDid = ltDevice.getGid();

        ltCameraUI.addCameraView(ltDevice);


        curPosition = i;
    }

    public void showView(int location, int offsetX, int offsetY, float alpha) {
        mUIFloatViewControl.showView(location, offsetX, offsetY, alpha);
    }


    public void requestFocus() {
        mUIFloatViewControl.requestFocus();
        ltCameraUI.getmFlmenu().showView(false);
    }


    public void closeFocus() {
        mUIFloatViewControl.clearFocus();
        ltCameraUI.getmFlmenu().hideView();
    }

    public void refreshData() {

        mAllCameraStatus = mCameraProviderFControl.getAllCameraStatus();
        for (EntityCameraStatus status : mAllCameraStatus) {
            if (status.getDid().equals(curDid)) {
                ltCameraUI.refreshStatu(status.getStatus());
                return;
            }
        }


    }


    public void closeAll(boolean isSendCloseBroadcast) {
        FloatDynamicWave.WAVE_START = false;
        mCameraProviderFControl.unRegister();

        if (isSendCloseBroadcast) {
            sendCloseBroadcast(mContext);

        }

        if (ltCameraUI != null)
            ltCameraUI.removeCameraView();

        closeService();
    }


    private void sendCloseBroadcast(Context context) {
        Intent intent = new Intent();
        intent.setAction(ACTION_RECEIVER_CLOSE_SERVICE);
//		intent.setPackage("com.sen5.klauncher");
        context.sendBroadcast(intent);
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
        if (v.getId() == R.id.ll_left) {
            if (!ClickFastUtil.clickFast()) {
                playCameraPosition(curPosition - 1);

            }
        } else if (v.getId() == R.id.ll_right) {
            if (!ClickFastUtil.clickFast()) {
                playCameraPosition(curPosition + 1);
            }
        } else if (v.getId() == R.id.ll_fullscren) {
            if (TextUtils.isEmpty(curDid)) {
                UtilsToast.makeToast(mContext, R.string.no_camera).show();
                return;
            }
            UtilsStartActivityCustom.startFullCameraNew(mContext, curPosition, curDid);
            closeAll(false);

        } else if (v.getId() == R.id.ll_close) {
            UtilsToastDebug.showToastTest(mContext, "close");
            closeAll(true);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        mHandler.removeMessages(MSG_HIDE_MAIN_VIEW);
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_MAIN_VIEW, DEFAULT_HIDE_VIEW_TIEM);

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                closeFocus();
                break;

            default:
                break;
        }

        return false;
    }

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            case MSG_HIDE_MAIN_VIEW:

                closeFocus();
                break;

        }

        return false;
    }
}
