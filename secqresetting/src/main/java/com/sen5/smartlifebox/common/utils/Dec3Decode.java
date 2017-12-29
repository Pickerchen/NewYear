package com.sen5.smartlifebox.common.utils;

import android.util.Base64;
import android.util.Log;

public class Dec3Decode {

	private static final String TAG = Dec3Decode.class.getSimpleName();
	private static byte[] mKey = Base64.decode("NPkjZGWmZ2hpamtsbb5vcLAyc6R1dnd1".getBytes(), Base64.DEFAULT);
	
	/**
	 * 解密
	 * @param decodedDidKey
	 * @return
	 * @throws Exception
	 */
	public static String decodeDidKey(String decodedDidKey) {
		
		String didKey = "";
		try {
			didKey = Des3.des3DecodeStringECB(mKey, decodedDidKey);			
		} catch (Exception e) {
			Log.e(TAG, "error == " + e.getMessage());
		}
		return didKey;
	}
}
