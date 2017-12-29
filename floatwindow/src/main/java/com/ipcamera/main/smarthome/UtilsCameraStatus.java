package com.ipcamera.main.smarthome;

import android.content.Context;

import com.ipcamera.main.R;
import com.ipcamera.main.utils.DLog;

public class UtilsCameraStatus {
	
	private static final int CAMERA_CONNECTING = 0;
	private static final int CAMERA_ONLINE = 100;
	private static final int CAMERA_ERROR = 101;
	private static final int CAMERA_TIME_OUT = 10;
	private static final int CAMERA_OFF_LINE = 9;
	private static final int CAMERA_ID_ERROR = 5;
	private static final int CAMERA_BREAK = 11;
	private static final int CAMERA_LOGIN_REEOR = 1;
	
	public static String getStatusStringByStatusID(Context context, int id){
		String str = "";
		DLog.e("-----------getStatusStringByStatusID = " + id);
		switch (id) {
		case CAMERA_CONNECTING:
//			str = "连接中...";
			str = context.getString(R.string.status_connecting);
			break;
		case CAMERA_ONLINE:
//			str = "在线!";
			str = context.getString(R.string.status);
			break;
		case CAMERA_ERROR:
//			str = "连接错误";
			str = context.getString(R.string.status_off_line);
			break;
		case CAMERA_TIME_OUT:
//			str = "连接超时";
			str = context.getString(R.string.status_off_line);
			break;
		case CAMERA_OFF_LINE:
//			str = "离线";
			str = context.getString(R.string.status_off_line);
			break;
		case CAMERA_ID_ERROR:
//			str = "离线[无效ID]";
			str = context.getString(R.string.status_off_line);
			break;
		case CAMERA_BREAK:
//			str = "断开";
			str = context.getString(R.string.status_off_line);
			break;
		case CAMERA_LOGIN_REEOR:
//			str = "用户名密码错误";
			str = context.getString(R.string.status_off_line);
			break;
		default:
			break;
		}
		return str;
	}
	
	public static boolean getStatusBooleanByStatusID(int id){
		boolean bool = false;
		switch (id) {
		case CAMERA_CONNECTING:
//			str = "连接中...";
			bool = false;
			break;
		case CAMERA_ONLINE:
//			str = "在线啊";
			bool = true;
			break;
		case CAMERA_ERROR:
//			str = "连接错误";
			bool = false;
			break;
		case CAMERA_TIME_OUT:
//			str = "连接超时";
			bool = false;
			break;
		case CAMERA_OFF_LINE:
//			str = "离线";
			bool = false;
			break;
		case CAMERA_ID_ERROR:
//			str = "离线[无效ID]";
			bool = false;
			break;
		case CAMERA_BREAK:
//			str = "断开";
			bool = false;
			break;
		case CAMERA_LOGIN_REEOR:
//			str = "用户名密码错误";
			bool = false;
			break;
		
		default:
			break;
		}
		return bool;
	}
}
