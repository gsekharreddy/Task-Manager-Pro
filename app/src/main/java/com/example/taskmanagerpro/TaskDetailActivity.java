package com.example.taskmanagerpro;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
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

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
	private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

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

		// Disable manual input, force using pickers
		etDueDate.setFocusable(false);
		etReminder.setFocusable(false);

		etDueDate.setOnClickListener(v -> showDatePicker());
		etReminder.setOnClickListener(v -> showTimePicker());

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

					// Set separate date and time strings
					etDueDate.setText(dateFormat.format(dueCalendar.getTime()));
					etReminder.setText(timeFormat.format(dueCalendar.getTime()));

					cbReminder.setChecked(currentTask.isReminderEnabled());
				} else {
					Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show();
					finish();
				}
			});
		}).start();
	}

	private void showDatePicker() {
		int year = dueCalendar.get(Calendar.YEAR);
		int month = dueCalendar.get(Calendar.MONTH);
		int day = dueCalendar.get(Calendar.DAY_OF_MONTH);

		DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, y, m, d) -> {
			dueCalendar.set(Calendar.YEAR, y);
			dueCalendar.set(Calendar.MONTH, m);
			dueCalendar.set(Calendar.DAY_OF_MONTH, d);

			etDueDate.setText(dateFormat.format(dueCalendar.getTime()));
		}, year, month, day);

		datePickerDialog.show();
	}

	private void showTimePicker() {
		int hour = dueCalendar.get(Calendar.HOUR_OF_DAY);
		int minute = dueCalendar.get(Calendar.MINUTE);

		TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, h, m) -> {
			dueCalendar.set(Calendar.HOUR_OF_DAY, h);
			dueCalendar.set(Calendar.MINUTE, m);
			dueCalendar.set(Calendar.SECOND, 0);

			etReminder.setText(timeFormat.format(dueCalendar.getTime()));
		}, hour, minute, false);

		timePickerDialog.show();
	}

	private void updateTaskInDb() {
		String title = etTitle.getText().toString().trim();
		String desc = etDescription.getText().toString().trim();
		boolean isReminder = cbReminder.isChecked();

		if (title.isEmpty() || etDueDate.getText().toString().isEmpty() || etReminder.getText().toString().isEmpty()) {
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
