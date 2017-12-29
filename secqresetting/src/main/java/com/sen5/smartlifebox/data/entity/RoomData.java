package com.sen5.smartlifebox.data.entity;

import java.util.List;

/**
 * Created by ZHOUDAO on 2017/5/23.
 */
public class RoomData {

    /**
     * dev_list : [43,47,48,49]
     * room_id : 2
     * room_name : lundroom
     */
    private int room_id;
    private String room_name;
    private List<Integer> dev_list;

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public List<Integer> getDev_list() {
        return dev_list;
    }

    public void setDev_list(List<Integer> dev_list) {
        this.dev_list = dev_list;
    }
}
