package com.sen5.smartlifebox.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wanglin on 2017/2/8.
 */
public class ContactEntity implements Parcelable{

    private String name;
    private String number;
    private int emergencyFlag;//是否是紧急联系人【0：不是；1：是】

    public ContactEntity() {
    }

    protected ContactEntity(Parcel in) {
        name = in.readString();
        number = in.readString();
        emergencyFlag = in.readInt();
    }

    public static final Creator<ContactEntity> CREATOR = new Creator<ContactEntity>() {
        @Override
        public ContactEntity createFromParcel(Parcel in) {
            return new ContactEntity(in);
        }

        @Override
        public ContactEntity[] newArray(int size) {
            return new ContactEntity[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getEmergencyFlag() {
        return emergencyFlag;
    }

    public void setEmergencyFlag(int emergencyFlag) {
        this.emergencyFlag = emergencyFlag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(number);
        dest.writeInt(emergencyFlag);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof ContactEntity){
            if(((ContactEntity)o).getNumber().equals(this.number)){
                return true;
            }
        }
        return false;
    }

}
