package com.ajh.zhh.android.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class NotificationUtils {
	public static <T extends Activity> void showNotification(Context context,
			int icon, String showTitle, String title, String content,
			Class<T> clazz) {
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, showTitle, when);
		Intent intent = new Intent();
		intent.setClass(context, clazz);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		notification.contentIntent = contentIntent;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
	//	notification.sound = Uri.parse("");
		notification.setLatestEventInfo(context, title, content, contentIntent);
		notificationManager.notify(0, notification);
	}
}
