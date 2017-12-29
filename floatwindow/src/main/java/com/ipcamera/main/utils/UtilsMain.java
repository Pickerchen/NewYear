package com.ipcamera.main.utils;


import android.os.Build;

public class UtilsMain {
	public static boolean getVersionIs905(){
		int sdk = Build.VERSION.SDK_INT;
		if(23 > sdk && sdk >= 21){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean getVersionIs905X(){
		if(Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= 23){
			return true;
		}else{
			return false;
		}
		
	}
}
