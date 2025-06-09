package com.example.taskmanagerpro.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.taskmanagerpro.database.AppDatabase;
import com.example.taskmanagerpro.database.Task;
import com.example.taskmanagerpro.database.TaskDao;

public class TaskContentProvider extends ContentProvider {

	public static final String AUTHORITY = "com.example.taskmanagerpro.provider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/tasks");

	private static final int TASKS = 1;
	private static final int TASK_ID = 2;

	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static {
		uriMatcher.addURI(AUTHORITY, "tasks", TASKS);
		uriMatcher.addURI(AUTHORITY, "tasks/#", TASK_ID);
	}

	private TaskDao taskDao;

	@Override
	public boolean onCreate() {
		Context context = getContext();
		if (context != null) {
			taskDao = AppDatabase.getInstance(context).taskDao();
			return true;
		}
		return false;
	}

	@Nullable
	@Override
	public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
						@Nullable String selection, @Nullable String[] selectionArgs,
						@Nullable String sortOrder) {

		Cursor cursor;
		switch (uriMatcher.match(uri)) {
			case TASKS:
				cursor = AppDatabase.getInstance(getContext()).getOpenHelper()
						.getReadableDatabase()
						.query("SELECT * FROM tasks", null);
				break;
			case TASK_ID:
				long id = ContentUris.parseId(uri);
				cursor = AppDatabase.getInstance(getContext()).getOpenHelper()
						.getReadableDatabase()
						.query("SELECT * FROM tasks WHERE id = ?", new String[]{String.valueOf(id)});
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		// Notify changes if cursor is observed
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Nullable
	@Override
	public String getType(@NonNull Uri uri) {
		switch (uriMatcher.match(uri)) {
			case TASKS:
				return "vnd.android.cursor.dir/vnd." + AUTHORITY + ".tasks";
			case TASK_ID:
				return "vnd.android.cursor.item/vnd." + AUTHORITY + ".tasks";
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	@Nullable
	@Override
	public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
		if (uriMatcher.match(uri) == TASKS && values != null) {
			Task task = new Task(
					values.getAsString("title"),
					values.getAsString("description"),
					values.getAsLong("dueTimeMillis"),
					values.getAsBoolean("reminderEnabled")
			);
			AppDatabase db = AppDatabase.getInstance(getContext());
			long id = db.taskDao().insertAndReturnId(task);
			Uri newUri = ContentUris.withAppendedId(CONTENT_URI, id);
			getContext().getContentResolver().notifyChange(newUri, null);
			return newUri;
		} else {
			throw new IllegalArgumentException("Insert not supported for " + uri);
		}
	}

	@Override
	public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
		int rowsDeleted = 0;
		switch (uriMatcher.match(uri)) {
			case TASK_ID:
				long id = ContentUris.parseId(uri);
				Task task = taskDao.getTaskById((int) id);
				if (task != null) {
					taskDao.delete(task);
					rowsDeleted = 1;
				}
				break;
			default:
				throw new IllegalArgumentException("Delete not supported for " + uri);
		}

		if (rowsDeleted > 0)
			getContext().getContentResolver().notifyChange(uri, null);

		return rowsDeleted;
	}

	@Override
	public int update(@NonNull Uri uri, @Nullable ContentValues values,
					  @Nullable String selection, @Nullable String[] selectionArgs) {
		if (uriMatcher.match(uri) == TASK_ID && values != null) {
			long id = ContentUris.parseId(uri);
			Task task = taskDao.getTaskById((int) id);
			if (task != null) {
				task.setTitle(values.getAsString("title"));
				task.setDescription(values.getAsString("description"));
				task.setDueTimeMillis(values.getAsLong("dueTimeMillis"));
				task.setReminderEnabled(values.getAsBoolean("reminderEnabled"));
				taskDao.update(task);
				getContext().getContentResolver().notifyChange(uri, null);
				return 1;
			}
		}
		throw new IllegalArgumentException("Update not supported for " + uri);
	}
}
