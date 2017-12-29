package com.ipcamerasen5.record.event;

/**
 * Created by chenqianghua on 2017/7/27.
 */

public class IpCamMoveEvent {
    public String did;
    private int duration;

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }

    private boolean isRecording;

    private  boolean stop = true;
    int time = 0;

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int status;//0：开始录制 1：停止录制

    public IpCamMoveEvent(String did) {
        this.did = did;
    }
}
