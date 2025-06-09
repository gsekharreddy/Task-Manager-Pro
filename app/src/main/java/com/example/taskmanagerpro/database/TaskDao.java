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
	long insertAndReturnId(Task task);  // Changed to return long ID

	@Update
	void update(Task task);

	@Delete
	void delete(Task task);

	// You can keep this LiveData version for your MainActivity recyclerView updates
	@Query("SELECT * FROM tasks ORDER BY dueTimeMillis ASC")
	LiveData<List<Task>> getAllTasks();

	@Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
	Task getTaskById(int taskId);
}
