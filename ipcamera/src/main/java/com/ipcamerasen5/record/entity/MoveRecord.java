package com.ipcamerasen5.record.entity;

/**
 * Created by chenqianghua on 2017/9/12.
 */

public class MoveRecord {
   private  boolean stop = false;
    int time = 0;

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
