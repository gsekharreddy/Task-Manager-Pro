package com.example.taskmanagerpro;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmanagerpro.database.AppDatabase;
import com.example.taskmanagerpro.database.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TaskDetailActivity extends AppCompatActivity {

	private EditText etTitle, etDescription, etDueDate, etReminder;
	private CheckBox cbReminder;
	private Button btnUpdate, btnDelete;

	private int taskId;
	private Task currentTask;
	private final Calendar dueCalendar = Calendar.getInstance();

	private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_detail);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

		initViews();

		taskId = getIntent().getIntExtra("taskId", -1);
		if (taskId == -1) {
			Toast.makeText(this, "Invalid Task", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		loadTask();

		etDueDate.setOnClickListener(v -> showDateTimePicker());
		btnUpdate.setOnClickListener(v -> updateTaskInDb());
		btnDelete.setOnClickListener(v -> deleteTaskInDb());
	}

	private void initViews() {
		etTitle = findViewById(R.id.etDetailTitle);
		etDescription = findViewById(R.id.etDetailDescription);
		etDueDate = findViewById(R.id.etDetailDueDate);
		etReminder = findViewById(R.id.etDetailReminder);
		cbReminder = findViewById(R.id.cbReminder);
		btnUpdate = findViewById(R.id.btnUpdate);
		btnDelete = findViewById(R.id.btnDelete);
	}

	private void loadTask() {
		new Thread(() -> {
			currentTask = AppDatabase.getInstance(getApplicationContext()).taskDao().getTaskById(taskId);

			runOnUiThread(() -> {
				if (currentTask != null) {
					etTitle.setText(currentTask.getTitle());
					etDescription.setText(currentTask.getDescription());
					dueCalendar.setTimeInMillis(currentTask.getDueTimeMillis());
					etDueDate.setText(dateTimeFormat.format(dueCalendar.getTime()));
					etReminder.setText(dateTimeFormat.format(dueCalendar.getTime()));  // optional
					cbReminder.setChecked(currentTask.isReminderEnabled());
				} else {
					Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show();
					finish();
				}
			});
		}).start();
	}

	private void showDateTimePicker() {
		new DatePickerDialog(this, (view, year, month, day) -> {
			dueCalendar.set(Calendar.YEAR, year);
			dueCalendar.set(Calendar.MONTH, month);
			dueCalendar.set(Calendar.DAY_OF_MONTH, day);

			new TimePickerDialog(this, (view1, hour, minute) -> {
				dueCalendar.set(Calendar.HOUR_OF_DAY, hour);
				dueCalendar.set(Calendar.MINUTE, minute);
				dueCalendar.set(Calendar.SECOND, 0);

				String formatted = dateTimeFormat.format(dueCalendar.getTime());
				etDueDate.setText(formatted);
				etReminder.setText(formatted);
			}, dueCalendar.get(Calendar.HOUR_OF_DAY), dueCalendar.get(Calendar.MINUTE), false).show();

		}, dueCalendar.get(Calendar.YEAR), dueCalendar.get(Calendar.MONTH), dueCalendar.get(Calendar.DAY_OF_MONTH)).show();
	}

	private void updateTaskInDb() {
		String title = etTitle.getText().toString().trim();
		String desc = etDescription.getText().toString().trim();
		boolean isReminder = cbReminder.isChecked();

		if (title.isEmpty() || etDueDate.getText().toString().isEmpty()) {
			Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
			return;
		}

		currentTask.setTitle(title);
		currentTask.setDescription(desc);
		currentTask.setDueTimeMillis(dueCalendar.getTimeInMillis());
		currentTask.setReminderEnabled(isReminder);

		new Thread(() -> {
			AppDatabase.getInstance(getApplicationContext()).taskDao().update(currentTask);
			runOnUiThread(() -> {
				Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
				finish();
			});
		}).start();
	}

	private void deleteTaskInDb() {
		new Thread(() -> {
			AppDatabase.getInstance(getApplicationContext()).taskDao().delete(currentTask);
			runOnUiThread(() -> {
				Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
				finish();
			});
		}).start();
	}
}
