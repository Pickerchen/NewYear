package com.ipcamerasen5.main.utils;


import java.util.List;

import hsl.p2pipcam.entity.EntityDevice;

/**
 * Created by jiangyicheng on 2016/11/7.
 */

public class UtilsCameraStatus {
    public static final int STATUS_SWITCH_NONE = -1;
    public static final int STATUS_SWITCH_NONE_CLOSE_STREAM = -2;
    public static final int STATUS_SWITCH_LEFT = 0;
    public static final int STATUS_SWITCH_RIGHT = 1;

    /**
     * 目前知道的判断为native方法返回1 表示成功， 如有其他的判断 后续再增加
     * @param status
     * @return
     */
    public static boolean isSuccess(int status){
        if(status != 0){
            return true;
        }
        return false;
    }

    public static int getNextPositionByStatus(int count, int position, int status){
        if(status == STATUS_SWITCH_LEFT){
            //left 减
            position --;
            if(position < 0){
                position = count-1;
            }
        }else if(status == STATUS_SWITCH_RIGHT){
            //right 加
            position ++;
            if(position >= count){
                position = 0;
            }
        }

        if(position < 0){
            position = 0;
        }
        return position;
    }

    public static boolean checkCameraChange(List<EntityDevice> listCameraDeviceNew, List<EntityDevice> listCameraDeviceOld){

//        List<EntityDevice> startCameraDevice = CameraControlNew.getStartCameraDevice(context);
//        List<EntityDevice> startCameraDevice = listCameraDeviceNew;

        if(null != listCameraDeviceNew && listCameraDeviceOld != null){
            int size = listCameraDeviceNew.size();
            int size1 = listCameraDeviceOld.size();
            if(size != size1){
                return true;
            }else{
                for (int i = 0; i < size; i++) {
                    if(listCameraDeviceNew.get(i).equals(listCameraDeviceOld.get(i))){

                    }else{
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
