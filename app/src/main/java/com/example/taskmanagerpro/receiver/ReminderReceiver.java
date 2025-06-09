package com.example.taskmanagerpro.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.taskmanagerpro.R;
import com.example.taskmanagerpro.TaskDetailActivity;

public class ReminderReceiver extends BroadcastReceiver {

	private static final String CHANNEL_ID = "task_reminder_channel";
	private static final String CHANNEL_NAME = "Task Reminders";

	@Override
	public void onReceive(Context context, Intent intent) {
		// Get task info from intent extras
		int taskId = intent.getIntExtra("taskId", -1);
		String taskTitle = intent.getStringExtra("taskTitle");

		// 🔔 Open TaskDetailActivity on tap
		Intent openIntent = new Intent(context, TaskDetailActivity.class);
		openIntent.putExtra("taskId", taskId);

		PendingIntent pendingIntent = PendingIntent.getActivity(
				context,
				taskId,
				openIntent,
				PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
		);

		// ✅ Ensure notification channel exists
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(
					CHANNEL_ID,
					CHANNEL_NAME,
					NotificationManager.IMPORTANCE_HIGH
			);
			channel.setDescription("Reminders for your tasks");
			channel.enableLights(true);
			channel.setLightColor(Color.CYAN);
			channel.enableVibration(true);
			manager.createNotificationChannel(channel);
		}

		// 🔥 Build the notification
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
				.setSmallIcon(R.drawable.ic_notification) // use a custom icon from drawable
				.setContentTitle("Task Reminder 🔔")
				.setContentText(taskTitle != null ? taskTitle : "You have a task reminder!")
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setContentIntent(pendingIntent)
				.setAutoCancel(true);

		// 💣 Fire it
		manager.notify(taskId, builder.build());
	}
}
