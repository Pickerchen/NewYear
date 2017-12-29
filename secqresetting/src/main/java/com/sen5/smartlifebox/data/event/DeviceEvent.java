package com.sen5.smartlifebox.data.event;

import com.sen5.smartlifebox.data.entity.DeviceData;

/**
 * Created by ZHOUDAO on 2017/5/23.
 */

public class DeviceEvent {
    public int flag;
    public static final int DEV_LIST = 0;
    public static final int DEV_UPDATE = 1;
    public static final int DEV_ADD = 2;
    public static final int DEV_REMOVE = 3;
    public static final int DEV_MODE = 4;
    public static final int DEV_EDIT = 5;

    public DeviceData deviceData;


    public DeviceData getDeviceData() {
        return deviceData;
    }

    public void setDeviceData(DeviceData deviceData) {
        this.deviceData = deviceData;
    }

    public DeviceEvent(int flag) {
        this.flag = flag;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int position;




}
