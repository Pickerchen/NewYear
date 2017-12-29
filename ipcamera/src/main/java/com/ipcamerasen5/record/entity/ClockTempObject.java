package com.ipcamerasen5.record.entity;

/**
 * RecordService中临时存储闹钟变量结构体
 * Created by chenqianghua on 2017/4/28.
 */

public class ClockTempObject {
    private int duration;
    private String did;
    private String startTime;
    private boolean hasShowCloseToast;//当前闹钟已经显示过录制开关未打开的土司
    private boolean hasStartRecord;//用来判断该闹钟是否已经录制了，防止重复录制

    public boolean isHasShowCloseToast() {
        return hasShowCloseToast;
    }

    public void setHasShowCloseToast(boolean hasShowCloseToast) {
        this.hasShowCloseToast = hasShowCloseToast;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public boolean isHasStartRecord() {
        return hasStartRecord;
    }

    public void setHasStartRecord(boolean hasStartRecord) {
        this.hasStartRecord = hasStartRecord;
    }
}
