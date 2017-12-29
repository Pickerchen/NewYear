package com.sen5.smartlifebox.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by wanglin on 2016/6/28.
 */

public class ModeData implements Parcelable {

    public static final int NO_MOTION_FALSE = 0;
    public static final int NO_MOTION_TRUE = 1;

    // 设防模式
    public static final int MODE_AWAY 	    = 1;
    public static final int MODE_STAY 	    = 2;
    public static final int MODE_DISARM 	= 3;

    private int sec_mode;
    private int no_motion;
    private List<Integer> dev_list; //当前模式起作用的Sensor

    public ModeData() {
    }

    protected ModeData(Parcel in) {
        sec_mode = in.readInt();
        no_motion = in.readInt();
    }

    public static final Creator<ModeData> CREATOR = new Creator<ModeData>() {
        @Override
        public ModeData createFromParcel(Parcel in) {
            return new ModeData(in);
        }

        @Override
        public ModeData[] newArray(int size) {
            return new ModeData[size];
        }
    };

    public List<Integer> getDev_list() {
        return dev_list;
    }

    public void setDev_list(List<Integer> dev_list) {
        this.dev_list = dev_list;
    }

    public int getSec_mode() {
        return sec_mode;
    }

    public void setSec_mode(int sec_mode) {
        this.sec_mode = sec_mode;
    }

    public int getNo_motion() {
        return no_motion;
    }

    public void setNo_motion(int no_motion) {
        this.no_motion = no_motion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sec_mode);
        dest.writeInt(no_motion);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof ModeData){
           if( ((ModeData)o).getSec_mode() == this.sec_mode){
               return true;
           }
        }
        return false;
    }
}
