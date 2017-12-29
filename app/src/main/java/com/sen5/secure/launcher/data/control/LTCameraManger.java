package com.sen5.secure.launcher.data.control;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.eric.xlee.lib.utils.LogUtil;
import com.sen5.secure.launcher.LauncherActivity;
import com.sen5.secure.launcher.R;
import com.sen5.secure.launcher.data.database.CameraProviderControl;
import com.sen5.secure.launcher.utils.Utils;
import com.sen5.secure.launcher.widget.LTCameraView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import glnk.io.OnDeviceStatusChangedListener;
import glnk.media.AViewRenderer;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DefaultObserver;
import nes.ltlib.data.CameraEntity;
import nes.ltlib.data.LTDevice;
import nes.ltlib.LTSDKManager;
import nes.ltlib.event.LTCameraEvent;
import nes.ltlib.event.LTSDKGlobalStatusEvent;
import nes.ltlib.utils.AppLog;

/**
 * Created by ZHOUDAO on 2017/8/30.
 */

public class LTCameraManger implements OnDeviceStatusChangedListener {

    /**
     * 新摄像头集合
     */
    ArrayList<LTCameraView> ltCameraViews = new ArrayList<>();
    private Context mContext;
    private LinearLayout mLlCamera;

    private View.OnFocusChangeListener onCameraFocusChangeListener;
    private View.OnClickListener onCameraClickListener;
    private View.OnKeyListener onCameraKeyListener;

    ArrayList<CameraEntity> mCameras = new ArrayList<>();

    ScheduledExecutorService mThreadPool = Executors.newScheduledThreadPool(4);


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_REPLAY:
                    Bundle bundle = msg.getData();
                    if (bundle != null) {
                        String gid = bundle.getString("gid");
                        AppLog.e("Replay");
                        LTCameraView lt = getLtCameraView(gid);
                        lt.setOnLine(false);
                        lt.setCameraStatusText(LTCameraView.UIStatus.connecting);
                    }
                    break;
                case MSG_RECONNECT:
                    bundle = msg.getData();
                    if (bundle != null) {
                        String gid = bundle.getString("gid");
                        play(gid);
                    }

                    break;
            }

        }
    };

    private static final int MSG_REPLAY = 0;
    private static final int MSG_RECONNECT = 1;

    public LTCameraManger(Context mContext) {
        this.mContext = mContext;
        EventBus.getDefault().register(this);
    }


    public void init(LinearLayout mLlCamera, View.OnFocusChangeListener onCameraFocusChangeListener, View.OnClickListener onCameraClickListener, View.OnKeyListener onCameraKeyListener) {

        this.mLlCamera = mLlCamera;
        this.onCameraClickListener = onCameraClickListener;
        this.onCameraFocusChangeListener = onCameraFocusChangeListener;
        this.onCameraKeyListener = onCameraKeyListener;
        for (int i = 0; i < 4; i++) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(((int) mContext.getResources().getDimension(R.dimen.dimen_290)), ((int) mContext.getResources().getDimension(R.dimen.dimen_163)));
            LTCameraView cameraView = new LTCameraView(mContext);
            cameraView.setLayoutParams(lp);
            cameraView.setFocusable(true);
            cameraView.setOnFocusChangeListener(onCameraFocusChangeListener);
            cameraView.setOnClickListener(onCameraClickListener);
            cameraView.setOnKeyListener(onCameraKeyListener);
            mLlCamera.addView(cameraView);
            ltCameraViews.add(cameraView);
        }

        setOnDeviceStatusChangedListener();
    }


    public void proGid(ArrayList<CameraEntity> cameraEntities) {
        mCameras = cameraEntities;
        for (int i = 0; i < cameraEntities.size(); i++) {
            //设置GID
            String gid = cameraEntities.get(i).getDeviceID();
            LTSDKManager.addGid(gid);
            ltCameraViews.get(i).setGid(gid);
            ltCameraViews.get(i).setCameraStatusText(LTCameraView.UIStatus.connecting);

            LTDevice ltDevice = new LTDevice();
            ltDevice.setGid(gid);
            LTSDKManager.getmLTDevices().add(ltDevice);


//            try {
//                CameraProviderControl.getInstance().setCameraData(mContext, gid, -1, -1, CameraProviderControl.CAMERA_MODE_PLAY);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

        }


    }

    public void playAll() {

        for (int i = 0; i < LTSDKManager.mLTDevices.size(); i++) {
            LTDevice ltDevice = LTSDKManager.mLTDevices.get(i);
            LTCameraView ltCameraView = ltCameraViews.get(i);
            AppLog.e("playAll ltDevice.getGid:" + ltDevice.getGid() + " ltCameraView.getGid:" + ltCameraView.getGid());
            if (ltDevice.getGid().equals(ltCameraView.getGid())) {

                if (ltCameraView != null) {
                    ltCameraView.addCameraView(ltDevice);
                    ltCameraView.setVideoSize();
                    ltCameraView.setTag(ltDevice.getGid());
                    ltCameraView.setLTCameraBg(R.color.bg_black_ap_30);
                    if (ltCameraView.getLtDevice().getAView() == null || !ltCameraView.isOnLine() || ltCameraView.getmUIStatus() == LTCameraView.UIStatus.offline) {
                        ltCameraView.setCameraStatusText(LTCameraView.UIStatus.offline);

                    } else {
                        ltCameraView.setCameraStatusText(LTCameraView.UIStatus.online);

                    }
                } else {
                    ltCameraView.setLTCameraBg(R.color.bg_black_ap_30);
                    ltCameraView.setCameraStatusText(LTCameraView.UIStatus.offline);
                }

            }

        }

        AppLog.e("playAll LT");
    }


    public void setOnDeviceStatusChangedListener() {
        LTSDKManager.getInstance().setDeviceStatusChangedListener(this);
    }


    public void play(String gid) {

        if (mHandler.hasMessages(MSG_REPLAY, gid)) {
            AppLog.i("存在有对应的离线重连 gid:" + gid);
            mHandler.removeMessages(MSG_REPLAY, gid);
            return;
        }

        //将LTCamera放入显示View中
        AppLog.e("play" + "ltDevices.size is " + LTSDKManager.mLTDevices.size());

        LTDevice ltDevice = null;

        LTCameraView ltCameraView = getViewByGid(gid);
        if (ltCameraView != null) {
            AppLog.i("play" + "删除原始的View");
            ltCameraView.removeLtDevice();
        } else {
            ltCameraView = getLtCameraView(gid);
            ltCameraView.setGid(gid);
        }

        //设置GID
        ltDevice = LTSDKManager.getInstance().createDevice(gid, mContext);


        if (ltCameraView != null) {

            /**
             * 如果不是IPCamera在栈顶就将CameraView添加到Launcher页面上
             */
            String packName = Utils.getActivityName(mContext);
            if (!TextUtils.isEmpty(packName) && !packName.contains("com.ipcamerasen5")) {
                ltCameraView.addCameraView(ltDevice);
            }

            AppLog.i("play----- topActivity:" + packName);

            ltCameraView.setVideoSize();
            ltCameraView.setTag(gid);
            ltCameraView.setOnLine(true);
            ltCameraView.setCameraStatusText(LTCameraView.UIStatus.online);

            ltDevice.getGlnkPlayer().start();
        }

//        LauncherActivity.isInitCamera = true;
    }

    public void replay(String gid) {

        Message msg = new Message();
        msg.what = MSG_REPLAY;
        msg.obj = gid;
        Bundle bundle = new Bundle();
        bundle.putString("gid", gid);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }


    public void addCamera(String gid) {

        AppLog.i("addCamera: gid :" + gid);
        //设置GID
        //将LTCamera放入显示View中
        LTCameraView ltCameraView = getLtCameraView(gid);
        LTSDKManager.addGid(gid);
        LTSDKManager.getInstance().addOneDevice(gid);

        if (ltCameraView != null) {
            ltCameraView.setGid(gid);
            ltCameraView.setCameraStatusText(LTCameraView.UIStatus.online);
            ltCameraView.setTag(gid);
            mCameras.add(new CameraEntity(gid));

            LTSDKManager.getInstance().setTimeZone(gid);

//            LTSDKManager.getInstance().setFrameRate(gid, 1, 10, new DefaultObserver<String>() {
//                @Override
//                public void onNext(@NonNull String s) {
//                    if (!s.equals("")) {
//                        AppLog.i("修改帧率成功");
//                    } else {
//                        AppLog.i("修改帧率失败");
//
//                    }
//                }
//
//                @Override
//                public void onError(@NonNull Throwable e) {
//                    AppLog.i("修改帧率error ：" + e.getMessage());
//                }
//
//                @Override
//                public void onComplete() {
//                    AppLog.i("修改帧率完成");
//                }
//            });

        }
    }

    public void deleteCamera(String gid) {
        for (int i = 0; i < mLlCamera.getChildCount(); i++) {
            LTCameraView cameraView = (LTCameraView) mLlCamera.getChildAt(i);
            AppLog.i("删除LT摄像头 cameraView_gid:" + cameraView.getGid() + "  gid:" + gid);
            if (!TextUtils.isEmpty(cameraView.getGid()) && gid.equals(cameraView.getGid())) {
                cameraView.removeLtDevice();
                mLlCamera.removeView(cameraView);
                ltCameraViews.remove(i);

                mCameras.remove(i);

                LTSDKManager.getInstance().deleteGid(gid);
                LTSDKManager.getInstance().deleteOneDevice(gid);


                for (Map<String, ScheduledFuture> map : futures) {
                    for (Map.Entry<String, ScheduledFuture> entry : map.entrySet()) {
                        if (entry.getKey().equals(gid)) {
                            ScheduledFuture future = entry.getValue();
                            future.cancel(true);
                            futures.remove(map);
                            break;
                        }
                    }
                }


                LTCameraView ltCameraView = createLtCameraView();
                ltCameraView.setGid("");

                mLlCamera.addView(ltCameraView);
                ltCameraViews.add(ltCameraView);
                return;
            }
        }
    }


    private LTCameraView createLtCameraView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(((int) mContext.getResources().getDimension(R.dimen.dimen_290)), ((int) mContext.getResources().getDimension(R.dimen.dimen_163)));
        LTCameraView cameraView = new LTCameraView(mContext);
        cameraView.setLayoutParams(lp);
        cameraView.setFocusable(true);
        cameraView.setOnFocusChangeListener(onCameraFocusChangeListener);
        cameraView.setOnClickListener(onCameraClickListener);
        cameraView.setOnKeyListener(onCameraKeyListener);
        return cameraView;
    }


    @Nullable
    private LTCameraView getViewByGid(String gid) {
        for (LTCameraView view : ltCameraViews) {
            if (view.getLtDevice() != null && gid.equals(view.getLtDevice().getGid())) {
                return view;
            }
        }
        return null;
    }


    @Nullable
    private LTCameraView getLtCameraView(String gid) {

        AppLog.i("添加LT摄像头 ");

        for (int i = 0; i < mCameras.size(); i++) {
            if (gid.equals(mCameras.get(i).getDeviceID())) {

                AppLog.i("添加LT摄像头 已存在Gid 直接取View 播放");

                return ltCameraViews.get(i);
            }
        }


        for (LTCameraView ltCameraView : ltCameraViews) {
            if (ltCameraView.getLtDevice() == null && TextUtils.isEmpty(ltCameraView.getGid())) {

                AppLog.i("添加LT摄像头 取未存在gid的view 播放");
                return ltCameraView;
            }
        }

        return null;
    }


    @Nullable
    private LTCameraView getReCameraView(String gid) {

        AppLog.i("添加LT摄像头 ");

        for (int i = 0; i < mCameras.size(); i++) {
            if (gid.equals(mCameras.get(i).getDeviceID())) {

                AppLog.i("添加LT摄像头 已存在Gid 直接取View 播放");

                return ltCameraViews.get(i);
            }
        }


        return null;
    }


    @Override
    public void onChanged(String gid, int status) {

        AppLog.i("LTSDK" + "onChanged----- gid:" + gid + "  UIStatus:" + status);

        LTSDKGlobalStatusEvent event = new LTSDKGlobalStatusEvent(gid, status);
        EventBus.getDefault().post(event);

        switch (status) {
            case 1:
            case 3:
                play(gid);
                break;
            case -2:
                replay(gid);
                break;

        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LTCameraEvent event) {

        if (TextUtils.isEmpty(event.getGid())) {
            return;
        }

//        try {
//            CameraProviderControl.getInstance().setCameraData(mContext, event.getGid(), -1, event.getStatus(), 0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        switch (event.getStatus()) {
            case LTCameraEvent.connected:
                getLtCameraView(event.getGid()).setCameraStatusText(LTCameraView.UIStatus.online);


                break;
            case LTCameraEvent.connecting:
                getLtCameraView(event.getGid()).setCameraStatusText(LTCameraView.UIStatus.connecting);


                break;
            case LTCameraEvent.disconnect:

                LTCameraView ltCameraView = getLtCameraView(event.getGid());

                if (ltCameraView.isOnLine()) {
                    play(event.getGid());
                    ltCameraView.setOnLine(false);

                } else {

                    ltCameraView.setCameraStatusText(LTCameraView.UIStatus.offline);

                    if (event.getError() == -4110 || event.getError() == -10) {
//                        Log.i("LTSDK", "重连准备中");
                        reConnect(event.getGid());
                    }

                }


                break;
            case LTCameraEvent.reconnecting:
                getLtCameraView(event.getGid()).setCameraStatusText(LTCameraView.UIStatus.connecting);
                break;


        }


    }


    @Override
    public void onPushSvrInfo(String s, String s1, int i) {

    }

    @Override
    public void onDevFunInfo(String s, String s1) {

    }

    @Override
    public void onStAuthResult(String s) {

    }

    @Override
    public void onStDevList(String s, String s1) {

    }


    private class ConnectRunnable implements Runnable {


        public ConnectRunnable(String gid) {
            this.gid = gid;
        }


        private String gid;

        @Override
        public void run() {
//            Log.e("重连", "重连进行中");

            try {
                LTCameraView ltCameraView = getReCameraView(gid);
//                Log.e("重连", "status：" + ltCameraView.getmUIStatus());
                if (ltCameraView != null && ltCameraView.getmUIStatus() != LTCameraView.UIStatus.online) {
//                    Log.e("重连", "开始重连");

                    Bundle bundle = new Bundle();
                    bundle.putString("gid", gid);

                    Message msg = new Message();
                    msg.what = MSG_RECONNECT;
                    msg.setData(bundle);

                    mHandler.sendMessage(msg);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    private void reConnect(String gid) {
//        mThreadPool.schedule(new ConnectRunnable(gid), 60, TimeUnit.SECONDS);

        for (Map<String, ScheduledFuture> map : futures) {
            for (Map.Entry<String, ScheduledFuture> entry : map.entrySet()) {
                if (entry.getKey().equals(gid)) {
                    return;
                }
            }
        }


        ScheduledFuture future = mThreadPool.scheduleAtFixedRate(new ConnectRunnable(gid), 10000, 10000, TimeUnit.MILLISECONDS);

        Map<String, ScheduledFuture> futureMap = new HashMap<>();
        futureMap.put(gid, future);
        futures.add(futureMap);

    }


    ArrayList<Map<String, ScheduledFuture>> futures = new ArrayList<>();
}
