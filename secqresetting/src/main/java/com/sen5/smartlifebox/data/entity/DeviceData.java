package com.sen5.smartlifebox.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.sen5.smartlifebox.SecQreSettingApplication;
import com.sen5.smartlifebox.data.DeviceJudge;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import nes.ltlib.utils.AppLog;


public class DeviceData implements Parcelable, Cloneable {

    public static final int NO_ROOM_ID = 0;
    public static final int MODE_SENSOR = 0;
    public static final int MODE_ACTION = 1;
    public static final int MODE_FULL   = 2;
    public static final String DEFAULT_NAME = "_";

    private int dev_id;
    private String dev_type;
    private String name = DEFAULT_NAME;
    private int sec_away; //设防模式
    private int sec_stay;
    private int sec_disarm;
    private int room_id = NO_ROOM_ID;//0表示设备没有分配房间
    private String camera_id = "";//0表示设备没有分配房间

    private long update_time;//状态更新UTC时间,精确到毫秒
    private List<DeviceStatusData> status; //状态列表，存放多种不同类型的状态

    private int mode; //设备类型【0:sensor；1:action；2:full；3:unknown】,即将废弃

    public DeviceData() {

    }

    protected DeviceData(Parcel in) {
        dev_id = in.readInt();
        dev_type = in.readString();
        name = in.readString();
        sec_away = in.readInt();
        sec_stay = in.readInt();
        sec_disarm = in.readInt();
        room_id = in.readInt();
        camera_id = in.readString();
        update_time = in.readLong();
        status = in.createTypedArrayList(DeviceStatusData.CREATOR);
        mode = in.readInt();
    }

    public static final Creator<DeviceData> CREATOR = new Creator<DeviceData>() {
        @Override
        public DeviceData createFromParcel(Parcel in) {
            return new DeviceData(in);
        }

        @Override
        public DeviceData[] newArray(int size) {
            return new DeviceData[size];
        }
    };

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getSec_stay() {
        return sec_stay;
    }

    public void setSec_stay(int sec_stay) {
        this.sec_stay = sec_stay;
    }

    public int getSec_disarm() {
        return sec_disarm;
    }

    public void setSec_disarm(int sec_disarm) {
        this.sec_disarm = sec_disarm;
    }

    public int getSec_away() {
        return sec_away;
    }

    public void setSec_away(int sec_away) {
        this.sec_away = sec_away;
    }

    public int getDev_id() {
        return dev_id;
    }

    public void setDev_id(int dev_id) {
        this.dev_id = dev_id;
    }

    public String getDev_type() {
        return dev_type;
    }

    public void setDev_type(String dev_type) {
        this.dev_type = dev_type;
    }

    public String getName() {
        AppLog.d("---------------name = " + name);
        if(name.equals(DEFAULT_NAME)){
            int resId = DeviceJudge.getTypeName(dev_type);
            name = SecQreSettingApplication.mContext.getString(resId);
        }
        return name;
    }

    public void setName(String name) {
        try {
            name = URLDecoder.decode(name,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.name = name;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public String getCamera_id(){
        return camera_id;
    }

    public void setCamera_id(String camera_id){
        this.camera_id = camera_id;
    }

//    public int getIpc_did() {
//        return camera_id;
//    }
//
//    public void setIpc_did(int camera_id) {
//        this.camera_id = camera_id;
//    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }

    public List<DeviceStatusData> getStatus() {
        return status;
    }

    public void setStatus(List<DeviceStatusData> status) {
        this.status = status;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return super.clone();
    }

    @Override
    public String toString() {
        String str = "name:" + name
                + " dev_type:" + dev_type
                + " dev_id:" + dev_id
                + " room_id:" + room_id
                + " camera_id:" + camera_id
                + " update_time:" + update_time
                + " mode:" + mode;
        return str;

    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof DeviceData){
            if(((DeviceData)o).getDev_id() == this.dev_id){
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
        dest.writeInt(dev_id);
        dest.writeString(dev_type);
        dest.writeString(name);
        dest.writeInt(sec_away);
        dest.writeInt(sec_stay);
        dest.writeInt(sec_disarm);
        dest.writeInt(room_id);
        dest.writeString(camera_id);
        dest.writeLong(update_time);
        dest.writeTypedList(status);
        dest.writeInt(mode);
    }
}




