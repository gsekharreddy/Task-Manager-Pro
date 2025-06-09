package com.example.taskmanagerpro.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.taskmanagerpro.R;
import com.example.taskmanagerpro.TaskDetailActivity;

public class NotificationService extends Service {

	private static final String CHANNEL_ID = "task_reminder_channel";
	private static final String CHANNEL_NAME = "Task Reminders";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		int taskId = intent.getIntExtra("taskId", -1);
		String taskTitle = intent.getStringExtra("taskTitle");

		// Intent to open TaskDetailActivity
		Intent notificationIntent = new Intent(this, TaskDetailActivity.class);
		notificationIntent.putExtra("taskId", taskId);

		PendingIntent pendingIntent = PendingIntent.getActivity(
				this,
				taskId,
				notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
		);

		// Create notification channel
		NotificationManager notificationManager =
				(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(
					CHANNEL_ID,
					CHANNEL_NAME,
					NotificationManager.IMPORTANCE_HIGH
			);
			channel.setDescription("Used by NotificationService for task reminders");
			channel.enableLights(true);
			channel.setLightColor(Color.YELLOW);
			channel.enableVibration(true);
			notificationManager.createNotificationChannel(channel);
		}

		// Build the notification
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
				.setSmallIcon(R.drawable.ic_notification)
				.setContentTitle("Scheduled Task Notification 🔔")
				.setContentText(taskTitle != null ? taskTitle : "Reminder: Check your task")
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setContentIntent(pendingIntent)
				.setAutoCancel(true);

		// Show it
		notificationManager.notify(taskId, builder.build());

		// Stop service after showing
		stopSelf();

		return START_NOT_STICKY;
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
