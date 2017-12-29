package com.ipcamerasen5.record.event;

/**
 * Created by chenqianghua on 2017/10/31.
 */

public class DeleteIpcameraEvent {
    private String did;

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public DeleteIpcameraEvent(String did) {
        this.did = did;
    }
}
