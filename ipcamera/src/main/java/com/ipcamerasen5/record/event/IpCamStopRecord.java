package com.ipcamerasen5.record.event;

/**
 * 设备录制结束时通知更新UI：在recordpresent中进行发送
 * Created by chenqianghua on 2017/5/9.
 */

public class IpCamStopRecord {
    private String did;
    private String jpgPath;//停止录制时将path传递，方便mainActivity更改UI

    public String getJpgPath() {
        return jpgPath;
    }

    public void setJpgPath(String jpgPath) {
        this.jpgPath = jpgPath;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }
}
