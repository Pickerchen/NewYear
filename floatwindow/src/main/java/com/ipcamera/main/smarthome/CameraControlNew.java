package com.ipcamera.main.smarthome;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;

import com.ipcamera.main.R;
import com.ipcamera.main.utils.DLog;
import com.ipcamera.main.utils.ImageTools;
import com.ipcamera.main.utils.UtilsFile;
import com.ipcamera.main.view.FingerPrint;
import com.litesuits.common.io.FileUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import hsl.p2pipcam.camera.MyRender;
import hsl.p2pipcam.camera.MyRender.RenderListener;
import hsl.p2pipcam.entity.EntityCameraStatus;
import hsl.p2pipcam.entity.EntityDevice;
import hsl.p2pipcam.nativecaller.DeviceSDK;

public class CameraControlNew {
	
	public static final int CAMERA_STATUS_CONNECTING = 0;
	public static final int CAMERA_STATUS_CONNECTED = 100;
	public static final int CAMERA_STATUS_ERROR = 101;
	public static final int CAMERA_STATUS_TIME_OUT = 10;
	public static final int CAMERA_STATUS_OFF_LINE = 9;
	public static final int CAMERA_STATUS_ERROR_ID = 5;
	public static final int CAMERA_STATUS_BREAK_OFF = 11;
	public static final int CAMERA_STATUS_USER_ERROR = 1;
	
	private static final String CAMERA_DEVICE_FILE_PATH = "/data/smarthome/camera.ini";
	
	private static final String  DEFAULT_PATH_TITLE = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "IPCamera" + File.separator;
	public static final String captureDefaultUIPath = DEFAULT_PATH_TITLE + "default";

	public static List<Long> mListUserId = new ArrayList<Long>();
	public static List<MyRender> mListMyRenderTest = new ArrayList<MyRender>();
	private static final String ACTION_RECEIVER_CLOSE_SERVICE = "com.sen5.process.camera.close.float";
	
	/**
	 * 表示接口调用成功
	 */
	public static final int AP_ERROR_SUCC = 1;
	
	/**
	 * 不进行Camera的切换，也不会停止当前Camera再播放
	 */
	public static final int STATUS_SWITCH_NONE = -1;
	
	public static final int STATUS_SWITCH_LEFT = 0;
	public static final int STATUS_SWITCH_RIGHT = 1;
	private static int mPosition;
	private static EventBus mEventBus;
	private static long mUserID;
	
	/**
	 * 设置Launcher8上小窗口图片  true表示设置成默认图片， false表示使用最后一张截图图片
	 */
	public static final boolean CAMERA_USE_DEFAULT_BITMAP = true;

	public static List<EntityDevice> initStartCameraDevice(Context context, FingerPrint mainActivity){
		mEventBus = EventBus.getDefault();
		UtilsFile.mkdir(captureDefaultUIPath);
		
		List<EntityDevice> list = new ArrayList<EntityDevice>();
		File file = new File(CAMERA_DEVICE_FILE_PATH);
		if(file.exists()){
			try {
				DLog.e("---------------------e = 0");
				List<String> readLines = FileUtils.readLines(file);
				if(null != readLines){
					int size = readLines.size();
					for (int i = 0; i < size; i++) {
						String[] split = readLines.get(i).replaceAll("##", "").replaceAll("-", "").split("#");
						EntityDevice ed = new EntityDevice();
						if(null != split){
							if(split.length > 1){
								ed.DeviceID = split[0];
								if(CAMERA_USE_DEFAULT_BITMAP){
									ed.bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_camera_default);
								}else{
									ed.bitmap = ImageTools.getBitmapForPath(captureDefaultUIPath + File.separator + ed.DeviceID + ".jpg",
											BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_camera_default));
								}
								ed.drawable = ImageTools.bitmapToDrawable(context, ed.bitmap);
								ed.DeviceName = split[1];
							}else if(split.length > 0){
								ed.DeviceID = split[0];
							}
						}
						list.add(ed);
					}
					mEventBus.post(list);
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				DLog.e("---------------------e = " + e.getMessage());
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public static List<EntityDevice> getRefreshCameraDevice(Context context){
		mEventBus = EventBus.getDefault();
		
		List<EntityDevice> list = new ArrayList<EntityDevice>();
		File file = new File(CAMERA_DEVICE_FILE_PATH);
		if(file.exists()){
			try {
				DLog.e("---------------------e = 0");
				List<String> readLines = FileUtils.readLines(file);
				if(null != readLines){
					int size = readLines.size();
					for (int i = 0; i < size; i++) {
						String[] split = readLines.get(i).replaceAll("##", "").replaceAll("-", "").split("#");
						EntityDevice ed = new EntityDevice();
						if(null != split){
							if(split.length > 1){
								ed.DeviceID = split[0];
								ed.DeviceName = split[1];
							}else if(split.length > 0){
								ed.DeviceID = split[0];
							}
						}
						list.add(ed);
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				DLog.e("---------------------e = " + e.getMessage());
				e.printStackTrace();
			}
			
		}
		return list;
	}
	
	public static boolean mCameraSwitchOver = true;
	public static void startPlayStreamTest(final int status){
		DLog.d("----------------startPlayStreamTest");
		if(mCameraSwitchOver){
			mCameraSwitchOver = false;
			new Thread(){
				public void run() {
					
					mListMyRenderTest.size();
					DLog.d("-------------startStatus------ = mUserID0 = " + mUserID + " mListMyRender.size() = " + mListMyRenderTest.size());
					
					int nextPositionByStatus = getNextPositionByStatus(mListUserId.size(), mListUserId.indexOf(mUserID), status);
					mPosition = nextPositionByStatus;
					long newUserID = mUserID;
					if(mListUserId.size() > nextPositionByStatus){
						newUserID = mListUserId.get(nextPositionByStatus);
					}
					
					if(newUserID != mUserID){
						 DeviceSDK.stopPlayStream(mUserID);
						 mUserID = newUserID;
					}
	
					DeviceSDK.setRender(mUserID, mListMyRenderTest.get(0));
					
					DLog.d("-------------startStatus------ = mUserID1 = " + mUserID);
					int startStatus = DeviceSDK.startPlayStream(mUserID, 0, 1);
					DLog.d("-------------startStatus------ = startStatus = " + startStatus);
					mCameraSwitchOver = true;
	//				DLog.e("-------------startStatus------ = " + startStatus);
				};
			}.start();
		
		}
	}
	
	public static void removeDevices(int position){
		Long long1 = mListUserId.get(position);
		stopPlayStream(long1);
		mListUserId.remove(position);
		if(position == getPosition()){
			CameraControlNew.startPlayStreamTest(CameraControlNew.STATUS_SWITCH_RIGHT);
		}
	}
	
	public static void initGlsListener(GLSurfaceView glSurfaceView, RenderListener renderListener){
		MyRender myRender = new MyRender(glSurfaceView);
		myRender.setListener(renderListener);
		glSurfaceView.setRenderer(myRender);
		mListMyRenderTest.add(myRender);
	}
	
	public static void setData(List<EntityCameraStatus> mAllCameraStatus){
		if(null != mAllCameraStatus){
			mListUserId.clear();
			int size = mAllCameraStatus.size();
			for (int i = 0; i < size; i++) {
				mListUserId.add(mAllCameraStatus.get(i).getUserID());
			}
		}
	}

	public static int getPosition(){
		mPosition = Math.max(mPosition, 0);
		return mPosition;
	}
	
	public static void setPosition(int position){
		mPosition = position;
	}
	
	public static void setUserId(long userId){
		mUserID = userId;
	}
	
	public static void clearListMyRender(){
		mListMyRenderTest.clear();
	}
	
	public static void clearListUserID(){
		CameraControlNew.mListUserId.clear();
	}
	
	public static void stopPlayStream(long userID){
		DeviceSDK.stopPlayStream(userID);
	}
	
	public static void stopPlayStream(int position){
		Long long1 = mListUserId.get(position);
		stopPlayStream(long1);
	}
	
	public static void closeDevice(long userID){
		DeviceSDK.closeDevice(userID);
	}
	
	public static void stopPlayStreamAll(){
		int size = CameraControlNew.mListUserId.size();
		DLog.d("---------stopPlayStreamAll size = " + size);
		for (int i = 0; i < size; i++) {
			CameraControlNew.stopPlayStream(CameraControlNew.mListUserId.get(i));
		}
	}
	
	public static void closeDeviceAll(){
		int size = CameraControlNew.mListUserId.size();
		for (int i = 0; i < size; i++) {
			CameraControlNew.closeDevice(CameraControlNew.mListUserId.get(i));
		}
	}
	
	public static void closeAll(){
//		stopPlayStreamAll();
//		DeviceSDK.stopPlayStream(mUserID);
		mListUserId.clear();
		mListMyRenderTest.clear();
	}

	/**
	 * 目前知道的判断为native方法返回1 表示成功， 如有其他的判断 后续再增加
	 * @param status
	 * @return
	 */
	public static boolean isSuccess(int status){
		if(status == 1){
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
	
	private static void sendCloseBroadcast(Context context){
		Intent intent = new Intent();
		intent.setAction(ACTION_RECEIVER_CLOSE_SERVICE);
//		intent.setPackage("com.sen5.klauncher");
		context.sendBroadcast(intent);
	}
	
	public static void closeStreamCamera(List<EntityDevice> listCameraDevice){
		CameraControlNew.closeAll();
	}
	
	public static void closeAll(Context context, boolean isSendCloseBroadcast){
		if(isSendCloseBroadcast){
			sendCloseBroadcast(context);
		}
	}
	
}
