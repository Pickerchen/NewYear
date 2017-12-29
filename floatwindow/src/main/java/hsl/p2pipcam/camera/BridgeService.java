/**
 * 
 */
package hsl.p2pipcam.camera;


import org.json.JSONException;
import org.json.JSONObject;

import com.ipcamera.main.utils.DLog;



import hsl.p2pipcam.listener.DeviceStatusListener;
import hsl.p2pipcam.listener.PlayListener;
import hsl.p2pipcam.listener.RecorderListener;
import hsl.p2pipcam.listener.SearchDeviceListener;
import hsl.p2pipcam.listener.SettingsListener;
import hsl.p2pipcam.nativecaller.DeviceSDK;
import android.app.Service;
import android.bluetooth.BluetoothClass.Device;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 *@author wang.jingui
 */
public class BridgeService extends Service
{
	//监听回调接口
	private static DeviceStatusListener deviceStatusListener;
	private static PlayListener playListener;
	private static RecorderListener recorderListener;
	private static SettingsListener settingsListener;
	private static SearchDeviceListener searchDeviceListener;
	
	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		DeviceSDK.initialize("");
//		DeviceSDK.setCallback(this);
		new Thread(){
			public void run() {
				
				DeviceSDK.networkDetect();
			};
		}.start();
		
		DeviceSDK.setSearchCallback(this);
		DLog.e("BridgeService onCreate");
	}
	
	@Override
	public void onDestroy() {
		DLog.e("BridgeService onDestroy");
		DeviceSDK.unInitSearchDevice();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		DLog.e("BridgeService onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}
	
	public static void setDeviceStatusListener(DeviceStatusListener deviceStatusListener) {
		BridgeService.deviceStatusListener = deviceStatusListener;
	}

	public static void setPlayListener(PlayListener playListener) {
		BridgeService.playListener = playListener;
	}


	public void setRecorderListener(RecorderListener recorderListener) {
		BridgeService.recorderListener = recorderListener;
	}
	
	public static void setSearchDeviceListener(SearchDeviceListener searchDeviceListener){
		BridgeService.searchDeviceListener = searchDeviceListener;
	}
	
	

	//-------------------------------------------------------------------------
	//---------------------------以下是JNI层回调的接口-------------------------------
	//-------------------------------------------------------------------------
	
	public static void setSettingsListener(SettingsListener settingsListener) {
		BridgeService.settingsListener = settingsListener;
	}

	public void CallBack_SnapShot(long UserID, byte[] buff, int len)
	{}
	
	
	public void CallBack_GetParam(long UserID, long nType, String param) 
	{
		if(settingsListener != null)
			settingsListener.callBack_getParam(UserID, nType, param);
	}
	

	public void CallBack_SetParam(long UserID, long nType, int nResult) 
	{
		if(settingsListener != null)
			settingsListener.callBack_setParam(UserID, nType, nResult);
	}
	
	public void CallBack_Event(long UserID, long nType) 
	{
		int status = new Long(nType).intValue();
		if(deviceStatusListener != null)
			deviceStatusListener.receiveDeviceStatus(UserID,status);
	}
	
	public void VideoData(long UserID, byte[] VideoBuf, int h264Data, int nLen, int Width, int Height, int time) 
	{
		
	}
	
	public void callBackAudioData(long nUserID, byte[] pcm, int size)
	{
		if(playListener != null)
			playListener.callBackAudioData(nUserID, pcm, size);
		if(recorderListener != null)
			recorderListener.callBackAudioData(nUserID, pcm, size);
	}
	
	public void CallBack_RecordFileList(long UserID, int filecount, String fname, String strDate, int size)
	{
		if(settingsListener != null)
			settingsListener.recordFileList(UserID, filecount, fname, strDate, size);
	}
	
	public void CallBack_P2PMode(long UserID, int nType)
	{
	}

	public void CallBack_RecordPlayPos(long userid, int pos)
	{}

	public void CallBack_VideoData(long nUserID, byte[] data, int type, int size) {}
	
	public void CallBack_AlarmMessage(long UserID, int nType) 
	{
		System.out.println("................");
	}
	
	public void showNotification(String message, Device device,int nType)
	{}
	
	public void CallBack_RecordPicture(long UserID, byte[] buff, int len)
	{
	}
	
	public void CallBack_RecordFileListV2(long UserID, String param) 
	{
		
	}
	
	public void CallBack_SearchDevice(String DeviceInfo){
		try {
			JSONObject jsonObject = new JSONObject(DeviceInfo);
			String string = jsonObject.getString("DeviceID");
			Log.e("A", "-----------deviceInfo s = " + string);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("A", "-----------deviceInfo e = " + e.getMessage());
		}
//		Log.e("A", "-----------deviceInfo 1 = " + DeviceInfo);
		if(null != BridgeService.searchDeviceListener){
			
			BridgeService.searchDeviceListener.callback_SearchDevice(DeviceInfo);
		}
	}
	
	public void CallBack_RGB(long userid, byte[] rgb, int w, int h, int  bitcount)
    {

    }
	
	// 2017-05-16 new libipc.so add method start
	public void CallBack_TfPicture(long UserID, byte[] buff, int len){
		
	}
	// 2017-05-16 new libipc.so add method end
	
}
