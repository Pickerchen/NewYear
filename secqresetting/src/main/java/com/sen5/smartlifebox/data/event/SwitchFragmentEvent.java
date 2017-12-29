package com.sen5.smartlifebox.data.event;

import com.sen5.smartlifebox.data.entity.ContactEntity;
import com.sen5.smartlifebox.data.entity.MemberEntity;

import nes.ltlib.data.CameraEntity;

/**
 * 新建Fragment，并跳转事件
 * Created by wanglin on 2016/9/1.
 */

public class SwitchFragmentEvent {
    public static final String IP_CAMERA_FRAGMENT = "IP_CAMERA_FRAGMENT";
    public static final String ADD_WIFI_CAMERA_FRAGMENT = "ADD_WIFI_CAMERA_FRAGMENT";

    public static final String SET_NEW_WIFI_CAMERA = "SET_NEW_WIFI_CAMERA";

    public static final String ALL_WIFI_CAMERA_FRAGMENT = "ALL_WIFI_CAMERA_FRAGMENT";
    public static final String FIND_LAN_CAMERA_FRAGMENT = "FIND_LAN_CAMERA_FRAGMENT";
    public static final String MEMBERS_FRAGMENT = "MEMBERS_FRAGMENT";
    public static final String EDIT_MEMBER_FRAGMENT = "EDIT_MEMBER_FRAGMENT";
    public static final String EDIT_CAMERA_FRAGMENT = "EDIT_CAMERA_FRAGMENT";
    public static final String DELETE_MEMBER_FRAGMENT = "DELETE_MEMBER_FRAGMENT";
    public static final String DELETE_CAMERA_FRAGMENT = "DELETE_CAMERA_FRAGMENT";
    public static final String ADD_CANCEL_FRAGMENT = "ADD_CANCEL_FRAGMENT";

    public static final String EMERGENCY_CONTACT_FRAGMENT = "EMERGENCY_CONTACT_FRAGMENT";
    public static final String MANAGE_CONTACT_FRAGMENT = "MANAGE_CONTACT_FRAGMENT";
    public static final String AFFIREM_DELETE_FRAGMENT = "AFFIREM_DELETE_FRAGMENT";
    public static final String EDIT_CONTACT_FRAGMENT = "EDIT_CONTACT_FRAGMENT";
    public static final String OTHER_FRAGMENT = "OTHER_FRAGMENT";
    public static final String NOTIFICATION_SYSTEM_CHOOSE = "NotificationSystemChooseFragment";

    private String flag;

    private MemberEntity member;
    private CameraEntity camera;
    private ContactEntity contact;

    public SwitchFragmentEvent(String flag) {
        this.flag = flag;
    }

    public String getFlag() {
        return flag;
    }

    public MemberEntity getMember() {
        return member;
    }

    public void setMember(MemberEntity member) {
        this.member = member;
    }

    public CameraEntity getCamera() {
        return camera;
    }

    public void setCamera(CameraEntity camera) {
        this.camera = camera;
    }

    public ContactEntity getContact() {
        return contact;
    }

    public void setContact(ContactEntity contact) {
        this.contact = contact;
    }
}

