package hsl.p2pipcam.entity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

public class EntityDevice {
	// {
	// “IP” : “192.168.1.100”,
	// “Port” : 81,
	// “DeviceID” : “HSL-000001-XXXXX”,
	// “DeviceName” : “WIFICAM”,
	// “Mac” : “00-00-00-00-00-00”,
	// “DeviceType” : 0,
	// “SmartConnect” : 0
	// }
	public String IP;
	public int Port;
	public String DeviceID;
	public String DeviceName;
	public String Mac;
	public int DeviceType;
	public int SmartConnect;
	public Bitmap bitmap;
	public String status = "1";//状态：在线，离线，录制:1,0,2
	public String videoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "IPCamera"+File.separator+"lastFrame"+File.separator+DeviceID+ File.separator+DeviceID+".jpg";;//缩略图地址
	public Drawable drawable;

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if(o instanceof EntityDevice){
			if(TextUtils.isEmpty(((EntityDevice) o).DeviceID) || 
					TextUtils.isEmpty(this.DeviceID) ||
					((EntityDevice) o).DeviceID.equals(this.DeviceID)){
				return true;
			}
		}
		return super.equals(o);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
//		return super.toString();
		return "IP = " + IP + "  Port = " + Port + "  DeviceID = " + DeviceID
				+"  DeviceName = " + DeviceName + "   Mac = " + Mac + "   DeviceType = "
				+ DeviceType + "   SmartConnect = " + SmartConnect;
		
	}
	
//	public String getDeviceID(){
//		if(TextUtils.isEmpty(DeviceID)){
//			
//			return DeviceID;
//		}
//		return DeviceID.replaceAll("-", "");
//	}

}
