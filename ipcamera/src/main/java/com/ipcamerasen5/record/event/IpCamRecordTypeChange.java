package com.ipcamerasen5.record.event;

/**
 * 设备录制的方式发生改变，改变时当前的录制需要停止。在recordsettingactivity中进行发送
 * Created by chenqianghua on 2017/5/9.
 */

public class IpCamRecordTypeChange {
    private String did;
    private int type;

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
}
