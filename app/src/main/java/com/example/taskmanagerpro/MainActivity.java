package com.example.taskmanagerpro;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagerpro.adapter.TaskAdapter;
import com.example.taskmanagerpro.database.AppDatabase;
import com.example.taskmanagerpro.database.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

	private RecyclerView recyclerView;
	private TaskAdapter taskAdapter;
	private List<Task> taskList = new ArrayList<>();

	// New launcher must be declared here at class level
	private ActivityResultLauncher<Intent> addTaskLauncher = registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(),
			result -> {
				if (result.getResultCode() == RESULT_OK) {
					// Reload your task list here if needed
					// But LiveData observer should auto update
					// loadTasks(); // Optional if you have loadTasks method
				}
			}
	);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

		FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

		// Only one click listener now, using new launcher
		fabAdd.setOnClickListener(v -> {
			Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
			v.startAnimation(bounce);
			v.postDelayed(() -> {
				Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
				addTaskLauncher.launch(intent);
			}, bounce.getDuration());
		});

		recyclerView = findViewById(R.id.recyclerView);

		taskAdapter = new TaskAdapter(this, taskList, this);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(taskAdapter);

		observeTasks();
	}

	private void observeTasks() {
		AppDatabase.getInstance(getApplicationContext())
				.taskDao()
				.getAllTasks()
				.observe(this, new Observer<List<Task>>() {
					@Override
					public void onChanged(List<Task> tasks) {
						taskList = tasks;
						taskAdapter.updateTasks(taskList);
					}
				});
	}

	@Override
	public void onTaskClick(Task task) {
		Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);
		intent.putExtra("taskId", task.getId());
		// You can convert this to use new ActivityResultLauncher as well if needed
		startActivity(intent);
	}

	// Remove onActivityResult, not needed with LiveData and new API
}
