package com.example.taskmanagerpro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagerpro.R;
import com.example.taskmanagerpro.database.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

	private final Context context;
	private List<Task> taskList;
	private final OnTaskClickListener listener;

	public interface OnTaskClickListener {
		void onTaskClick(Task task);
	}

	public TaskAdapter(Context context, List<Task> taskList, OnTaskClickListener listener) {
		this.context = context;
		this.taskList = taskList;
		this.listener = listener;
	}

	@NonNull
	@Override
	public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
		return new TaskViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
		Task task = taskList.get(position);

		holder.tvTitle.setText(task.getTitle());

		// Convert dueTimeMillis to formatted string
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
		String formattedDate = sdf.format(new Date(task.getDueTimeMillis()));
		holder.tvDueDate.setText("Due: " + formattedDate);

		holder.itemView.setOnClickListener(v -> listener.onTaskClick(task));
	}

	@Override
	public int getItemCount() {
		return taskList.size();
	}

	public void updateTasks(List<Task> newTasks) {
		this.taskList = newTasks;
		notifyDataSetChanged();
	}

	public static class TaskViewHolder extends RecyclerView.ViewHolder {
		TextView tvTitle, tvDueDate;

		public TaskViewHolder(@NonNull View itemView) {
			super(itemView);
			tvTitle = itemView.findViewById(R.id.tvTitle);
			tvDueDate = itemView.findViewById(R.id.tvDueDate);
		}
	}
}
