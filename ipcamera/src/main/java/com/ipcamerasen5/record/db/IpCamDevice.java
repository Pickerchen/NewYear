package com.ipcamerasen5.record.db;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by chenqianghua on 2017/4/5.
 * 关系映射表，所有私有成员都会被映射过去。
 * 数据库内容是新旧摄像头通用的
 */
public class IpCamDevice extends DataSupport implements Serializable {
    private String did;
    private String aliasName;
    private String lastRecordPath;
    private String thumpPath;//存放视频缩略图的位置
    private String lastJPGPath;//最后一张图片位置
    private String status;//0:设备掉线了 1:设备在线 2:设备正在录制
    private String roundPath;//24小时录制路径
    private String secondPath;//十秒视频录制路径
    private String sensorPath;//sensor触发路径
    private String clockPath;//闹钟路径
    private String clockTime;//闹钟时间
    private String dynamicPath;//动态路径
    private String intelligentPath;//智能录制路径
    private String isStop = "2";//1：用户手动暂停(设备为不用录制状态,哪怕到了闹钟也不进行录制) 2：恢复录制:默认值为2
    private String secondIsOpen;//1：十秒小视频关闭 2：十秒小视频开启
    private String recordType = "";//0:闹钟录制 1:24小时录制 2:智能录制(sensor触发录制)3:动态录制(人脸识别)


    public String getSecondIsOpen() {
        return secondIsOpen;
    }

    public void setSecondIsOpen(String secondIsOpen) {
        this.secondIsOpen = secondIsOpen;
    }

    public String getIsStop() {
        return isStop;
    }

    public void setIsStop(String isStop) {
        this.isStop = isStop;
    }

    public String getLastJPGPath() {
        return lastJPGPath;
    }

    public void setLastJPGPath(String lastJPGPath) {
        this.lastJPGPath = lastJPGPath;
    }

    public String getThumpPath() {
        
        return thumpPath;
    }

    public void setThumpPath(String thumpPath) {
        this.thumpPath = thumpPath;
    }

    public String getLastRecordPath() {
        return lastRecordPath;
    }

    public void setLastRecordPath(String lastRecordPath) {
        this.lastRecordPath = lastRecordPath;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getRoundPath() {
        return roundPath;
    }

    public void setRoundPath(String roundPath) {
        this.roundPath = roundPath;
    }

    public String getSecondPath() {
        return secondPath;
    }

    public void setSecondPath(String secondPath) {
        this.secondPath = secondPath;
    }

    public String getSensorPath() {
        return sensorPath;
    }

    public void setSensorPath(String sensorPath) {
        this.sensorPath = sensorPath;
    }

    public String getClockPath() {
        return clockPath;
    }

    public void setClockPath(String clockPath) {
        this.clockPath = clockPath;
    }

    public String getDynamicPath() {
        return dynamicPath;
    }

    public void setDynamicPath(String dynamicPath) {
        this.dynamicPath = dynamicPath;
    }

    public String getIntelligentPath() {
        return intelligentPath;
    }

    public void setIntelligentPath(String intelligentPath) {
        this.intelligentPath = intelligentPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
