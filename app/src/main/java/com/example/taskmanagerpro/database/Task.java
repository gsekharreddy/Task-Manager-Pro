package com.example.taskmanagerpro.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "tasks")
public class Task {

	@PrimaryKey(autoGenerate = true)
	private int id;

	@NonNull
	private String title;

	private String description;

	private long dueTimeMillis;  // For storing alarm/reminder time

	private boolean reminderEnabled;

	// 🔧 Constructors
	public Task(@NonNull String title, String description, long dueTimeMillis, boolean reminderEnabled) {
		this.title = title;
		this.description = description;
		this.dueTimeMillis = dueTimeMillis;
		this.reminderEnabled = reminderEnabled;
	}

	// ⚙️ Getters & Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@NonNull
	public String getTitle() {
		return title;
	}

	public void setTitle(@NonNull String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getDueTimeMillis() {
		return dueTimeMillis;
	}

	public void setDueTimeMillis(long dueTimeMillis) {
		this.dueTimeMillis = dueTimeMillis;
	}

	public boolean isReminderEnabled() {
		return reminderEnabled;
	}

	public void setReminderEnabled(boolean reminderEnabled) {
		this.reminderEnabled = reminderEnabled;
	}
}
