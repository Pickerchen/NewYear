package com.sen5.smartlifebox.data.db;

import android.content.ContentResolver;
import android.content.Context;

import android.database.Cursor;
import android.net.Uri;

import nes.ltlib.utils.AppLog;


public class CameraProviderFControl {
	private static final String AUTHORITY = "com.example.cameraProvider";
	private static final Uri NOTIFY_URI = Uri.parse("content://" + AUTHORITY
			+ "/camera");
	
	private static final Uri NOTIFY_URI_ONE = Uri.parse("content://" + AUTHORITY
			+ "/camera_one");
//	private static CameraProviderFControl mCameraProviderFControl;
	private Context mContext;
	private ContentResolver mContentResolver;
	
	private static final String DB_FIELD_DID = "DID";
	private static final String DB_FIELD_STATUS = "status";
	private static final String DB_FIELD_USERID = "userId";
	public static final String DB_FIELD_MODE = "mode";
	
	public static final int MSG_PROVIDE_DATA_CHANGE = 123321;

	public CameraProviderFControl(Context context) {
		mContext = context;
		mContentResolver = mContext.getContentResolver();

	}



	public int getCameraStatusByDID(String did){
		int status = -1;
		Cursor queryCursorByDID = queryCursorByDID(did);
		if(null != queryCursorByDID && queryCursorByDID.getCount() > 0){
			while (queryCursorByDID.moveToNext()){
				AppLog.i("moveToNext");
				status = queryCursorByDID.getInt(queryCursorByDID.getColumnIndex(DB_FIELD_STATUS));
				AppLog.i("status:"+status);
			}
		}else{
			status = -1;
		}
		return status;
	}


	public int getCameraUserIdByDID(String did){
		int userId = -1;
		Cursor queryCursorByDID = queryCursorByDID(did);
		if(null != queryCursorByDID && queryCursorByDID.getCount() > 0){
			while (queryCursorByDID.moveToNext()){
				AppLog.i("moveToNext");
				userId = queryCursorByDID.getInt(queryCursorByDID.getColumnIndex(DB_FIELD_USERID));
				AppLog.i("userId:"+userId);
			}
		}else{
			userId = -1;
		}
		return userId;
	}
	

	public Cursor queryCursorByDID(String did){
		Cursor query = null;
		if(null != mContentResolver){
			query = mContentResolver.query(NOTIFY_URI, null, DB_FIELD_DID + " = ? and "+DB_FIELD_MODE+" =? ", new String[]{did,"0"}, null);
		}
		return query;
	}

	public Cursor queryCursor(){
		Cursor query = null;
		if(null != mContentResolver){
			query = mContentResolver.query(NOTIFY_URI, null, null, null, null);
		}
		return query;
	}


	
}
