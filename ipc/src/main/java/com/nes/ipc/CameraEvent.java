package com.nes.ipc;

/**
 * Created by zhoudao on 2017/5/19.
 */

public class CameraEvent {

    public Long getUserId() {
        return UserId;
    }

    public void setUserId(Long userId) {
        UserId = userId;
    }

    private Long UserId;

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


    public CameraEvent(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    private int flag;
}
