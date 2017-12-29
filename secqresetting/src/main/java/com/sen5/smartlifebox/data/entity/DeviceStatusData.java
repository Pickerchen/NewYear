package com.sen5.smartlifebox.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Created by wanglin on 2016/7/25.
 */

public class DeviceStatusData implements Parcelable {

    //设备状态参数
    public static final int SENSOR_STATUS_NO_WORK = 0;
    public static final int SENSOR_STATUS_SAFETY = 0;
    public static final int SENSOR_STATUS_DANGER = 1;

    private int id;        //设备状态ID
    private byte[] params; //状态参数


    private long time; //设备触发时间

    public DeviceStatusData() {
    }

    protected DeviceStatusData(Parcel in) {
        id = in.readInt();
        time = in.readLong();
        params = in.createByteArray();
    }

    public static final Creator<DeviceStatusData> CREATOR = new Creator<DeviceStatusData>() {
        @Override
        public DeviceStatusData createFromParcel(Parcel in) {
            return new DeviceStatusData(in);
        }

        @Override
        public DeviceStatusData[] newArray(int size) {
            return new DeviceStatusData[size];
        }
    };


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getParams() {
        return params;
    }

    public void setParams(byte[] params) {
        this.params = params;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(time);
        dest.writeByteArray(params);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DeviceStatusData) {
            if (((DeviceStatusData) obj).getId() == id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "id:" + id + "params: " +  Arrays.toString(params);
    }



}
