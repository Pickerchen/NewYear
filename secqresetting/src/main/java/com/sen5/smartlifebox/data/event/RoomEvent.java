package com.sen5.smartlifebox.data.event;

/**
 * Created by ZHOUDAO on 2017/5/28.
 */

public class RoomEvent {

    public static final int NEW_ROOM = 0;
    public static final int DELETE_ROOM = 2;
    public static final int EDIT_ROOM = 3;
    public static final int LIST_ROOM =1;

    public RoomEvent(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    private int flag;
}
