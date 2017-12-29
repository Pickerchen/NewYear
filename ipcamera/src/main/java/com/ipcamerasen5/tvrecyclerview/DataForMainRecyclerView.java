package com.ipcamerasen5.tvrecyclerview;

/**
 * Created by chenqianghua on 2017/7/7.
 */

public class DataForMainRecyclerView {
    private String did;
    private String name = "defaultName";
    private String status = "2";//状态：在线，离线，录制
    private String videoPath;//缩略图地址

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
