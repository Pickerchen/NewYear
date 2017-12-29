package com.ipcamera.main.control;

import com.ipcamera.main.utils.Utils;

import java.util.List;

import nes.ltlib.data.CameraEntity;

/**
 * Created by ZHOUDAO on 2017/9/11.
 */

public class CameraSourceControl {

    private static CameraSourceControl control;

    public static CameraSourceControl getInstance() {
        if (control == null) {
            control = new CameraSourceControl();
            control.initCameraSource();
        }

        return control;
    }


    private List<CameraEntity> cameraSource;

    public List<CameraEntity> getCameraSource() {

        return cameraSource;
    }

    public List<CameraEntity> initCameraSource(){
        cameraSource = Utils.getCameraList();
        return cameraSource;
    }


    public int getCameraType() {
        int type = 0;

        if (cameraSource != null && !cameraSource.isEmpty()) {
            if (cameraSource.get(0).getDeviceID().startsWith("SLIFE")) {
                type = 1;
            }
        }

        return type;
    }


    public int getCameraType(List<CameraEntity> cameraSource) {
        int type = 0;

        if (cameraSource != null && !cameraSource.isEmpty()) {
            if (cameraSource.get(0).getDeviceID().startsWith("SLIFE")) {
                type = 1;
            }
        }

        return type;
    }

}
