package com.example.taskmanagerpro;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmanagerpro.database.AppDatabase;
import com.example.taskmanagerpro.database.Task;
import com.example.taskmanagerpro.utils.AlarmUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {

	private EditText etTitle, etDescription, etDueDate, etReminderTime;
	private Button btnSave;
	private long dueTimeMillis = 0L;
	private long reminderTimeMillis = 0L;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_task);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

		etTitle = findViewById(R.id.etTitle);
		etDescription = findViewById(R.id.etDescription);
		etDueDate = findViewById(R.id.etDueDate);
		etReminderTime = findViewById(R.id.etReminderTime);
		btnSave = findViewById(R.id.btnSave);

		// Due Date picker on click
		etDueDate.setOnClickListener(v -> showDatePicker());

		// Reminder Time picker on click
		etReminderTime.setOnClickListener(v -> showTimePicker());

		btnSave.setOnClickListener(v -> {
			Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
			v.startAnimation(bounce);
			v.postDelayed(this::saveTask, bounce.getDuration());
		});
	}

	private void showDatePicker() {
		final Calendar calendar = Calendar.getInstance();

		DatePickerDialog datePickerDialog = new DatePickerDialog(this,
				(view, year, month, dayOfMonth) -> {
					calendar.set(year, month, dayOfMonth);
					dueTimeMillis = calendar.getTimeInMillis();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					etDueDate.setText(sdf.format(new Date(dueTimeMillis)));
				},
				calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
		datePickerDialog.show();
	}

	private void showTimePicker() {
		final Calendar calendar = Calendar.getInstance();

		TimePickerDialog timePickerDialog = new TimePickerDialog(this,
				(view, hourOfDay, minute) -> {
					calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
					calendar.set(Calendar.MINUTE, minute);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
					reminderTimeMillis = calendar.getTimeInMillis();

					SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
					etReminderTime.setText(sdf.format(new Date(reminderTimeMillis)));
				},
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE),
				false);
		timePickerDialog.show();
	}

	private void saveTask() {
		String title = etTitle.getText().toString().trim();
		String description = etDescription.getText().toString().trim();

		if (TextUtils.isEmpty(title)) {
			Toast.makeText(this, "Title can't be empty!", Toast.LENGTH_SHORT).show();
			return;
		}

		if (dueTimeMillis == 0L) {
			Toast.makeText(this, "Please select a due date!", Toast.LENGTH_SHORT).show();
			return;
		}

		Task newTask = new Task(title, description, dueTimeMillis, false); // Reminder flag false for now

		// Insert in DB on background thread
		new Thread(() -> {
			long id = AppDatabase.getInstance(getApplicationContext()).taskDao().insertAndReturnId(newTask);
			newTask.setId((int) id);

			// You can extend this part to schedule reminder alarm if you want.
			// For now, no reminder checkbox so no alarms set.

			runOnUiThread(() -> {
				Toast.makeText(this, "Task saved!", Toast.LENGTH_SHORT).show();
				finish();
			});
		}).start();
	}
}
