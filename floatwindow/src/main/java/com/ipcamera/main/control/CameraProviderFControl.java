package com.ipcamera.main.control;

import hsl.p2pipcam.entity.EntityCameraStatus;

import java.util.ArrayList;
import java.util.List;

import com.ipcamera.main.utils.DLog;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

public class CameraProviderFControl {
	
	private static final int CAMERA_MODE_PLAY = 0;
	private static final int CAMERA_MODE_RECORD = 1;
	
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
	private static final String DB_FIELD_MODE = "mode";
	
	public static final int MSG_PROVIDE_DATA_CHANGE = 123321;
	private MyContentObserver mMyContentObserver;
	
	public CameraProviderFControl(Context context, Handler handler) {
		mContext = context;
		mContentResolver = mContext.getContentResolver();
		mMyContentObserver = new MyContentObserver(handler);
		mContentResolver.registerContentObserver(NOTIFY_URI, true, mMyContentObserver);
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
		closeCursor(queryCursorByDID);
		return status;
	}
	
	public List<EntityCameraStatus> getAllCameraStatus(){
		List<EntityCameraStatus> list = new ArrayList<EntityCameraStatus>();
		Cursor queryCursorByDID = queryCursor();
		if(null != queryCursorByDID && queryCursorByDID.getCount() > 0){
			while (queryCursorByDID.moveToNext()) {
//				queryCursorByDID.moveToNext();
				
				EntityCameraStatus entityCameraStatus = new EntityCameraStatus();
				entityCameraStatus.setUserID(queryCursorByDID.getLong(queryCursorByDID.getColumnIndex(DB_FIELD_USERID)));
				entityCameraStatus.setDid(queryCursorByDID.getString(queryCursorByDID.getColumnIndex(DB_FIELD_DID)));
				entityCameraStatus.setStatus(queryCursorByDID.getInt(queryCursorByDID.getColumnIndex(DB_FIELD_STATUS)));
//				entityCameraStatus.setUserID(queryCursorByDID.getLong(queryCursorByDID.getColumnIndex(DB_FIELD_USERID)));
//				entityCameraStatus.setDid(queryCursorByDID.getString(queryCursorByDID.getColumnIndex(DB_FIELD_DID)));
//				entityCameraStatus.setStatus(queryCursorByDID.getInt(queryCursorByDID.getColumnIndex(DB_FIELD_STATUS)));
				list.add(entityCameraStatus);
			}
		}
		closeCursor(queryCursorByDID);
		DLog.e("---------------abcd = " + queryCursorByDID.getCount());
		for (int i = 0; i < list.size(); i++) {
			DLog.e("-------------cameraStatus = " + list.get(i).toString());
		}
		return list;
	}
	
	public Cursor queryCursorByDID(String did){
		Cursor query = null;
		if(null != mContentResolver){
			query = mContentResolver.query(NOTIFY_URI, null, DB_FIELD_DID + " = ? and " + DB_FIELD_MODE + " = ?", new String[]{did, Integer.toString(CAMERA_MODE_PLAY)}, null);
		}
		return query;
	}
	
	public Cursor queryCursor(){
		Cursor query = null;
		if(null != mContentResolver){
			query = mContentResolver.query(NOTIFY_URI, null, DB_FIELD_MODE + " = ?", new String[]{Integer.toString(CAMERA_MODE_PLAY)}, null);
		}
		return query;
	}
	
	private void closeCursor(Cursor cursor){
		if(null != cursor && !cursor.isClosed()){
			cursor.close();
		}
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
			DLog.e("---------------- selfChange = " + selfChange);
		}
		
		@Override
		public void onChange(boolean selfChange, Uri uri) {
			// TODO Auto-generated method stub
			super.onChange(selfChange, uri);
			DLog.e("---------------- selfChange = " + selfChange + "--uri = " + uri.getAuthority());
			
			getAllCameraStatus();
			if(null != mHandler){
				mHandler.removeMessages(MSG_PROVIDE_DATA_CHANGE);
				mHandler.sendEmptyMessageDelayed(MSG_PROVIDE_DATA_CHANGE, 230);
			}
		}
		
	}
	
}
