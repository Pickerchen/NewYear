package com.sen5.launchertools.notification;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * @author JesseYao
 * @version 2016 2016年12月13日 下午1:50:25 ClassName：NotificationService.java
 *          Description：
 */
public class NotificationService extends NotificationListenerService {

	private NLServiceReceiver mNLServicereciver;

	@Override
	public void onCreate() {
		super.onCreate();
		mNLServicereciver = new NLServiceReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(COMMAND);
		registerReceiver(mNLServicereciver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mNLServicereciver);
	}

	/**
	 * 收到通知
	 */
	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		Log.d(TAG, "ID :" + sbn.getId() + "\t" 
						+ sbn.getNotification().tickerText + "\t" 
						+ sbn.getPackageName());

		Notification notification = sbn.getNotification();
		Intent i = new Intent(NotificationPopupWindow.UPDATE);
		i.putExtras(notification.extras);
		i.putExtra(NotificationPopupWindow.CLEARABLE, sbn.isClearable());
		i.putExtra(NotificationPopupWindow.EVENT, "received: " + sbn.getPackageName() + "\n");
		i.putExtra(NotificationPopupWindow.PACKAGE_NAME, sbn.getPackageName());
		i.putExtra(NotificationPopupWindow.NOTIFICATION_ID, sbn.getId());
		i.putExtra(NotificationPopupWindow.PENDING_INTENT, notification.contentIntent);
		i.putExtra(NotificationPopupWindow.VIEW_S, notification.contentView);
		i.putExtra(NotificationPopupWindow.View_L, notification.bigContentView);
		try {
			sendBroadcast(i, BROADCAST_PERMISSION_NOTIFICATION_UPDATE);
			onBounReveive(sbn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void onBounReveive(StatusBarNotification sbn) {
		
	}

	/**
	 * 移除通知
	 */
	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		Log.d(TAG, "ID :" + sbn.getId() + "\t" 
						+ sbn.getNotification().tickerText + "\t" 
						+ sbn.getPackageName());
		Intent i = new Intent(NotificationPopupWindow.UPDATE);
		Notification notification = sbn.getNotification();
		i.putExtras(notification.extras);
		i.putExtra(NotificationPopupWindow.EVENT, "removed: " + sbn.getPackageName() + "\n");
		i.putExtra(NotificationPopupWindow.PACKAGE_NAME, sbn.getPackageName());
		i.putExtra(NotificationPopupWindow.NOTIFICATION_ID, sbn.getId());
		i.putExtra(NotificationPopupWindow.PENDING_INTENT, notification.contentIntent);
		i.putExtra(NotificationPopupWindow.VIEW_S, notification.contentView);
		i.putExtra(NotificationPopupWindow.View_L, notification.bigContentView);
		try {
			sendBroadcast(i, BROADCAST_PERMISSION_NOTIFICATION_UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class NLServiceReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String command = intent.getStringExtra(COMMAND_EXTRA);
			Log.d(TAG, "Command receive:" + command);

			if (command.equals(CANCEL_ALL)) {
				NotificationService.this.cancelAllNotifications();
			} else if (command.equals(GET_LIST)) {
				int i = 1;
				for (StatusBarNotification sbn : NotificationService.this.getActiveNotifications()) {
					Intent i2 = new Intent(NotificationPopupWindow.UPDATE);
					Notification notification = sbn.getNotification();
					i2.putExtras(notification.extras);
					i2.putExtra(NotificationPopupWindow.EVENT, i + " " + sbn.getPackageName() + "\n");
					i2.putExtra(NotificationPopupWindow.PACKAGE_NAME, sbn.getPackageName());
					i2.putExtra(NotificationPopupWindow.NOTIFICATION_ID, sbn.getId());
					i2.putExtra(NotificationPopupWindow.PENDING_INTENT, notification.contentIntent);
					i2.putExtra(NotificationPopupWindow.VIEW_S, notification.contentView);
					i2.putExtra(NotificationPopupWindow.View_L, notification.bigContentView);
					try {
						sendBroadcast(i2, BROADCAST_PERMISSION_NOTIFICATION_UPDATE);
					} catch (Exception e) {
						e.printStackTrace();
					}
					i++;
				}
			}

		}
	}

	public static final String COMMAND = "com.sen5.Launcher.COMMAND_NOTIFICATION_LISTENER_SERVICE";
	public static final String COMMAND_EXTRA = "command";
	public static final String CANCEL_ALL = "clearall";
	public static final String GET_LIST = "notification_list";
	public static final String BROADCAST_PERMISSION_NOTIFICATION_UPDATE = "com.sen5.launcher.permissions.NOTIFICATION_UPDATE";
	private static final String TAG = NotificationService.class.getSimpleName();

}
