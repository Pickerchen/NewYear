package com.ipcamerasen5.record.event;

/**
 * 设备的录制开关发生变化
 * Created by chenqianghua on 2017/5/19.
 */

public class IpcamStopStatusChanges {
    private String did;
    private String isStop;//1:录制开关关闭 2：开关打开

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getIsStop() {
        return isStop;
    }

    public void setIsStop(String isStop) {
        this.isStop = isStop;
    }

    public IpcamStopStatusChanges(String did, String isStop) {
        this.did = did;
        this.isStop = isStop;
    }
}
