package com.ipcamerasen5.record.entity;

/**
 * 存储容器信息
 * Created by chenqianghua on 2017/5/15.
 */
public class StorageInfo {
    public String path;
    public String state;
    public boolean isRemoveable;

    public StorageInfo(String path) {
        this.path = path;
    }

    public boolean isMounted() {
        return "mounted".equals(state);
    }
}