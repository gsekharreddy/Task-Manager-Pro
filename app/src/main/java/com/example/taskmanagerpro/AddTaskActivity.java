package com.example.taskmanagerpro;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmanagerpro.database.AppDatabase;
import com.example.taskmanagerpro.database.Task;
import com.example.taskmanagerpro.utils.AlarmUtils;

import java.util.Calendar;
import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {

	private EditText etTitle, etDescription, etDueDate, etReminderTime;
	private RadioGroup rgReminderType;
	private Button btnSave;
	private long dueDateMillis = 0L;
	private int reminderHour = -1, reminderMinute = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_task);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

		etTitle = findViewById(R.id.etTitle);
		etDescription = findViewById(R.id.etDescription);
		etDueDate = findViewById(R.id.etDueDate);
		etReminderTime = findViewById(R.id.etReminderTime);
		rgReminderType = findViewById(R.id.rgReminderType);
		btnSave = findViewById(R.id.btnSave);

		etDueDate.setOnClickListener(v -> showDatePicker());

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
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);

					dueDateMillis = calendar.getTimeInMillis();
					etDueDate.setText(android.text.format.DateFormat.format("dd/MM/yyyy", new Date(dueDateMillis)));
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
					reminderHour = hourOfDay;
					reminderMinute = minute;
					calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
					calendar.set(Calendar.MINUTE, minute);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);

					etReminderTime.setText(android.text.format.DateFormat.format("hh:mm a", calendar));
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

		if (dueDateMillis == 0L) {
			Toast.makeText(this, "Please select a due date!", Toast.LENGTH_SHORT).show();
			return;
		}

		long tempReminderMillis = dueDateMillis;

		if (reminderHour >= 0 && reminderMinute >= 0) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(dueDateMillis);
			calendar.set(Calendar.HOUR_OF_DAY, reminderHour);
			calendar.set(Calendar.MINUTE, reminderMinute);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			tempReminderMillis = calendar.getTimeInMillis();
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(dueDateMillis);
			calendar.set(Calendar.HOUR_OF_DAY, 9);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			tempReminderMillis = calendar.getTimeInMillis();
		}

		final long reminderMillis = tempReminderMillis;

		if (reminderMillis < System.currentTimeMillis()) {
			Toast.makeText(this, "Reminder time must be in the future!", Toast.LENGTH_SHORT).show();
			return;
		}

		Task newTask = new Task(title, description, dueDateMillis, false);

		// Get selected reminder type
		int selectedId = rgReminderType.getCheckedRadioButtonId();
		boolean useAlarm = (selectedId == R.id.rbAlarm);

		new Thread(() -> {
			long id = AppDatabase.getInstance(getApplicationContext()).taskDao().insertAndReturnId(newTask);
			newTask.setId((int) id);

			// Set alarm or notification based on user choice
			if (useAlarm) {
				AlarmUtils.setAlarm(getApplicationContext(), newTask.getId(), newTask.getTitle(), reminderMillis);
			} else {
				AlarmUtils.setNotification(getApplicationContext(), newTask.getId(), newTask.getTitle(), reminderMillis);
			}

			runOnUiThread(() -> {
				String msg = useAlarm ? "Task saved and alarm set!" : "Task saved and notification set!";
				Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
				finish();
			});
		}).start();
	}
}
