package com.sen5.secure.launcher.data.control;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.eric.xlee.lib.utils.LogUtil;
import com.sen5.secure.launcher.R;
import com.sen5.secure.launcher.data.database.CameraProviderControl;
import com.sen5.secure.launcher.utils.MyRender;
import com.sen5.secure.launcher.widget.CameraView;
import com.sen5.smartlifebox.data.event.CameraEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import hsl.p2pipcam.nativecaller.DeviceSDK;
import nes.ltlib.data.CameraEntity;
import nes.ltlib.utils.AppLog;

/**
 * Created by ZHOUDAO on 2017/8/30.
 */

public class OldCameraManger {

    private Context mContext;
    private CameraRunnable cameraRunnable;
    private int delay = 30000;

    private boolean isPause;
    private boolean isBackground;

    private LinearLayout mLlCamera;
    private View.OnFocusChangeListener onCameraFocusChangeListener;
    private View.OnClickListener onCameraClickListener;
    private View.OnKeyListener onCameraKeyListener;

    private ArrayList<CameraEntity> cameraEntities;


    public ArrayList<CameraView> getCameraList() {
        return cameraList;
    }

    private ArrayList<CameraView> cameraList;

    public OldCameraManger(Context mContext) {
        this.mContext = mContext;
        EventBus.getDefault().register(this);
    }

    /**
     * 初始化CameraView
     *
     * @param mLlCamera
     * @param onCameraFocusChangeListener
     * @param onCameraClickListener
     * @param onCameraKeyListener
     */
    public void init(LinearLayout mLlCamera, View.OnFocusChangeListener onCameraFocusChangeListener, View.OnClickListener onCameraClickListener, View.OnKeyListener onCameraKeyListener) {

        this.mLlCamera = mLlCamera;
        this.onCameraClickListener = onCameraClickListener;
        this.onCameraFocusChangeListener = onCameraFocusChangeListener;
        this.onCameraKeyListener = onCameraKeyListener;
        cameraList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {

            //创建CameraView
            CameraView cameraView = new CameraView(mContext);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(((int) mContext.getResources().getDimension(R.dimen.dimen_290)), ((int) mContext.getResources().getDimension(R.dimen.dimen_163)));
            cameraView.setRender(new MyRender(cameraView.mGlSurfaceView));
            cameraView.setLayoutParams(lp);
            cameraView.setFocusable(true);
            cameraView.setOnFocusChangeListener(onCameraFocusChangeListener);
            cameraView.setOnClickListener(onCameraClickListener);
            cameraView.setOnKeyListener(onCameraKeyListener);
            mLlCamera.addView(cameraView);


            cameraList.add(cameraView);
        }


    }

    /**
     * 初始化Camera
     *
     * @param cameraEntities
     */
    public void initCamera(ArrayList<CameraEntity> cameraEntities) {
        this.cameraEntities = cameraEntities;
        for (int i = 0; i < Math.min(cameraEntities.size(), 4); i++) {
            //初始化Camera
            CameraView cameraView = cameraList.get(i);
            cameraView.setTag(cameraEntities.get(i).getDeviceID());
            cameraView.initDevices(cameraEntities.get(i).getDeviceID());
        }


        CameraProviderControl.getInstance().initCameraData(mContext, cameraEntities, cameraList);
    }

    /**
     * 增加Camera
     *
     * @param did
     */
    public void addCamera(String did) {
        int size = 0;
        if (cameraEntities == null) {
            cameraEntities = new ArrayList<>();
        }
        if (!cameraEntities.isEmpty()) {
            size = cameraEntities.size();
        }


        CameraView cameraView = cameraList.get(size);
        cameraView.initDevices(did);
        CameraEntity cameraEntity = new CameraEntity();
        cameraEntity.setDeviceID(did);
        cameraView.setTag(did);
        cameraEntities.add(cameraEntity);


        Log.e("增加摄像头", "mUserId=" + cameraView.mUserid + " mRecordId= " + cameraView.mRecordId);

        try {
            CameraProviderControl.getInstance().setCameraData(mContext, did, cameraView.mUserid, -1, CameraProviderControl.CAMERA_MODE_PLAY);
            CameraProviderControl.getInstance().setCameraData(mContext, did, cameraView.mRecordId, -1, CameraProviderControl.CAMERA_MODE_RECORD);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteCamera(String did) {
        for (int i = cameraList.size() - 1; i >= 0; i--) {
            if (did.equals(cameraList.get(i).mDid)) {
                CameraView cameraView = cameraList.get(i);
                cameraView.stopPlayStream();
                cameraView.closeDevice();
                cameraView.destroyDevice();
                cameraView.setCameraStatusText(CameraView.status.other);
                cameraList.remove(i);
                mLlCamera.removeView(cameraView);
                cameraEntities.remove(i);

                CameraView cameraViewNew = new CameraView(mContext);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(((int) mContext.getResources().getDimension(R.dimen.dimen_290)), ((int) mContext.getResources().getDimension(R.dimen.dimen_163)));
                cameraViewNew.setRender(new MyRender(cameraViewNew.mGlSurfaceView));
                cameraViewNew.setLayoutParams(lp);
                cameraViewNew.setFocusable(true);
                cameraViewNew.setOnFocusChangeListener(onCameraFocusChangeListener);
                cameraViewNew.setOnClickListener(onCameraClickListener);
                cameraViewNew.setOnKeyListener(onCameraKeyListener);
                // TODO: 2017/6/28 Lund.zhou 增加默认图
                cameraViewNew.mFlDef.setVisibility(View.VISIBLE);
                mLlCamera.addView(cameraViewNew);
                cameraList.add(cameraViewNew);
                try {
                    CameraProviderControl.getInstance().deleteData(mContext, did);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }

    }

    public void playAll() {

        for (CameraView cameraView : cameraList) {
            cameraView.startPlay();
        }
    }

    /**
     * 重连机制 start
     */
    private Handler mCameraHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            for (CameraView cameraView : getCameraList()) {
                if (cameraView.mUserid == msg.what) {
                    cameraView.restartDevices(0);

                    AppLog.e("重连机制：userId =" + cameraView.mUserid + " did:" + cameraView.mDid);
                    break;
                } else if (cameraView.mRecordId == msg.what) {
                    cameraView.restartDevices(1);
                    AppLog.e("重连机制：RecordId =" + cameraView.mRecordId + " did:" + cameraView.mDid);
                    break;
                }
            }
        }
    };


    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    public class CameraRunnable implements Runnable {


        private long userId;

        public CameraRunnable(long userId) {
            this.userId = userId;
        }

        @Override
        public void run() {

            int status = getCameraStatusByUserId(userId);

            if (status == -1 || status == 100) {
                return;
            }


            long start = System.currentTimeMillis();
            int i = 0;
            while (i != 1) {
                i = DeviceSDK.destoryDevice(userId);

                if (System.currentTimeMillis() - start > 30000) {
                    break;
                }

            }

            AppLog.e("重连机制：注销实例  userId = " + userId);
            mCameraHandler.sendEmptyMessage((int) userId);
        }
    }


    /**
     * 获取摄像头状态
     *
     * @param userId
     * @return
     */
    private int getCameraStatusByUserId(long userId) {
        for (CameraView cameraView : getCameraList()) {
            if (cameraView.mUserid == userId) {
                return cameraView.playCode;
            } else if (cameraView.mRecordId == userId) {
                return cameraView.recordCode;
            }
        }
        return -1;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CameraEvent event) {


        switch (event.getFlag()) {
            case CameraEvent.CAMERA_CONNECTED:


                for (int i = 0; i < getCameraList().size(); i++) {

                    CameraView cameraView = getCameraList().get(i);

                    if (event.getUserId() == cameraView.mUserid) {
                        if (!isPause)
                            cameraView.startPlay();
                        cameraView.setCameraStatusText(CameraView.status.online);
                        cameraView.playCode = (int) event.getStatus();
                        CameraProviderControl.getInstance().setCameraData(mContext, cameraView.mDid, event.getUserId(), (int) event.getStatus(), CameraProviderControl.CAMERA_MODE_PLAY);
                        break;
                    }
                    if (event.getUserId() == cameraView.mRecordId) {
                        AppLog.e("DeviceSDK 事件回调  userID=" + event.getUserId() + "  nType=" + event.getStatus() + "  did:" + cameraView.mDid);
                        CameraProviderControl.getInstance().setCameraData(mContext, cameraView.mDid, event.getUserId(), (int) event.getStatus(), CameraProviderControl.CAMERA_MODE_RECORD);
                        cameraView.recordCode = (int) event.getStatus();
                        break;
                    }
                }

                break;

            case CameraEvent.CAMERA_CONNECTING:

                for (int i = 0; i < getCameraList().size(); i++) {

                    CameraView cameraView = getCameraList().get(i);

                    if (event.getUserId() == cameraView.mUserid) {
                        AppLog.e("DeviceSDK 事件回调  userID=" + event.getUserId() + "  nType=" + event.getStatus() + "  did:" + cameraView.mDid);
                        CameraProviderControl.getInstance().setCameraData(mContext, cameraView.mDid, event.getUserId(), (int) event.getStatus(), CameraProviderControl.CAMERA_MODE_PLAY);
                        cameraView.setCameraStatusText(CameraView.status.connecting);
                        cameraView.playCode = (int) event.getStatus();
                        break;
                    }

                    if (event.getUserId() == cameraView.mRecordId) {
                        AppLog.e("DeviceSDK 事件回调  userID=" + event.getUserId() + "  nType=" + event.getStatus() + "  did:" + cameraView.mDid);
                        CameraProviderControl.getInstance().setCameraData(mContext, cameraView.mDid, event.getUserId(), (int) event.getStatus(), CameraProviderControl.CAMERA_MODE_RECORD);
                        cameraView.recordCode = (int) event.getStatus();
                        break;
                    }
                }


                break;
            case CameraEvent.CAMERA_CONNECT_ERROR:
            case CameraEvent.CAMERA_CONNECT_TIME_OUT:
            case CameraEvent.CAMERA_DISCONNECT:
            case CameraEvent.CAMERA_NO_ONLINE:
                for (int i = 0; i < getCameraList().size(); i++) {

                    final CameraView cameraView = getCameraList().get(i);


                    if (event.getUserId() == cameraView.mUserid) {

                        CameraProviderControl.getInstance().setCameraData(mContext, cameraView.mDid, event.getUserId(), (int) event.getStatus(), CameraProviderControl.CAMERA_MODE_PLAY);

                        cameraView.setCameraStatusText(CameraView.status.offline);
                        cameraView.stopPlayStream(CameraView.Mode.play);
                        cameraView.playCode = (int) event.getStatus();

                        AppLog.e("停止的CameraView:userId =" + cameraView.mUserid + " Did =" + cameraView.mDid);


                        cameraRunnable = new CameraRunnable(cameraView.mUserid);

                        executorService.schedule(cameraRunnable, delay + i * 1000, TimeUnit.MILLISECONDS);

                        break;
                    }

                    if (event.getUserId() == cameraView.mRecordId) {
                        CameraProviderControl.getInstance().setCameraData(mContext, cameraView.mDid, event.getUserId(), (int) event.getStatus(), CameraProviderControl.CAMERA_MODE_RECORD);


                        cameraView.recordCode = (int) event.getStatus();
                        cameraView.stopPlayStream(CameraView.Mode.record);
                        AppLog.e("停止的CameraView:userId =" + cameraView.mRecordId + " Did =" + cameraView.mDid);

                        cameraRunnable = new CameraRunnable(cameraView.mRecordId);

                        executorService.schedule(cameraRunnable, delay + i * 1000, TimeUnit.MILLISECONDS);


                        break;
                    }
                }

                break;

        }
    }

    /**
     * 重连机制 end
     */


    public void setPause(boolean pause) {
        isPause = pause;
    }


    public void setBackground(boolean background) {
        isBackground = background;
    }


}
