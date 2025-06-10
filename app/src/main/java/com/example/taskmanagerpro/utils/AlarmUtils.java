package com.example.taskmanagerpro.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.taskmanagerpro.receiver.ReminderReceiver;

public class AlarmUtils {

	// Set exact alarm that works even in Doze mode (Android M+)
	public static void setAlarm(Context context, int taskId, String taskTitle, long triggerAtMillis) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(context, ReminderReceiver.class);
		intent.putExtra("taskId", taskId);
		intent.putExtra("taskTitle", taskTitle);
		intent.putExtra("reminderType", "alarm"); // distinguish type

		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context,
				taskId,
				intent,
				PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
		);

		if (alarmManager != null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				alarmManager.setExactAndAllowWhileIdle(
						AlarmManager.RTC_WAKEUP,
						triggerAtMillis,
						pendingIntent
				);
			} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				alarmManager.setExact(
						AlarmManager.RTC_WAKEUP,
						triggerAtMillis,
						pendingIntent
				);
			} else {
				alarmManager.set(
						AlarmManager.RTC_WAKEUP,
						triggerAtMillis,
						pendingIntent
				);
			}
		}
	}

	// Set notification alarm similarly
	public static void setNotification(Context context, int taskId, String taskTitle, long triggerAtMillis) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(context, ReminderReceiver.class);
		intent.putExtra("taskId", taskId);
		intent.putExtra("taskTitle", taskTitle);
		intent.putExtra("reminderType", "notification");

		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context,
				taskId,
				intent,
				PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
		);

		if (alarmManager != null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				alarmManager.setExactAndAllowWhileIdle(
						AlarmManager.RTC_WAKEUP,
						triggerAtMillis,
						pendingIntent
				);
			} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				alarmManager.setExact(
						AlarmManager.RTC_WAKEUP,
						triggerAtMillis,
						pendingIntent
				);
			} else {
				alarmManager.set(
						AlarmManager.RTC_WAKEUP,
						triggerAtMillis,
						pendingIntent
				);
			}
		}
	}

	// Cancel alarm properly by matching the exact Intent
	public static void cancelAlarm(Context context, int taskId) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(context, ReminderReceiver.class);
		intent.putExtra("taskId", taskId);  // **IMPORTANT: add taskId here to match original intent**
		intent.putExtra("reminderType", "alarm");

		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context,
				taskId,
				intent,
				PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
		);

		if (alarmManager != null) {
			alarmManager.cancel(pendingIntent);
		}
	}

	// Cancel notification alarm similarly
	public static void cancelNotification(Context context, int taskId) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(context, ReminderReceiver.class);
		intent.putExtra("taskId", taskId);  // **Add this here too**
		intent.putExtra("reminderType", "notification");

		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context,
				taskId,
				intent,
				PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
		);

		if (alarmManager != null) {
			alarmManager.cancel(pendingIntent);
		}
	}
}