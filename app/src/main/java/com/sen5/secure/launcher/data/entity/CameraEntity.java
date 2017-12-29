/*
 * Copyright (c) belongs to sen5.
 */

package com.sen5.secure.launcher.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wanglin on 2016/10/25.
 */
public class CameraEntity implements Parcelable {
    public String DeviceID;
    public String DeviceName;
    public String IP;

    public CameraEntity() {

    }


    public CameraEntity(String id) {
        DeviceID = id;
    }

    protected CameraEntity(Parcel in) {
        DeviceID = in.readString();
        DeviceName = in.readString();
        IP = in.readString();
    }

    public static final Creator<CameraEntity> CREATOR = new Creator<CameraEntity>() {
        @Override
        public CameraEntity createFromParcel(Parcel in) {
            return new CameraEntity(in);
        }

        @Override
        public CameraEntity[] newArray(int size) {
            return new CameraEntity[size];
        }
    };

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CameraEntity) {
            if (((CameraEntity) obj).getDeviceID().equals(DeviceID)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(DeviceID);
        dest.writeString(DeviceName);
        dest.writeString(IP);
    }


}
