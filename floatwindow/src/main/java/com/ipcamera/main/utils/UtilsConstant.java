package com.ipcamera.main.utils;

import android.os.SystemProperties;

public class UtilsConstant {
	public static final String CAMERA = "Camera";
	public static boolean VERSON_905;
	
	public static final String DEFAULT_DID = "error";
	
	
	public static final boolean isDebug = SystemProperties.getBoolean("persist.sys.jyc.debug", false);
}
