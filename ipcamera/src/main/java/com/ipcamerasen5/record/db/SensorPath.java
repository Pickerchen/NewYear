package com.ipcamerasen5.record.db;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by chenqianghua on 2017/4/5.
 * 由sensor触发的录制视频保存地址
 */

public class SensorPath extends DataSupport implements Serializable {
    private String did;
    private String sensorPath;//存储当前摄像头sensor触发的绝对路径
    private String fileName;//文件名
    private String time;//该时间是在文件展示按照日期分类时需要：27/04/2017
    private String duration;//录制时长
    private String lastThumpPath;//视频缩略图地址

    public String getLastThumpPath() {
        return lastThumpPath;
    }

    public void setLastThumpPath(String lastThumpPath) {
        this.lastThumpPath = lastThumpPath;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getSensorPath() {
        return sensorPath;
    }

    public void setSensorPath(String sensorPath) {
        this.sensorPath = sensorPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
