package com.ipcamerasen5.record.entity;

/**
 * Created by chenqianghua on 2017/5/8.
 */

public class VideoFileEntity {

    private String deviceName;
    private String did;
    private String date;//    8/5/2017
    private String duration;//视频文件的时间，用分钟表示：12min
    private String videoFilePath; //视频文件路径

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getVideoFilePath() {
        return videoFilePath;
    }

    public void setVideoFilePath(String videoFilePath) {
        this.videoFilePath = videoFilePath;
    }

    public String getThumpPath() {
        return thumpPath;
    }

    public void setThumpPath(String thumpPath) {
        this.thumpPath = thumpPath;
    }

    private String thumpPath; //视频缩略图地址


    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
