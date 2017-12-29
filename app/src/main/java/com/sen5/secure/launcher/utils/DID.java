package com.sen5.secure.launcher.utils;

import android.util.Log;

import com.sen5.secure.launcher.base.LauncherApplication;

public class DID {
	
	/**
	 * 从盒子的key里获取连接p2p服务器的key。
	 * 盒子key解密后是22位，截取前16位，并在第6、13位插入"_"作为连接p2p服务器的key
	 * @param key
	 * @return
	 */
	public static String getDID(String key) {
		
		String strDID = "";
		if(null != key) {
			try {
				StringBuilder sbKey = new StringBuilder(Dec3Decode.decodeDidKey(key));
				sbKey.insert(5, "-");
				sbKey.insert(12, "-");
				strDID = sbKey.substring(0, 18);
			} catch (Exception e) {
				if(LauncherApplication.DEBUG) {
					Log.e("DID", "error == " + e.getMessage());
				}
			}
		}
		return strDID;		
	}
}
