package com.example.taskmanagerpro.service;

public class NotificationService {
}
RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.notification_task);
remoteView.setTextViewText(R.id.tvTaskTitle, taskTitle);
remoteView.setTextViewText(R.id.tvTaskTime, "Due: " + dueTime);
// optional: set image or button action

Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
		.setSmallIcon(R.drawable.ic_notification)
		.setCustomContentView(remoteView)
		.setPriority(NotificationCompat.PRIORITY_HIGH)
		.build();

NotificationManagerCompat manager = NotificationManagerCompat.from(context);
manager.notify(NOTIFICATION_ID, notification);
