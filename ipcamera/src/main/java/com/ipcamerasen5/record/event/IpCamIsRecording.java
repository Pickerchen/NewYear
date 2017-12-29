package com.ipcamerasen5.record.event;

/**
 * Created by chenqianghua on 2017/5/2.
 */

public class IpCamIsRecording {
     boolean isRecording;
    String did;

    public IpCamIsRecording(boolean isRecording) {
        this.isRecording = isRecording;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }
}
