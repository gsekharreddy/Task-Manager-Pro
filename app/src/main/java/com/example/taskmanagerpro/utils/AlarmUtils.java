package com.example.taskmanagerpro.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.taskmanagerpro.receiver.ReminderReceiver;

public class AlarmUtils {

	public static void setAlarm(Context context, int taskId, String taskTitle, long triggerAtMillis) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(context, ReminderReceiver.class);
		intent.putExtra("taskId", taskId);
		intent.putExtra("taskTitle", taskTitle);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context,
				taskId,  // unique request code per task
				intent,
				PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
		);

		if (alarmManager != null) {
			alarmManager.setExactAndAllowWhileIdle(
					AlarmManager.RTC_WAKEUP,
					triggerAtMillis,
					pendingIntent
			);
		}
	}

	public static void cancelAlarm(Context context, int taskId) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(context, ReminderReceiver.class);
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
