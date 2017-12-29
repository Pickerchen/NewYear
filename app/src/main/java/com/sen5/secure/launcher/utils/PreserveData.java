package com.sen5.secure.launcher.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 配置文件的读写
 */
public class PreserveData {

	private SharedPreferences settings = null;
	private Context context;
	/**
	 * 
	 * @param context
	 */
	public PreserveData(Context context) {
		this.context = context;
	}
	
	//////////////////////////////////////////////////////////////////////////
	/**
	 * 保存boolean
	 * @param fileName
	 * @param strKey
	 * @param value
	 */
	public void writeBool(String fileName, String strKey, boolean value) {
		
		settings = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(strKey, value);
		editor.commit();
	}
	
	/**
	 * 读取boolean
	 * @param fileName
	 * @param strKey
	 * @return
	 */
	public boolean readBool(String fileName, String strKey, boolean bDefault) {
		
		settings = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		boolean result = settings.getBoolean(strKey, bDefault);
		return result;
	} 
	
	/////////////////////////////////////////////////////////////////////////
	/**
	 * 保存float
	 * @param fileName
	 * @param strKey
	 * @param fltValue
	 */
	public void writeFloat(String fileName, String strKey, float fltValue) {
		
		settings = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat(strKey, fltValue);
		editor.commit();
	}
	
	/**
	 * 读取float
	 * @param fileName
	 * @param strKey
	 * @return
	 */
	public float readFloat(String fileName, String strKey, float fDefault) {
		
		settings = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		float fltResult = settings.getFloat(strKey, fDefault);
		return fltResult;
	} 
	
	//////////////////////////////////////////////////////////////////////////
	/**
	 * 保存Int
	 * @param fileName
	 * @param strKey
	 * @param value
	 */
	public void writeInt(String fileName, String strKey, int value) {
		
		settings = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(strKey, value);
		editor.commit();
	}
	
	/**
	 * 读取Int
	 * @param fileName
	 * @param strKey
	 * @return
	 */
	public int readInt(String fileName, String strKey, int nDefault) {
		
		settings = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		int result = settings.getInt(strKey, nDefault);
		return result;
	} 

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 保存Long
	 * @param fileName
	 * @param strKey
	 * @param lngValue
	 */
	public void writeLong(String fileName, String strKey, long lngValue) {
		
		settings = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(strKey, lngValue);
		editor.commit();
	}
	
	/**
	 * 读取Long
	 * @param fileName
	 * @param strKey
	 * @return
	 */
	public long readLong(String fileName, String strKey, long lDefault) {
		
		settings = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		long lngResult = settings.getLong(strKey, lDefault);
		return lngResult;
	} 
	
	///////////////////////////////////////////////////////////////////////////
	/**
	 * 保存String
	 * @param fileName
	 * @param strKey
	 * @param strValue
	 */
	public void writeString(String fileName, String strKey, String strValue) {

		settings = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit(); // 让settings处于编辑状态
		editor.putString(strKey, strValue); // 存放数据
		editor.commit(); // 完成提交
	}
	
	/**
	 * 读取String
	 * @param fileName
	 * @param strKey
	 * @return
	 */
	public String readString(String fileName, String strKey, String strDefault) {
		
		settings = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		String strResult = settings.getString(strKey, strDefault); // 取出数据
		return strResult;
	}
}

