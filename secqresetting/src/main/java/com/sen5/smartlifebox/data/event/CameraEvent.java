/*
 * Copyright (c) belongs to sen5.
 */

package com.sen5.smartlifebox.data.event;

/**
 * IP Camera操作事件
 * Created by wanglin on 2016/10/26.
 */
public class CameraEvent {



    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    private long status;

    public String getGid() {
        return Gid;
    }

    public void setGid(String gid) {
        Gid = gid;
    }

    private String Gid;

    public static final int CAMERA_CONNECTING = 0;
    public static final int CAMERA_CONNECTED = 100;
    public static final int CAMERA_CONNECT_ERROR = 101;
    public static final int CAMERA_DISCONNECT = 11;
    public static final int CAMERA_CONNECT_TIME_OUT = 10;
    public static final int CAMERA_NO_ONLINE = 9;


    public static final int ADD_CAMERA = 5;
    public static final int DELETE_CAMERA = 1;
    public static final int CAMERA_RENAME = 2;
    public static final int CAMERA_RENAME_SUCCESS = 3;
    public static final int CAMERA_ONLINE = 4;

    private int flag;
    private long userId;//连接上camera后返回的id
    private String cameraDID;

    public CameraEvent(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getCameraDID() {
        return cameraDID;
    }

    public void setCameraDID(String cameraDID) {
        this.cameraDID = cameraDID;
    }
}
