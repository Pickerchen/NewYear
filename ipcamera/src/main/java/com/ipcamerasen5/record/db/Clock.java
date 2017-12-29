package com.ipcamerasen5.record.db;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 存储闹钟
 * Created by chenqianghua on 2017/4/13.
 */

public class Clock extends DataSupport implements Serializable{
    //开始录制时间
    private String startTime;
    //结束时间
    private String endTime;
    //执行该录制闹钟的设备
    private String did;
    //周期
    private String times;//1,2,3,4,5,6,7,8:周一到每天(0表示该天不录制，8表示每天都进行录制)
    //录制时长
    private int duration;//统一使用毫秒值

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }
}
