package com.ipcamerasen5.record.db;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by chenqianghua on 2017/4/5.
 * 摄像头录制闹钟
 */

public class RecordClock extends DataSupport implements Serializable {
    private String weekday;
    private String druration;
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

    private String time;//该时间是在文件展示按照日期分类时需要：27/04/2017
    private String did;
    private String hour;
    private String minutes;

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public String getDruration() {
        return druration;
    }

    public void setDruration(String druration) {
        this.druration = druration;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

}
