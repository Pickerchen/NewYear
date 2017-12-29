package com.sen5.secure.launcher.utils;

import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * DES3加密/解密算法
 * Key要是以下模式
 * private byte[] key = Base64.decode("JWJjZNMlZ2hpaatsbW5vceMyc3e1dKq1".getBytes(), Base64.DEFAULT);
 * @author jok
 */
public class Des3 {
	/**
	 * DES3加密
	 * @param key - key
	 * @param data - 要加密的数据
	 * @return - 加密后的数据
	 * @throws Exception
	 */
	public static byte[] des3EncodeECB(byte[] key, byte[] data)throws Exception {
		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(key);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		deskey = keyfactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, deskey);
		byte[] bOut = cipher.doFinal(data);
		return bOut;
	}
	
	/**
	 * DES3解密
	 * @param key - key
	 * @param data - 要解密的数据
	 * @return - 解密后的数据
	 * @throws Exception
	 */
	public static byte[] des3DecodeECB(byte[] key, byte[] data)throws Exception {
		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(key);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		deskey = keyfactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, deskey);
		byte[] bOut = cipher.doFinal(data);
		return bOut;
	}
	
	/**
	 * DES3加密(字符串方式)
	 * @param key - key
	 * @param  - 要加密的数据
	 * @return - 加密后的数据
	 * @throws Exception
	 */
	public static String des3EncodeStringECB(byte[] key, String strData)throws Exception {
		//转成byte
		byte[] data = strData.getBytes("UTF-8");
		
		//加密
		byte[] encodedData = des3EncodeECB(key, data); 
		
		//转成String返回
		return new String(Base64.encode(encodedData, Base64.DEFAULT),"UTF-8");
	}
		
	/**
	 * DES3 加密(字符串方式)
	 * @param key - key
	 * @param strData - 要解密的数据
	 * @return - 解密后的数据
	 * @throws Exception
	 */
	public static String des3DecodeStringECB(byte[] key, String strData )throws Exception {
		
		//转Byte
		byte[] encodedData= Base64.decode(strData, Base64.DEFAULT);
		
		//解密
		byte[] decodedData = des3DecodeECB(key, encodedData);			
		
		//转成String返回 		
		return new String(decodedData, "UTF-8");
	}	
}
