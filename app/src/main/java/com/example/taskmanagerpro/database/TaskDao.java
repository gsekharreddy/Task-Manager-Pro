package com.example.taskmanagerpro.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

	@Insert
	long insertAndReturnId(Task task);  // Returns the inserted row ID
	@Insert
	void insert(Task task);

	@Update
	void update(Task task);

	@Delete
	void delete(Task task);

	@Query("DELETE FROM tasks")
	void deleteAll();  // 🔥 Deletes ALL tasks from the table

	@Query("SELECT * FROM tasks ORDER BY dueTimeMillis ASC")
	LiveData<List<Task>> getAllTasks();

	@Query("SELECT * FROM tasks ORDER BY dueTimeMillis ASC")
	List<Task> getAll();  // ✅ Non-LiveData version for background thread loading

	@Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
	Task getTaskById(int taskId);
}
