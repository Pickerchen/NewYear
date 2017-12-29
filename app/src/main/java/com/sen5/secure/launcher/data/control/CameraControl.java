package com.sen5.secure.launcher.data.control;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.eric.xlee.lib.utils.LogUtil;
import com.ipcamera.main.control.CameraSourceControl;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import nes.ltlib.LTSDKManager;
import nes.ltlib.data.CameraEntity;
import nes.ltlib.interf.CameraSearchListener;
import nes.ltlib.utils.AppLog;

/**
 * Created by ZHOUDAO on 2017/8/29.
 */

public class CameraControl {


    private Context mContext;


    private static CameraControl cameraControl;

    private LTCameraManger ltCameraManger;
    private OldCameraManger oldCameraManger;

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    private int type = 0;


    public static CameraControl getInstance() {
        if (cameraControl == null) {
            cameraControl = new CameraControl();
        }
        return cameraControl;
    }


    public CameraControl() {
    }


    public void init(Context mContext, LinearLayout mLlCamera, View.OnFocusChangeListener onCameraFocusChangeListener, View.OnClickListener onCameraClickListener, View.OnKeyListener onCameraKeyListener) {
        this.mContext = mContext;

        if (type == 0) {
            ltCameraManger = new LTCameraManger(mContext);
            ltCameraManger.init(mLlCamera, onCameraFocusChangeListener, onCameraClickListener, onCameraKeyListener);
        } else if (type == 1) {
            oldCameraManger = new OldCameraManger(mContext);
            oldCameraManger.init(mLlCamera, onCameraFocusChangeListener, onCameraClickListener, onCameraKeyListener);
        }


//
    }

    public void initCamera(ArrayList<CameraEntity> list) {

        if (type == 0) {
            ltCameraManger.proGid(list);
            for (CameraEntity entity : list) {
                if (LTSDKManager.getInstance().gids.indexOf(entity.getDeviceID()) == -1)
                    LTSDKManager.getInstance().gids.add(entity.getDeviceID());
            }

        } else if (type == 1) {
            oldCameraManger.initCamera(list);
        }

    }

    public void addCamera(String did) {

        if (type == 0) {

            ltCameraManger.addCamera(did);

        } else if (type == 1) {

            oldCameraManger.addCamera(did);

        }

    }


    public void deleteCamera(String did) {

        if (type == 0) {

            ltCameraManger.deleteCamera(did);

        } else if (type == 1) {

            oldCameraManger.deleteCamera(did);


        }
    }

    public void playAll() {


        if (type == 0) {

            ltCameraManger.playAll();

        } else if (type == 1) {

            oldCameraManger.playAll();


        }
    }


    ScheduledExecutorService mThreadPool = Executors.newSingleThreadScheduledExecutor();

    public void synchronousTime() {

        mThreadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {

                    updateTime();
                } catch (Exception e) {
                    AppLog.e("定时器：设置时间失败");
                    e.printStackTrace();
                }
            }

        }, 0, 6, TimeUnit.HOURS);


    }

    public void updateTime() {
        LTSDKManager.getInstance().setSearchListener(new CameraSearchListener() {
            @Override
            public void onSearchFinish(ArrayList<CameraEntity> list) {
                AppLog.e("定时器：开始同步时间");
                for (CameraEntity cameraEntity : CameraSourceControl.getInstance().getCameraSource()) {
                    LTSDKManager.getInstance().setTimeZone(cameraEntity.getDeviceID());
                }

            }
        });

        LTSDKManager.getInstance().searchCamera(3);
    }


}
