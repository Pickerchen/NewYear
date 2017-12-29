package com.nes.ipc;


public class Encrypt {

	private final static int PWD_LENGTH = 16;
	private final static String ENCRY_KEY = "lbtech";

	public static byte[] getEncryPwdByte(String pwd){
		byte[] test = new byte[16];
		byte[] tmp = pwd.getBytes();
		final int len = pwd.length();
		for (int i = 0; i < len; i++) {
			test[i] = tmp[i];
		}
		if (len < test.length) {
			test[len] = '\0';
		}
		return EncrypKey(test, PWD_LENGTH, len, ENCRY_KEY.getBytes());
	}

	public static byte[] EncrypKey(byte[] src, int passwordLen, int len, byte[] key) {// lbtech
		// int KeyPos = -1;
		// int SrcPos = 0;
		// int SrcAsc = 0;
		// // time_t t;
		// int KeyLen = key.length;
		// if (KeyLen == 0)
		// return "";
		// // srand((unsigned) time(&t));
		// int offset = 0x0A;
		// String dest = "";
		//
		// for (int i = 0; i < src.length; i++) {
		// SrcAsc = (src[i] + offset) % 255;
		//
		// if (KeyPos < KeyLen - 1)
		// KeyPos++;
		// else
		// KeyPos = 0;
		//
		// SrcAsc = SrcAsc ^ key[KeyPos];
		//
		// String strSrcAsc;
		// strSrcAsc = SrcAsc + "";
		// String str_SrcAsc = strSrcAsc;
		//
		// dest = dest + str_SrcAsc;
		// offset = SrcAsc;
		// }
		// dest = dest + "\0";
		// return dest;
		// TODO
		int KeyPos = -1;
		int SrcPos = 0;
		int SrcAsc = 0;
		// time_t t;

		int KeyLen = key.length;
		if (KeyLen == 0)
			return null;

		// srand((unsigned) time(&t));
		int offset = 0x0A;
		// byte[] czTmp = new byte[32];
		byte[] dest = new byte[passwordLen];
		for (int i = 0; i < len; i++) {
			SrcAsc = src[i];// (src[i] + offset) % 255;

			if (KeyPos < KeyLen - 1)
				KeyPos++;
			else
				KeyPos = 0;

			SrcAsc = SrcAsc ^ (key[KeyPos] + 0x19);// SrcAsc ^ key[KeyPos];

			dest[i] = (byte) SrcAsc;
			offset = SrcAsc;
		}

//		Log.e("", "Encryp result:" + new String(dest));
		return dest;

	}
	
	public static String decry(byte[] src, int passwordLen, int len,
                               byte[] key ){
		
		int keyPos = -1;
		int SrcPos = 0; 
		int SrcAsc = 0;
		
		int keyLen = key.length;
		if( keyLen == 0){
			return null;
		}
		int offset = 0x0A;
		byte[] dest = new byte[passwordLen];
		for( int i = 0; i < len; i ++){
			SrcAsc = src[i];
			if( keyPos < keyLen - 1){
				keyPos ++;
			}
			else {
				keyPos = 0;
			}
			
			SrcAsc = SrcAsc ^ (key[keyPos] - 0x19);
			
			dest[i] =  (byte) SrcAsc;
			offset = SrcAsc;
		}
		
		return new String(dest);
	}

}
