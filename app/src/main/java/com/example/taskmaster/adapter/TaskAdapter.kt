package com.example.taskmaster.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmaster.R
import com.example.taskmaster.model.Task

class TaskAdapter(private var tasks: List<Task>, private val onDeleteClickListener: OnDeleteClickListener) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    interface OnDeleteClickListener {
        fun onDeleteClick(task: Task)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.task_card_main, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskTitleTextView: TextView = itemView.findViewById(R.id.Task)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
        private val recyclerViewTaskItems: RecyclerView =
            itemView.findViewById(R.id.recyclerViewCheckBoxCard)

        fun bind(task: Task) {
            taskTitleTextView.text = task.title
            // Set up RecyclerView for Task Items here if needed
            // Example:
            // val taskItemsAdapter = TaskItemsAdapter(task.items)
            // recyclerViewTaskItems.adapter = taskItemsAdapter

            // Handle delete button click if needed
            deleteButton.setOnClickListener {
                onDeleteClickListener.onDeleteClick(task)
            }
        }
    }
}