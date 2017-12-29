package com.ipcamerasen5.main.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import java.util.Map;

/**
 * 在使用该工具类的时候 一定要先调用方法（initMySharePre）进行初始化。
 * @author jiangyicheng
 *
 */
public class MySharePre {
	private static SharedPreferences defaultSharedPreferences;
	private static Editor edit;
//	public static final String S_G_MUSIC_SWITCH = "g_music_switch";
//	public static final String S_G_SOUND_SWITCH = "g_sound_switch";
//	
//	public static final String S_WU_JING_FLAG = "wujing_flag";
	public static final String S_STORAGE_DEVICE = "default_storage";
	public static final String S_RECORD_DURATION = "default_record_duration";

	private MySharePre(){
		
	}
	@SuppressLint("CommitPrefEdits")
	public static void initMySharePre(Context context){
		defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		edit = defaultSharedPreferences.edit();
	}
	
	public static boolean containsKey(String key){
		boolean contains = defaultSharedPreferences.contains(key);
		return contains;
	}
	
	public static Map<String, ?> getAll(){
		Map<String, ?> all = defaultSharedPreferences.getAll();
		return all;
	}
	
	public static void setSharePreBoolean(String key, boolean value){
		edit.putBoolean(key, value);
		edit.commit();
	}
	
	public static boolean getSharePreBoolean(String key, boolean defValue){
		return defaultSharedPreferences.getBoolean(key, defValue);
	}
	
	public static void setSharePreInt(String key, int value){
		edit.putInt(key, value);
		edit.commit();
	}
	
	public static int getSharePreInt(String key, int defValue){
		return defaultSharedPreferences.getInt(key, defValue);
	}
	
	public static void addSharePreInt(String key, int addValue){
		int sharePreInt = getSharePreInt(key, 0);
		edit.putInt(key, addValue + sharePreInt);
		edit.commit();
	}
	
	public static void setSharePreString(String key, String value){
		edit.putString(key, value);
		edit.commit();
	}
	
	public static String getSharePreString(String key, String defValue){
		return defaultSharedPreferences.getString(key, defValue);
	}
	
	public static void setSharePreLong(String key, Long value){
		edit.putLong(key, value);
		edit.commit();
	}
	
	public static Long getSharePreLong(String key, Long defValue){
		return defaultSharedPreferences.getLong(key, defValue);
	}

	public static void commit(){
		edit.commit();
	}

}