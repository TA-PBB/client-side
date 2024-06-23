import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmaster.R
import com.example.taskmaster.api.TaskService
import com.example.taskmaster.model.Task
import com.example.taskmaster.model.TaskItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class   TaskAdapter(private var tasks: List<Task>, private val taskService: TaskService,    private val listener: OnTaskClickListener) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    interface OnTaskClickListener {
        fun onTaskClick(taskId: Int, taskName: String)
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
        private val recyclerViewTaskItems: RecyclerView = itemView.findViewById(R.id.recyclerViewCheckBoxCard)


        fun bind(task: Task) {
            taskTitleTextView.text = task.title

            val taskItemsAdapter = TaskItemAdapter(emptyList()) // Initialize adapter with empty list
            recyclerViewTaskItems.layoutManager = LinearLayoutManager(itemView.context)
            recyclerViewTaskItems.adapter = taskItemsAdapter

            // Load task items for this task
            loadTaskItems(task.id, taskItemsAdapter)


            // Handle delete button click if needed
            deleteButton.setOnClickListener {
                listener.onDeleteClick(task)
            }
            itemView.setOnClickListener {
                listener.onTaskClick(task.id, task.title)
            }
        }
    }

    private fun loadTaskItems(taskId: Int, adapter: TaskItemAdapter) {
        val call = taskService.getTaskItems(taskId)

        call.enqueue(object : Callback<List<TaskItem>> {
            override fun onResponse(call: Call<List<TaskItem>>, response: Response<List<TaskItem>>) {
                if (response.isSuccessful) {
                    val taskItems = response.body()
                    Log.d("Body Task", taskItems.toString())
//                    Log.d("Body Task", "Success")
                    taskItems?.let {
                        adapter.updateTaskItems(it.take(3)) // Update RecyclerView adapter with fetched task items, limit to 4
                    }
                } else {
                    Log.d("TaskItem", "Failed to retrieve")
//                    Toast.makeText(itemView.context, "Failed to retrieve task items: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<TaskItem>>, t: Throwable) {
                Log.d("TaskItem", "Failure to retrieve")
//                Toast.makeText(itemView.context, "Failed to retrieve task items: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}


