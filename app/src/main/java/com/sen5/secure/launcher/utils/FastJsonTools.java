package com.sen5.secure.launcher.utils;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FastJsonTools {

	private static final String TAG = FastJsonTools.class.getSimpleName();
	
	/**
	 * 用fastjson 将json字符串解析为一个 JavaBean
	 * 
	 * @param
	 * @param cls
	 * @return
	 */
	public static<T> T getItem(String json, Class<T> cls) {
		
		T t = null;
		try {
			t = JSON.parseObject(json, cls);
		} catch (Exception e) {
			Log.e(TAG, "Error == " + e.toString());
		}
		return t;
	}
	
	/**
	 * 用fastjson 将json字符串 解析成为一个 List<JavaBean> 及 List<String>
	 * 
	 * @param json
	 * @param cls
	 * @return
	 */
	public static <T> List<T> getList(String json, Class<T> cls) {
		List<T> list = new ArrayList<T>();
		try {
			list = JSON.parseArray(json, cls);
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, "Error == " + e.toString());
		}
		return list;
	}

	/**
	 * 用fastjson 将jsonString 解析成 List<Map<String,Object>>
	 * 
	 * @param json
	 * @return
	 */
	public static List<Map<String, Object>> getListMap(String json) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			// 两种写法
			// list = JSON.parseObject(jsonString, new
			// TypeReference<List<Map<String, Object>>>(){}.getType());

			list = JSON.parseObject(json,
					new TypeReference<List<Map<String, Object>>>() {
					});
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, "Error == " + e.toString());
		}
		return list;

	}
	
}
