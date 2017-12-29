package com.nes.ipc;

import android.content.Context;
import android.content.SharedPreferences;


public class VersionManager {

	private static VersionManager mInstance;
	public final static int VERSION_TYPE_1 = 0;	// 原来的版本，按照文件列表搜索
	public final static int VERSION_TYPE_2 = 1;	// 新的版本，按照时间段搜索
	private int nVersionType ;
	private boolean bSuppurtYuyan 	;
	private int nYuyanCamDirect 	;
	
	private int nDataType 	= 2;  	//请求视频流类型
	private int nChannelNo 	= 0;	//通道号
	private int nStreamType = 1; 	//默认是子码流
	
	private boolean bIsEnc = false;
	
	private SharedPreferences mySharedPreferences;
	private final String sSpStr = "VersionManager";
	private final String sEncKey = "enc";
	private final String sYuyanKey = "yuyanEnable";
	private final String sYuyanCamDirect = "yuyanCamDirect";
	private Context mContext;
	
	public void setContext(Context context)
	{
		this.mContext = context;
		mySharedPreferences = mContext.getSharedPreferences(sSpStr, 0);
	}
	
	private VersionManager()
	{
		nVersionType 	= VERSION_TYPE_1;
		bIsEnc 			= false; 			//默认是不加密的
		bSuppurtYuyan 	= false;			//默认不支持鱼眼
		nChannelNo		= 0;				//默认为通道0
	}
	
	public void setVersionType(int nVersionType)
	{
		if (nVersionType != VERSION_TYPE_1 
				&& nVersionType != VERSION_TYPE_2)
		{
			return ;
		}
		
		this.nVersionType = nVersionType;
	}
	
	public int getDataType()
	{
		return nDataType;
	}
	
	public int getVersionType()
	{
		return nVersionType;
	}
	
	public int getStreamType()
	{
		return this.nStreamType;
	}
	
	public void setStreamType(int nSteamType)
	{
		this.nStreamType = nSteamType;
	}
	
	public void setDataType(int nDataType)
	{
		this.nDataType = nDataType;
	}
	
	public int getChannelNo()
	{
		return this.nChannelNo;
	}
	
	public void setChannelNo(int nChannelNo)
	{
		this.nChannelNo = nChannelNo;
	}
	
	public static VersionManager getInstance()
	{
		if (mInstance == null)
		{
			mInstance = new VersionManager();
		}
		
		return mInstance;
	}
	
	//设置是否加密
	public void setIfNeedEnc(boolean bIsEnc)
	{
		this.bIsEnc = bIsEnc;
		SharedPreferences.Editor  editor  =  mySharedPreferences.edit();
		editor.putBoolean(sEncKey, bIsEnc);
		editor.commit();
	}
	
	//获取是否是加密版本
	public boolean getIfEnc()
	{
		this.bIsEnc = mySharedPreferences.getBoolean(sEncKey, false);
		return this.bIsEnc;
	}
	
}
