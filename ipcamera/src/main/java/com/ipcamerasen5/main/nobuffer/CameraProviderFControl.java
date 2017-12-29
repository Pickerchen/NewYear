package com.ipcamerasen5.main.nobuffer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import nes.ltlib.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;


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
	public MyContentObserver mMyContentObserver;

	public CameraProviderFControl(Context context, Handler handler) {
		mContext = context;
		mContentResolver = mContext.getContentResolver();
		if(null != handler){
			mMyContentObserver = new MyContentObserver(handler);
			mContentResolver.registerContentObserver(NOTIFY_URI, true, mMyContentObserver);
		}
	}

	public void unRegister(){
		if(null != mMyContentObserver){

			mContentResolver.unregisterContentObserver(mMyContentObserver);
		}
	}

	public int getCameraStatusByDID(String did){
		int status = -1;
		Cursor queryCursorByDID = queryCursorByDID(did);
		if(null != queryCursorByDID && queryCursorByDID.getCount() > 0){
			queryCursorByDID.getString(queryCursorByDID.getColumnIndex("status"));
		}else{
			status = -1;
		}
		return status;
	}
	
	public List<EntityCameraStatus>  getAllCameraStatus(){
		List<EntityCameraStatus> list = new ArrayList<EntityCameraStatus>();
		Cursor queryCursorByDID = queryCursor();
		if(null != queryCursorByDID && queryCursorByDID.getCount() > 0){
			while (queryCursorByDID.moveToNext()) {
//				queryCursorByDID.moveToNext();
				
				EntityCameraStatus entityCameraStatus = new EntityCameraStatus();
				entityCameraStatus.setUserID(queryCursorByDID.getLong(queryCursorByDID.getColumnIndex(DB_FIELD_USERID)));
				entityCameraStatus.setDid(queryCursorByDID.getString(queryCursorByDID.getColumnIndex(DB_FIELD_DID)));
				entityCameraStatus.setStatus(queryCursorByDID.getInt(queryCursorByDID.getColumnIndex(DB_FIELD_STATUS)));
				entityCameraStatus.setDbFieldMode(queryCursorByDID.getString(queryCursorByDID.getColumnIndex(DB_FIELD_MODE)));

//				entityCameraStatus.setUserID(queryCursorByDID.getLong(queryCursorByDID.getColumnIndex(DB_FIELD_USERID)));
//				entityCameraStatus.setDid(queryCursorByDID.getString(queryCursorByDID.getColumnIndex(DB_FIELD_DID)));
//				entityCameraStatus.setStatus(queryCursorByDID.getInt(queryCursorByDID.getColumnIndex(DB_FIELD_STATUS)));
				list.add(entityCameraStatus);
			}
		}

		LogUtils.e("getAllCameraStatus","----------------abcd = " + queryCursorByDID.getCount());
		for (int i = 0; i < list.size(); i++) {
			LogUtils.e("getAllCameraStatus","-------------cameraStatus = " + list.get(i).toString());
		}
		//// TODO: 2017/6/7
		if (null != queryCursorByDID){
			queryCursorByDID.close();
		}
		return list;
	}
	
	public Cursor queryCursorByDID(String did){
		Cursor query = null;
		if(null != mContentResolver){
			query = mContentResolver.query(NOTIFY_URI, null, DB_FIELD_DID + " = ?", new String[]{did}, null);
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
	
	class MyContentObserver extends ContentObserver{
		private Handler mHandler;
		public MyContentObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
			mHandler = handler;
		}
		
		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			LogUtils.e("MyContentObserver","---------------- selfChange = " + selfChange);
		}
		
		@Override
		public void onChange(boolean selfChange, Uri uri) {
			// TODO Auto-generated method stub
			super.onChange(selfChange, uri);
			LogUtils.e("MyContentObserver","---------------- selfChange = " + selfChange + "--uri = " + uri.getAuthority());
			
			getAllCameraStatus();
			if(null != mHandler){
				mHandler.removeMessages(MSG_PROVIDE_DATA_CHANGE);
				mHandler.sendEmptyMessageDelayed(MSG_PROVIDE_DATA_CHANGE, 230);
			}
		}
		
	}
	
}
