package com.ipcamerasen5.record.db;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 存放十秒小视频的文件路径
 * Created by chenqianghua on 2017/4/13.
 */

public class SecondPath extends DataSupport implements Serializable {
    private String secondPath;
    private String fileName;
    private String did;
    private String time;//该时间是在文件展示按照日期分类时需要：27/04/2017
    private String duration;//录制时长
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

    public String getSecondPath() {
        return secondPath;
    }

    public void setSecondPath(String secondPath) {
        this.secondPath = secondPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }
}
