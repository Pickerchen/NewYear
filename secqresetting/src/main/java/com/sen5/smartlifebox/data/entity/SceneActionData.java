package com.sen5.smartlifebox.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class SceneActionData implements Parcelable {

    public static final int ACTION_SWITCH = 1;//开关

    private int dev_id;
    /**
     * 设备动作
     */
    private int action_id;
    private byte[] action_params;

    public SceneActionData() {
    }

    protected SceneActionData(Parcel in) {
        dev_id = in.readInt();
        action_id = in.readInt();
        action_params = in.createByteArray();
    }

    public int getDev_id() {
        return dev_id;
    }

    public void setDev_id(int dev_id) {
        this.dev_id = dev_id;
    }

    public int getAction_id() {
        return action_id;
    }

    public void setAction_id(int action_id) {
        this.action_id = action_id;
    }

    public byte[] getAction_params() {

        return action_params;
    }

    public void setAction_params(byte[] action_params) {

        this.action_params = action_params;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dev_id);
        dest.writeInt(action_id);
        dest.writeByteArray(action_params);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SceneActionData> CREATOR = new Creator<SceneActionData>() {
        @Override
        public SceneActionData createFromParcel(Parcel in) {
            return new SceneActionData(in);
        }

        @Override
        public SceneActionData[] newArray(int size) {
            return new SceneActionData[size];
        }
    };

    @Override
    public boolean equals(Object o) {

        if (o instanceof SceneActionData) {
            SceneActionData object = (SceneActionData) o;
            if (object.getDev_id() == this.dev_id ) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
