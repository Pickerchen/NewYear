/*
 * Copyright (c) belongs to sen5.
 */

package com.sen5.smartlifebox.data.event;

/**
 * Fragment回退时间
 * Created by wanglin on 2016/10/26.
 */
public class BackFragmentEvent {
    public static final String IP_CAMERA_FRAGMENT = "IP_CAMERA_FRAGMENT";
    public static final String ADD_WIFI_CAMERA_FRAGMENT = "ADD_WIFI_CAMERA_FRAGMENT";
    public static final String ALL_WIFI_CAMERA_FRAGMENT = "ALL_WIFI_CAMERA_FRAGMENT";
    public static final String FIND_LAN_CAMERA_FRAGMENT = "FIND_LAN_CAMERA_FRAGMENT";
    public static final String MEMBERS_FRAGMENT = "MEMBERS_FRAGMENT";
    public static final String OTHER_FRAGMENT = "OTHER_FRAGMENT";
    public static final String EDIT_MEMBER_FRAGMENT = "EDIT_MEMBER_FRAGMENT";
    public static final String EDIT_CAMERA_FRAGMENT = "EDIT_CAMERA_FRAGMENT";
    public static final String DELETE_MEMBER_FRAGMENT = "DELETE_MEMBER_FRAGMENT";
    public static final String DELETE_CAMERA_FRAGMENT = "DELETE_CAMERA_FRAGMENT";
    public static final String ADD_CANCEL_FRAGMENT = "ADD_CANCEL_FRAGMENT";

    public static final String MANAGE_CONTACT_FRAGMENT = "MANAGE_CONTACT_FRAGMENT";

    private String mFlag;

    private boolean mSwitchFindLANCameraFragment = false;

    public BackFragmentEvent(String flag) {
        this.mFlag = flag;
    }

    public String getFlag() {
        return mFlag;
    }

    public boolean isSwitchFindLANCameraFragment() {
        return mSwitchFindLANCameraFragment;
    }

    public void setSwitchFindLANCameraFragment(boolean switchFindLANCameraFragment) {
        this.mSwitchFindLANCameraFragment = switchFindLANCameraFragment;
    }
}
