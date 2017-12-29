package com.ipcamerasen5.main.control;

import android.content.Context;
import android.content.res.Resources;

import com.ipcamerasen5.main1.R;
import nes.ltlib.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangyicheng on 2017/3/7.
 */

public class CameraViewUI {

    public static final int CAMERA_STATUS_CONNECTING = 0;
    public static final int CAMERA_STATUS_CONNECTED = 100;
    public static final int CAMERA_STATUS_ERROR = 101;
    public static final int CAMERA_STATUS_TIME_OUT = 10;
    public static final int CAMERA_STATUS_OFF_LINE = 9;
    public static final int CAMERA_STATUS_ERROR_ID = 5;
    public static final int CAMERA_STATUS_BREAK_OFF = 11;
    public static final int CAMERA_STATUS_USER_ERROR = 1;

    private Context mContext;
    private List<Integer> mListCameraStatus;
    private static final int DEFAULT_CAMERA_HOLD_COUNT = 8;

    public CameraViewUI(Context context){
        this.mContext = context;
        mListCameraStatus = new ArrayList<Integer>();
        for (int i = 0; i < DEFAULT_CAMERA_HOLD_COUNT; i++) {
            mListCameraStatus.add(-1);
        }
    }

    public List<Integer> getDataUIList(){
        return mListCameraStatus;
    }

    public void setDataList(List<Integer> listCameraStatus){
        this.mListCameraStatus = listCameraStatus;
    }

    public void refreshDataList(int location, int data){
        mListCameraStatus.set(location, data);
    }



    public String getCameraStatusString(int position){
        Integer integer = mListCameraStatus.get(position);
        String statusStringByStatusID = getStatusStringByStatusID(mContext.getResources(), integer);
        return statusStringByStatusID;
    }

    public boolean getCameraStatusBoolean(int position){
        Integer integer = mListCameraStatus.get(position);
        boolean statusBooleanByStatusID = getStatusBooleanByStatusID(integer);
        return statusBooleanByStatusID;
    }

    private String getStatusStringByStatusID(Resources mResources, int id){
        String str = "";
        LogUtils.e("getStatusStringByStatusID","-----------getStatusStringByStatusID = " + id);
        switch (id) {
            case CAMERA_STATUS_CONNECTING:
//			str = "连接中...";
                str = mResources.getString(R.string.status_connecting);
                break;
            case CAMERA_STATUS_CONNECTED:
//			str = "在线!";
                str = mResources.getString(R.string.status_online);
                break;
            case CAMERA_STATUS_ERROR:
//			str = "连接错误";
                str = mResources.getString(R.string.status_off_line);
                break;
            case CAMERA_STATUS_TIME_OUT:
//			str = "连接超时";
                str = mResources.getString(R.string.status_off_line);
                break;
            case CAMERA_STATUS_OFF_LINE:
//			str = "离线";
                str = mResources.getString(R.string.status_off_line);
                break;
            case CAMERA_STATUS_ERROR_ID:
//			str = "离线[无效ID]";
                str = mResources.getString(R.string.status_off_line);
                break;
            case CAMERA_STATUS_BREAK_OFF:
//			str = "断开";
                str = mResources.getString(R.string.status_off_line);
                break;
            case CAMERA_STATUS_USER_ERROR:
//			str = "用户名密码错误";
                str = mResources.getString(R.string.status_off_line);
                break;
            default:
                break;
        }
        return str;
    }

    public static boolean getStatusBooleanByStatusID(int id){
        boolean bool = false;
        switch (id) {
            case CAMERA_STATUS_CONNECTING:
//			str = "连接中...";
                bool = false;
                break;
            case CAMERA_STATUS_CONNECTED:
//			str = "在线啊";
                bool = true;
                break;
            case CAMERA_STATUS_ERROR:
//			str = "连接错误";
                bool = false;
                break;
            case CAMERA_STATUS_TIME_OUT:
//			str = "连接超时";
                bool = false;
                break;
            case CAMERA_STATUS_OFF_LINE:
//			str = "离线";
                bool = false;
                break;
            case CAMERA_STATUS_ERROR_ID:
//			str = "离线[无效ID]";
                bool = false;
                break;
            case CAMERA_STATUS_BREAK_OFF:
//			str = "断开";
                bool = false;
                break;
            case CAMERA_STATUS_USER_ERROR:
//			str = "用户名密码错误";
                bool = false;
                break;

            default:
                break;
        }
        return bool;
    }
}
