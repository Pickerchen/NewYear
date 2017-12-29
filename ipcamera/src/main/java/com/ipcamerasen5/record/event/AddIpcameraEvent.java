package com.ipcamerasen5.record.event;

/**
 * Created by chenqianghua on 2017/10/31.
 */

public class AddIpcameraEvent {
    private  String did;

    public AddIpcameraEvent(String did) {
        this.did = did;
    }

    public String getDid() {

        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }
}
