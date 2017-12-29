package com.ipcamerasen5.record.db;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by chenqianghua on 2017/4/5.
 * 存放摄像头闹钟录制视频路径
 */

public class ClockPath extends DataSupport implements Serializable {
    private String did;
    private String clockPath;
    private String fileName;
    private String startTime;
    private String time;//该时间是在文件展示按照日期分类时需要：27/04/2017
    private String lastThumpPath;//视频缩略图地址


    public String getLastThumpPath() {
        return lastThumpPath;
    }

    public void setLastThumpPath(String lastThumpPath) {
        this.lastThumpPath = lastThumpPath;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    private String duration;

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getClockPath() {
        return clockPath;
    }

    public void setClockPath(String clockPath) {
        this.clockPath = clockPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
