package com.ipcamerasen5.record.event;

/**
 * 设备闹钟响应但是开关未打开时发送的事件
 * 用来更新recordPresent中的currentduration
 * Created by chenqianghua on 2017/5/23.
 */

public class IpCamAlarmEvent_Switch_close {
    private String did;
    private int duration;

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
