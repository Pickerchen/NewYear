package com.sen5.secure.launcher.utils;

import com.sen5.secure.launcher.data.entity.WorkspaceData;
import com.sen5.secure.launcher.data.interf.FeedParser;
import com.sen5.secure.launcher.data.parserxml.XmlPullFeedParser;

import java.util.List;

public class UtilXml {
	
	public static List<WorkspaceData> getDataByXml(String fileName) {
		// TODO Auto-generated method stub
		FeedParser mXmlPullFeedParser = new XmlPullFeedParser(fileName);
//		XmlPullFeedParser mXmlPullFeedParser = new XmlPullFeedParser("system/etc/launcher/hideapps/HideApps.config", false);
		List<WorkspaceData> parseHideApps = mXmlPullFeedParser.parseHideApps();
		if(null != parseHideApps){
//			for (int i = 0; i < parseHideApps.size(); i++) {
//				DLog.e("===================" + parseHideApps.get(i).getPackageName());
//			}
		}
		return parseHideApps;
	}





}
