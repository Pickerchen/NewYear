package com.sen5.secure.launcher.data.parserxml;

import android.util.Xml;

import com.eric.xlee.lib.utils.LogUtil;
import com.sen5.secure.launcher.base.BaseFeedParser;
import com.sen5.secure.launcher.data.entity.Message;
import com.sen5.secure.launcher.data.entity.WorkspaceData;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;

public class XmlPullFeedParser extends BaseFeedParser {

	public XmlPullFeedParser(String feedUrl) {
		super(feedUrl);
	}

	public List<WorkspaceData> parseHideApps() {
		List<WorkspaceData> listHideApps = null;
		XmlPullParser parser = Xml.newPullParser();
		InputStream inputStream = this.getInputStream();

		try {
			// auto-detect the encoding from the stream
			if(null == inputStream){
				return null;
			}
			parser.setInput(inputStream, null);
			int eventType = parser.getEventType();
			WorkspaceData entityHideApp = null;
			boolean done = false;
			while (eventType != XmlPullParser.END_DOCUMENT && !done){
				String name = null;
				switch (eventType){
					case XmlPullParser.START_DOCUMENT:
						listHideApps = new ArrayList<WorkspaceData>();
						break;
					case XmlPullParser.START_TAG:
						name = parser.getName();

						if(name.equalsIgnoreCase(APP)){
							entityHideApp = new WorkspaceData();

						}else if(name.equalsIgnoreCase(GROUP)){
							entityHideApp.setIsAdded(parser.nextText());

						}else if(name.equalsIgnoreCase(PACKAGE_NAME)){
							entityHideApp.setPackageName(parser.nextText());
						}
						else if(name.equalsIgnoreCase(CLASS_NAME)){
							entityHideApp.setClassName(parser.nextText());
						}


						break;
					case XmlPullParser.END_TAG:
						name = parser.getName();
						if(name.equalsIgnoreCase(APP) && entityHideApp != null){
							listHideApps.add(entityHideApp);
						}
						if(name.equalsIgnoreCase(HIDE_APPS)){
							done = true;
						}
						break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			LogUtil.e("AndroidNewsPullFeedParser", e.getMessage());
			throw new RuntimeException(e);
		}finally {
            try {
                if (inputStream!=null){
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		return listHideApps;
	}



	public List<Message> parse() {
		List<Message> messages = null;
		XmlPullParser parser = Xml.newPullParser();
		try {
			// auto-detect the encoding from the stream

			parser.setInput(this.getInputStream(), null);
			int eventType = parser.getEventType();
			Message currentMessage = null;
			boolean done = false;
			while (eventType != XmlPullParser.END_DOCUMENT && !done){
				String name = null;
				switch (eventType){
					case XmlPullParser.START_DOCUMENT:
						messages = new ArrayList<Message>();
						break;
					case XmlPullParser.START_TAG:
						name = parser.getName();
						if(name.equalsIgnoreCase(CHANNEL)){
//							String attributeValue = parser.getAttributeValue("tids", "tids");
							String attributeValue = parser.getAttributeValue(0);
							LogUtil.e("------------attributeValue = " + attributeValue);
						}
						if(name.equalsIgnoreCase("PackageName")){

						}

						if (name.equalsIgnoreCase(ITEM)){
							currentMessage = new Message();
						} else if (currentMessage != null){
							if (name.equalsIgnoreCase(LINK)){
								currentMessage.setLink(parser.nextText());
							} else if (name.equalsIgnoreCase(DESCRIPTION)){
								currentMessage.setDescription(parser.nextText());
							} else if (name.equalsIgnoreCase(PUB_DATE)){
								currentMessage.setDate(parser.nextText());
							} else if (name.equalsIgnoreCase(TITLE)){
								currentMessage.setTitle(parser.nextText());
							}
						}
						break;
					case XmlPullParser.END_TAG:
						name = parser.getName();
						if (name.equalsIgnoreCase(ITEM) && currentMessage != null){
							messages.add(currentMessage);
						} else if (name.equalsIgnoreCase(CHANNEL)){
//							done = true;
						} else if(name.equalsIgnoreCase("rss")){
							done = true;
						}
						break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			LogUtil.e("AndroidNews::PullFeedParser", e.getMessage());
			throw new RuntimeException(e);
		}
		return messages;
	}
}
