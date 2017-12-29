package com.ipcamerasen5.record.event;

/**
 * 闹钟事件分发，当闹钟开始响应时进行发送，在recordService中进行
 * Created by chenqianghua on 2017/4/13.
 */

public class IpCamAlarmEvent {
    private String did;
    private long duration;
    private int type;//录制的类型
    private String time;//时间，用于判断数据库中是否还存在该时间的闹钟.17:09
    private String repeats;//重复的周期

    public String getWhere() {
        return mWhere;
    }

    public void setWhere(String where) {
        mWhere = where;
    }

    private String mWhere;//从哪发出

    public IpCamAlarmEvent(String did, long duration) {
        this.did = did;
        this.duration = duration;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDid() {

        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
