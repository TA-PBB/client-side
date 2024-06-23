import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmaster.R
import com.example.taskmaster.model.TaskItem

class TaskItemAdapter(private var taskItems: List<TaskItem>) : RecyclerView.Adapter<TaskItemAdapter.TaskItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.checkbox_layout, parent, false)
        return TaskItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskItemViewHolder, position: Int) {
        val taskItem = taskItems[position]
        holder.bind(taskItem)
    }

    override fun getItemCount(): Int {
        return taskItems.size
    }

    fun updateTaskItems(newTaskItems: List<TaskItem>) {
        taskItems = newTaskItems
        notifyDataSetChanged()
    }

    inner class TaskItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
        private val editText: EditText = itemView.findViewById(R.id.editText)

        fun bind(taskItem: TaskItem) {
            editText.setText(taskItem.description)
            checkbox.isChecked = taskItem.completed
            checkbox.isEnabled = false
            editText.isFocusable = false



        }
    }
}
