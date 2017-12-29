package com.sen5.secure.launcher.base;

import android.util.Log;

import com.sen5.secure.launcher.data.interf.FeedParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class BaseFeedParser implements FeedParser {

	// names of the XML tags
	public static final String CHANNEL = "channel";
	public static final String PUB_DATE = "pubDate";
	public static final String DESCRIPTION = "description";
	public static final String LINK = "link";
	public static final String TITLE = "title";
	public static final String ITEM = "item";

	public static final String PACKAGE_NAME = "PackageName";
	public static final String CLASS_NAME = "ClassName";
	public static final String APP = "App";
	public static final String HIDE_APPS = "HideApps";
	public static final String GROUP = "Group";
	
	public static final String PATH_SYS = "system/etc/config_launcher/";
//	private final URL feedUrl;
	private final String feedStr;

	protected BaseFeedParser(String feedUrl){
		this.feedStr = feedUrl;
	}

	@SuppressWarnings("resource")
	protected InputStream getInputStream() {
		InputStream open = null;
	    try {
//	    	if(mIsAssets){
//	    		open = LauncherApplication.mContext.getResources().getAssets().open(feedStr);
//	    		
//	    	}else{
	    		open = new FileInputStream(PATH_SYS + feedStr);
//	    	}
	    	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			 Log.e("TAG", "DomFeedParser = " + e.getMessage());
			e.printStackTrace();
			try {
				open = LauncherApplication.mContext.getResources().getAssets().open(feedStr);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return open;
		
//		try {
//			return feedUrl.openConnection().getInputStream();
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
	}
}