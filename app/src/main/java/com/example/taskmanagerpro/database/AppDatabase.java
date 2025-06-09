package com.example.taskmanagerpro.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Task.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

	private static volatile AppDatabase INSTANCE;

	public abstract TaskDao taskDao();

	private static final ExecutorService databaseWriteExecutor =
			Executors.newFixedThreadPool(4);

	public static AppDatabase getInstance(Context context) {
		if (INSTANCE == null) {
			synchronized (AppDatabase.class) {
				if (INSTANCE == null) {
					INSTANCE = Room.databaseBuilder(
									context.getApplicationContext(),
									AppDatabase.class,
									"task_manager_db"
							)
							.fallbackToDestructiveMigration()
							.addCallback(prepopulateCallback) // 💡 Prepopulate
							.build();
				}
			}
		}
		return INSTANCE;
	}

	private static final Callback prepopulateCallback = new Callback() {
		@Override
		public void onCreate(@NonNull SupportSQLiteDatabase db) {
			super.onCreate(db);
			databaseWriteExecutor.execute(() -> {
				TaskDao dao = INSTANCE.taskDao();

				long now = System.currentTimeMillis();
				for (int i = 1; i <= 20; i++) {
					dao.insertAndReturnId(new Task(
							"Task " + i,
							"This is task number " + i,
							now + (i * 3600000), // due time increasing every hour
							i % 2 == 0 // reminder set for even-numbered tasks
					));
				}
			});
		}
	};
}
