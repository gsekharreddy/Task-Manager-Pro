package com.example.taskmanagerpro.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {

	@PrimaryKey(autoGenerate = true)
	public int id;

	public String title;
	public String description;
	public String dueDate;
	public String reminderTime;

	public Task(String title, String description, String dueDate, String reminderTime) {
		this.title = title;
		this.description = description;
		this.dueDate = dueDate;
		this.reminderTime = reminderTime;
	}
}
