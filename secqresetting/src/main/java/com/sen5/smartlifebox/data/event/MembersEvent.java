package com.sen5.smartlifebox.data.event;

/**
 * 成员操作事件
 * Created by wanglin on 2016/10/13.
 */

public class MembersEvent {
    public static final int LIST_USER         = 0;
    public static final int ADD_USER          = 1;
    public static final int DELETE_USER       = 2;
    public static final int REQUEST_ADD_USER  = 3;
    public static final int EDIT_USER         = 4;
    public static final int RENAME_USER       = 5;
    public static final int IDENTITY_LEGAL    = 6;
    public static final int IDENTITY_NOT_LEGAL    = 7;

    private int flag;
    private String addUserId;

    public MembersEvent(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public String getAddUserId() {
        return addUserId;
    }

    public void setAddUserId(String addUserId) {
        this.addUserId = addUserId;
    }
}
