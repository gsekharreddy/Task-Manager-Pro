package com.example.taskmanagerpro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagerpro.adapter.TaskAdapter;
import com.example.taskmanagerpro.database.AppDatabase;
import com.example.taskmanagerpro.database.Task;
import com.example.taskmanagerpro.utils.AlarmUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	private RecyclerView recyclerView;
	private TaskAdapter taskAdapter;
	private AppDatabase db;

	private FloatingActionButton fabAdd, fabDeleteAll;
	private View parentLayout;

	private Task recentlyDeletedTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

		parentLayout = findViewById(android.R.id.content);
		recyclerView = findViewById(R.id.recyclerView);
		fabAdd = findViewById(R.id.fabAdd);
		fabDeleteAll = findViewById(R.id.fabDeleteAll);

		db = AppDatabase.getInstance(getApplicationContext());

		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		taskAdapter = new TaskAdapter(this, new ArrayList<>(), task -> {
			Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);
			intent.putExtra("taskId", task.getId());
			startActivity(intent);
		});

		recyclerView.setAdapter(taskAdapter);

		// Add task
		fabAdd.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, AddTaskActivity.class)));

		// Delete all tasks
		fabDeleteAll.setOnClickListener(view -> showDeleteAllConfirmation());

		// Enable swipe to delete
		setupSwipeToDelete();

		loadTasks();
	}

	private void loadTasks() {
		new Thread(() -> {
			List<Task> taskList = db.taskDao().getAll();
			if (taskList != null) {
				Collections.sort(taskList, Comparator.comparingLong(Task::getDueDateMillis)); // Sort by due date
			}
			runOnUiThread(() -> taskAdapter.setTasks(taskList));
		}).start();
	}

	private void showDeleteAllConfirmation() {
		new AlertDialog.Builder(this)
				.setTitle("Delete All Tasks")
				.setMessage("Are you sure you want to delete all tasks? This action cannot be undone.")
				.setPositiveButton("Delete", (dialog, which) -> deleteAllTasks())
				.setNegativeButton("Cancel", null)
				.show();
	}

	private void deleteAllTasks() {
		new Thread(() -> {
			db.taskDao().deleteAll();
			runOnUiThread(() -> {
				taskAdapter.setTasks(new ArrayList<>());
				Toast.makeText(this, "All tasks deleted", Toast.LENGTH_SHORT).show();
			});
		}).start();
	}

	private void setupSwipeToDelete() {
		ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
			@Override
			public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
				return false;
			}

			@Override
			public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
				int position = viewHolder.getAdapterPosition();
				Task task = taskAdapter.getTaskAt(position);
				recentlyDeletedTask = task;

				taskAdapter.removeTask(position);

				AlarmUtils.cancelAlarm(getApplicationContext(), task.getId());

				// Delete from DB
				new Thread(() -> db.taskDao().delete(task)).start();

				// Show Snackbar to undo
				Snackbar.make(parentLayout, "Task deleted", Snackbar.LENGTH_LONG)
						.setAction("UNDO", v -> undoDelete(task))
						.show();
			}
		};

		new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
	}

	private void undoDelete(Task task) {
		new Thread(() -> {
			db.taskDao().insert(task); // Reinsert
			runOnUiThread(this::loadTasks);

			// Restore alarm
			AlarmUtils.setAlarm(getApplicationContext(), task.getId(), task.getTitle(), task.getDueDateMillis());
		}).start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadTasks();
	}
}
