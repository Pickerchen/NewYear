package com.sen5.secure.launcher.data.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.eric.xlee.lib.utils.LogUtil;
import com.sen5.secure.launcher.widget.CameraView;

import java.util.List;

import nes.ltlib.data.CameraEntity;
import nes.ltlib.utils.AppLog;

/**
 * @author jiangyicheng
 * @fileName CameraProviderControl.java
 * @describe
 * @date 2017-5-12
 */
public class CameraProviderControl {
    public static final int CAMERA_MODE_PLAY = 0;
    public static final int CAMERA_MODE_RECORD = 1;
    private static final String AUTHORITY = "com.example.cameraProvider";
    private static final Uri NOTIFY_URI = Uri.parse("content://" + AUTHORITY
            + "/camera");

    private static final Uri NOTIFY_URI_ONE = Uri.parse("content://" + AUTHORITY
            + "/camera_one");

    private static CameraProviderControl mCameraProviderControl;

    private CameraProviderControl() {

    }

    public static CameraProviderControl getInstance() {
        if (null == mCameraProviderControl) {
            synchronized (CameraProviderControl.class) {
                if (null == mCameraProviderControl) {
                    mCameraProviderControl = new CameraProviderControl();
                }
            }
        }
        return mCameraProviderControl;
    }

    /**
     * 初始化数据 添加Camera 数据到数据库中，如果已经存在，则进行数据跟新
     *
     * @param context
     * 0表示用于播放， 1表示用于录制
     * @methodName setCameraData
     * @function
     */
    public void initCameraData(Context context, List<CameraEntity> listDevice, List<CameraView> list) {
        if (null == listDevice) {
            deleteDataAll(context);
            return;
        }
        for (CameraEntity entityDevice : listDevice) {
            for (CameraView view : list) {
                if (view.mDid.equals(entityDevice.getDeviceID())) {
                    setCameraData(context, entityDevice.getDeviceID(), view.mUserid, -1, 0);
                    setCameraData(context, entityDevice.getDeviceID(), view.mRecordId, -1, 1);
                    break;
                }
            }
        }
        deleteData(context, listDevice);
    }

    /**
     * @param context
     * @param did
     * @param userId
     * @param status
     * @param mode    0表示用于播放， 1表示用于录制
     * @methodName insertData
     * @function
     */
    public void insertData(Context context, String did, long userId, int status, int mode) {
        AppLog.d("----------did = " + did + "----userId = " + userId);
        ContentValues arg1 = new ContentValues();
        arg1.put(DBOpenCameraHelper.DB_FIELD_DID, did);
        arg1.put(DBOpenCameraHelper.DB_FIELD_USERID, userId);
        arg1.put(DBOpenCameraHelper.DB_FIELD_STATUS, status);
        arg1.put(DBOpenCameraHelper.DB_FIELD_MODE, mode);
        context.getContentResolver().insert(NOTIFY_URI, arg1);
    }

    /**
     * 添加Camera 数据到数据库中，如果已经存在，则进行数据跟新
     *
     * @param context
     * @param did
     * @param userId
     * @param status
     * @param mode    0表示用于播放， 1表示用于录制
     * @methodName setCameraData
     * @function
     */
    public void setCameraData(Context context, String did, long userId, int status, int mode) {
        boolean checkThisDID = checkThisDID(context, did, mode);
        if (checkThisDID) {
            updateData(context, did, userId, status, mode);
        } else {
            insertData(context, did, userId, status, mode);
        }
    }

    /**
     * @param context
     * @param did
     * @param userId
     * @param status
     * @param mode    0表示用于播放， 1表示用于录制
     * @methodName updateData
     * @function
     */
    public void updateData(Context context, String did, long userId, int status, int mode) {

        AppLog.d("----------did = " + did + "----userId = " + userId);
        ContentValues arg1 = new ContentValues();
        arg1.put(DBOpenCameraHelper.DB_FIELD_DID, did);
        arg1.put(DBOpenCameraHelper.DB_FIELD_USERID, userId);
        arg1.put(DBOpenCameraHelper.DB_FIELD_STATUS, status);
        arg1.put(DBOpenCameraHelper.DB_FIELD_MODE, mode);
        context.getContentResolver().update(NOTIFY_URI_ONE, arg1, null, null);
    }

    /**
     * 删除数据根据did
     *
     * @param context
     * @param did
     * @methodName deleteData
     * @function
     */
    public void deleteData(Context context, String did) {
        context.getContentResolver().delete(NOTIFY_URI_ONE, DBOpenCameraHelper.DB_FIELD_DID + " = ?", new String[]{did});
    }

    /**
     * 删除所有数据
     *
     * @param context
     * @methodName deleteDataAll
     * @function
     */
    public void deleteDataAll(Context context) {
        context.getContentResolver().delete(NOTIFY_URI, null, null);
    }

    /**
     * 删除数据库中 不包含listDevice的数据，如果listDevice为空， 则全部删除
     *
     * @param context
     * @param listDevice
     * @methodName deleteData
     * @function
     */
    public void deleteData(Context context, List<CameraEntity> listDevice) {
        if (null == listDevice) {
            deleteDataAll(context);
            return;
        }
        Cursor cursor = queryDataAll(context);
        if (null != cursor && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String did = cursor.getString(cursor.getColumnIndex(DBOpenCameraHelper.DB_FIELD_DID));
                CameraEntity entityDevice = new CameraEntity();
                entityDevice.DeviceID = did;
                if (!listDevice.contains(entityDevice)) {
                    deleteData(context, did);
                } else {

                }
            }
        }
        closeCursor(cursor);

    }

    /**
     * 不符合判断条件的返回false
     *
     * @param context
     * @param did     camera的did
     * @param mode    该条数据的作用，0表示用于播放， 1表示用于录制
     * @return
     * @methodName checkThisDID
     * @function
     */
    private boolean checkThisDID(Context context, String did, int mode) {
        boolean bool = false;
        Cursor cursor = queryDataByDID(context, did, mode);
        if (null != cursor && cursor.getCount() > 0) {
            bool = true;
        }
        closeCursor(cursor);
        AppLog.d("----------did = " + did + "----bool = " + bool);
        return bool;
    }

    private Cursor queryDataByDID(Context context, String did, int mode) {
        AppLog.d("----------did = " + did);
        Cursor query = context.getContentResolver().query(NOTIFY_URI, null,
                DBOpenCameraHelper.DB_FIELD_DID + " = ? and "
                        + DBOpenCameraHelper.DB_FIELD_MODE + " = ?", new String[]{did, Integer.toString(mode)}, null);

        return query;
    }

    private Cursor queryDataAll(Context context) {
        Cursor query = context.getContentResolver().query(NOTIFY_URI, null,
                null, null, null);

        return query;
    }

    private void closeCursor(Cursor cursor) {
        if (null != cursor && !cursor.isClosed()) {
            cursor.close();
        }

    }

}
