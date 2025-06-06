📱 Application Name: Task Manager Pro
🧩 Components Used:
Component	Description
Activity	MainActivity displays tasks and navigates to add/view/update screens.
Service	NotificationService runs in background to send reminders for tasks.
BroadcastReceiver	ReminderReceiver listens to alarms and triggers notifications.
ContentProvider	TaskContentProvider shares task data with other apps.
Room Database	Stores task info (title, description, due date, etc.).

Key Features:
Add, edit, delete, and view tasks.

Set reminders using AlarmManager.

Receive notifications at scheduled times.

Data stored locally using Room (SQLite).

Exposes task data through a custom ContentProvider.

Suggested File Structure:
arduino
Copy
Edit
com.example.taskmanager
├── MainActivity.java
├── AddTaskActivity.java
├── TaskDetailActivity.java
├── database/
│   ├── AppDatabase.java
│   ├── Task.java
│   └── TaskDao.java
├── service/
│   └── NotificationService.java
├── receiver/
│   └── ReminderReceiver.java
├── provider/
│   └── TaskContentProvider.java
├── utils/
