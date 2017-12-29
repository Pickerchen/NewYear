package com.ipcamera.main.control;

import hsl.p2pipcam.entity.EntityDevice;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import com.ipcamera.main.imple.ICameraStatus;
import com.ipcamera.main.smarthome.CameraControlNew;
import com.ipcamera.main.smarthome.UtilsCameraStatus;
import com.ipcamera.main.utils.DLog;
import com.ipcamera.main.view.FingerPrint;

public class DataCameraControl {
	
	private List<EntityDevice> mListCameraDevice;
	private String mCameraDIDdefault;
	private List<String> mListUIL = new ArrayList<String>();
	private List<Integer> mListCameraStatus;
	
	private Context mContext;
	private UIFingerPrintControl mUIFingerPrintControl;


	private ICameraStatus mICameraStatus;
	/**
	 * 标记当前播放的Camera的下标
	 */
	private int mPosition;
	
	public List<EntityDevice> getListCameraDevice(){
		return mListCameraDevice;
	}
	
	public int getPosition(){
		return mPosition;
	}
	
	public List<String> getListCameraStatusString(){
		return mListUIL;
	}
	
	public List<Integer> getListCameraStatusInt(){
		return mListCameraStatus;
	}

	public DataCameraControl(Context context, FingerPrint mainActivity){
		this.mContext = context;
		mListCameraDevice = CameraControlNew.initStartCameraDevice(context, mainActivity);
		initData();
	}
	
	public void initData(){
		mListCameraStatus = new ArrayList<Integer>();
		for (int i = 0; i < 8; i++) {
			mListCameraStatus.add(-1);
		}
		
		
		int size = mListCameraDevice.size();
		for (int i = 0; i < size; i++) {
			mListUIL.add(mListCameraDevice.get(i).DeviceID);
		}
		
		mPosition = mListUIL.indexOf(mCameraDIDdefault);
		if(mPosition < 0){
			mPosition = 0;
		}
	}

	private void initCameraBg(){
		if(null != mListCameraDevice){
			int size = mListCameraDevice.size();
			if(size > 0){
				EntityDevice entityDevice = mListCameraDevice.get(mPosition);
				if(null != mUIFingerPrintControl){
					mUIFingerPrintControl.setVideoDefaultBg(entityDevice.drawable);
				}
			}
		}
	}
	
	public void setDefaultDID(String did){
		mCameraDIDdefault = did;
	}

	public void setUIControlFingerPrint(UIFingerPrintControl uiControlFingerPrint){
		mUIFingerPrintControl = uiControlFingerPrint;
		initCameraBg();
	}
	
	public void setICameraStatus(ICameraStatus iCameraStatus){
		mICameraStatus = iCameraStatus;
	}
	
	public void setPosition(int position){
		mPosition = position;
	}
	
	public EntityDevice getCameraDeviceByPosition(int position){
		EntityDevice entityDevice = null;
		if(null != mListCameraDevice){
			int size = mListCameraDevice.size();
			if(size > position){
				entityDevice = mListCameraDevice.get(position);
			}
		}
		return entityDevice;
	}
	
	public void refreshData(){
		List<EntityDevice> refreshCameraDevice = CameraControlNew.getRefreshCameraDevice(mContext);
		DLog.e("--------------refreshCameraDevice = " + refreshCameraDevice.size());
		int size = mListCameraDevice.size();
		int size2 = refreshCameraDevice.size();
		if(size < size2){
			//增摄像头
			EntityDevice entityDevice = refreshCameraDevice.get(size2-1);
			mListCameraDevice.add(entityDevice);
			mListUIL.add(entityDevice.DeviceID);
			
//			CameraControlNew.initDevices(entityDevice.DeviceID);
//			if(size == 0){
//				mHandler.sendEmptyMessageDelayed(MSG_REFRESH_CAMERA_CHANGE, 500);
//			}
		}else if(size > size2){
			//减摄像头
			for (int i = size -1 ; i >= 0; i--) {
				EntityDevice entityDevice = mListCameraDevice.get(i);
				int indexOf = refreshCameraDevice.indexOf(entityDevice);
				if(indexOf == -1){
					CameraControlNew.removeDevices(i);
					mListCameraDevice.remove(i);
					mListCameraStatus.remove(i);
					mListUIL.remove(entityDevice.DeviceID);
				}
			}
		}
	}

	public void switchCamera(final int switchDirection){
		mPosition = CameraControlNew.getPosition();
//		CameraControlNew.startPlayStreamTest(mPosition, switchDirection);
		CameraControlNew.startPlayStreamTest(switchDirection);
		mICameraStatus.refreshMainUI(switchDirection);
		
	}
	
	public void startInitDevices(){
		CameraControlNew.clearListUserID();
		int size = mListCameraDevice.size();
		for (int i = 0; i < size; i++) {
//			CameraControlNew.initDevices(mListUIL.get(i));
		}
	}
	
	public void initCameraFirstUI(){
    	if(null != mListCameraDevice && mListCameraDevice.size() > 0){
    		
    		mUIFingerPrintControl.setVideoDefaultBg(mListCameraDevice.get(0).drawable);
    	}
	}
	
	public void refreshCameraStatusInt(int what, Integer arg1){
		mListCameraStatus.set(what, arg1);
	}
	
	public void playCameraByPosition(String did){
		//jyc
		int indexOf = mListUIL.indexOf(did);
		DLog.d("-------------indexOf = " + indexOf + "  did = " + did + "  mPosition = " + mPosition);
		if(indexOf < 0 || indexOf == mPosition){
			return;
		}else{
			mCameraDIDdefault = did;
		}
		
		int switchDirection = CameraControlNew.STATUS_SWITCH_NONE;
		if(indexOf > mPosition){
			switchDirection = CameraControlNew.STATUS_SWITCH_RIGHT;
		}else if(indexOf < mPosition){
			switchDirection = CameraControlNew.STATUS_SWITCH_LEFT;
		}
		CameraControlNew.stopPlayStream(mPosition);
		
		DLog.d("-------------indexOf = " + indexOf + " switchDirection = " + switchDirection);
		
		CameraControlNew.setPosition(indexOf);
		
		switchCamera(switchDirection);
	}
	
	public boolean refreshCameraStatusUI(int status){
		int nextPositionByStatus = CameraControlNew.getNextPositionByStatus(CameraControlNew.mListUserId.size(), mPosition, status);
		String cameraStatusString = getCameraStatusString(nextPositionByStatus);
		boolean cameraStatusBoolean = getCameraStatusBoolean(nextPositionByStatus);
		mPosition = nextPositionByStatus;
		mICameraStatus.refreshStatus(cameraStatusString);
//		DLog.e("---------------------nextPositionByStatus = "  +nextPositionByStatus + );
		return cameraStatusBoolean;
	}
	
	private String getCameraStatusString(int position){
		Integer integer = mListCameraStatus.get(position);
		String statusStringByStatusID = UtilsCameraStatus.getStatusStringByStatusID(mContext, integer);
		return statusStringByStatusID;
	}
	
	private boolean getCameraStatusBoolean(int position){
		Integer integer = mListCameraStatus.get(position);
		boolean statusBooleanByStatusID = UtilsCameraStatus.getStatusBooleanByStatusID(integer);
		return statusBooleanByStatusID;
	}
	
}
