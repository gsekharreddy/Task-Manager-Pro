package com.example.taskmanagerpro.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.taskmanagerpro.R;
import com.example.taskmanagerpro.TaskDetailActivity;

public class ReminderReceiver extends BroadcastReceiver {

	private static final String CHANNEL_ID = "task_reminder_channel";
	private static final String CHANNEL_NAME = "Task Reminders";

	@Override
	public void onReceive(Context context, Intent intent) {
		int taskId = intent.getIntExtra("taskId", -1);
		String taskTitle = intent.getStringExtra("taskTitle");

		Log.d("ReminderReceiver", "Alarm received for taskId: " + taskId + ", Title: " + taskTitle);

		// Quick Toast to check if receiver triggers
		Toast.makeText(context, "Reminder: " + (taskTitle != null ? taskTitle : "Task"), Toast.LENGTH_SHORT).show();

		// Intent to open TaskDetailActivity when notification is tapped
		Intent openIntent = new Intent(context, TaskDetailActivity.class);
		openIntent.putExtra("taskId", taskId);

		PendingIntent pendingIntent = PendingIntent.getActivity(
				context,
				taskId,
				openIntent,
				PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
		);

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
			if (manager != null) {
				manager.createNotificationChannel(channel);
			}
		}

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
				.setSmallIcon(R.drawable.ic_notification)  // make sure this icon exists!
				.setContentTitle("Task Reminder 🔔")
				.setContentText(taskTitle != null ? taskTitle : "You have a task reminder!")
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setContentIntent(pendingIntent)
				.setAutoCancel(true)
				.setDefaults(NotificationCompat.DEFAULT_ALL);  // vibrate, sound, lights

		if (manager != null) {
			manager.notify(taskId, builder.build());
		}
	}
}
