package com.sen5.smartlifebox.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wanglin on 2016/10/13.
 */

public class MemberEntity implements Parcelable {

    private String identity_id;
    private String identity_name;

    public MemberEntity() {
    }

    protected MemberEntity(Parcel in) {
        identity_id = in.readString();
        identity_name = in.readString();
    }

    public static final Creator<MemberEntity> CREATOR = new Creator<MemberEntity>() {
        @Override
        public MemberEntity createFromParcel(Parcel in) {
            return new MemberEntity(in);
        }

        @Override
        public MemberEntity[] newArray(int size) {
            return new MemberEntity[size];
        }
    };

    public String getIdentity_id() {
        return identity_id;
    }

    public void setIdentity_id(String identity_id) {
        this.identity_id = identity_id;
    }

    public String getIdentity_name() {
        return identity_name;
    }

    public void setIdentity_name(String identity_name) {
        this.identity_name = identity_name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MemberEntity){
            if(((MemberEntity)obj).getIdentity_id().equals(identity_id)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(identity_id);
        dest.writeString(identity_name);
    }
}
