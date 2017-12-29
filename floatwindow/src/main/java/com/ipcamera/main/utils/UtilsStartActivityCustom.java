package com.ipcamera.main.utils;

import hsl.p2pipcam.entity.EntityDevice;

import android.app.LauncherActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class UtilsStartActivityCustom {
	private static final String DEFAULT_DID = "default_did";
	private static final String DEFAULT_POSITION = "default_position";
	
	public static void startFullCamera(Context context, int position, String did){
		ComponentName component = new ComponentName("com.ipcamerasen5.main", "com.ipcamerasen5.main.MainActivity");
		
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		intent.putExtra(DEFAULT_DID, did);
		intent.putExtra(DEFAULT_POSITION, position);
		
		Utils.startActivityByComponentName(context, intent, component);
	}


	public static void startFullCameraNew(Context context, int position, String did){


		Intent intent = new Intent();
		intent.setPackage("com.sen5.secure.launcher");
		intent.setComponent(new ComponentName(context, "com.ipcamerasen5.main.MainActivity"));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		intent.putExtra(DEFAULT_DID, did);
		intent.putExtra(DEFAULT_POSITION, position);
		context.startActivity(intent);
	}
}
