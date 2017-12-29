package com.sen5.launchertools.notification;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

/**
 * @author JesseYao
 * @version 2016 2016年12月13日 下午8:21:04 ClassName：NTBean.java Description：
 */
public class NTBean {
	String info;
	String title;
	String text;
	String subText;
	// Icon smallIcon;
	Bitmap largeIcon;
	RemoteViews viewS, viewL;
	PendingIntent intent;
	String packageName;
	int notificationId;
	boolean clearable;
	
	@Override
	public boolean equals(Object o) {
		NTBean bean = (NTBean)o;
		if (bean.packageName.equals(packageName) &&
				bean.notificationId == notificationId) {
			return true;
		}
		return false;
	}
}
