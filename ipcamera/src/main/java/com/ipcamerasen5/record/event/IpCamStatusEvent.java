package com.ipcamerasen5.record.event;

/**
 * 设备状态分发：在recordActivity中进行发送
 * Created by chenqianghua on 2017/4/7.
 */

public class IpCamStatusEvent {
    private long userId;
    private int status;

    public IpCamStatusEvent(long userId, int status) {
        this.userId = userId;
        this.status = status;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
