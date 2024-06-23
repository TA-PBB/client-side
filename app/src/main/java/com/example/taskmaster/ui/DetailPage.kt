package com.example.taskmaster.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import com.example.mykotlinclientapp.api.ApiClient
import com.example.taskmaster.R
import com.example.taskmaster.api.TaskService
import com.example.taskmaster.model.Task
import com.example.taskmaster.model.TaskItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailPage : AppCompatActivity() {

    private lateinit var saveButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var editText: EditText
    private lateinit var checkboxContainer: LinearLayout

    private var taskId: Int? = null
    private val sharedPreferences by lazy { getSharedPreferences("app_prefs", MODE_PRIVATE) }
    private val token by lazy { sharedPreferences.getString("token", null) }
    private val taskService by lazy { ApiClient.getClient(token).create(TaskService::class.java) }
    private val taskItems = mutableListOf<TaskItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_page)

        saveButton = findViewById(R.id.saveBtn)
        backButton = findViewById(R.id.backBtn)
        editText = findViewById(R.id.Task)
        checkboxContainer = findViewById(R.id.checkboxContainer)

        taskId = intent.getIntExtra("TASK_ID", -1).takeIf { it != -1 }

        saveButton.setOnClickListener {
            saveData()
        }

        backButton.setOnClickListener {
            saveData {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        editText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                val taskText = editText.text.toString()
                if (taskText.isNotEmpty()) {
                    addNewCheckbox(checkboxContainer, taskText)
                    editText.text.clear()
                }
                true
            } else {
                false
            }
        }

        if (taskId != null) {
            loadTaskData(taskId!!)
        } else {
            addNewCheckbox(checkboxContainer, "")
        }
    }

    private fun addNewCheckbox(container: LinearLayout, text: String) {
        val inflater = LayoutInflater.from(this)
        val newCheckboxLayout = inflater.inflate(R.layout.checkbox_layout, container, false) as LinearLayout

        val newCheckbox = newCheckboxLayout.findViewById<CheckBox>(R.id.checkbox)
        val newEditText = newCheckboxLayout.findViewById<EditText>(R.id.editText)
        newEditText.setText(text)
        newEditText.hint = "description"

        newEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                val newTaskText = newEditText.text.toString()
                if (newTaskText.isNotEmpty()) {
                    addNewCheckbox(container, newTaskText)
                    newEditText.text.clear()
                }
                true
            } else {
                false
            }
        }

        newCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {
                deleteTaskItem(newEditText.text.toString())
                container.removeView(newCheckboxLayout)
            } else {
                updateTaskItem(newEditText.text.toString(), isChecked)
            }
        }

        container.addView(newCheckboxLayout)
    }

    private fun loadTaskData(taskId: Int) {
        taskService.getTaskItems(taskId).enqueue(object : Callback<List<TaskItem>> {
            override fun onResponse(call: Call<List<TaskItem>>, response: Response<List<TaskItem>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        taskItems.clear()
                        taskItems.addAll(it)
                        taskItems.forEach { taskItem ->
                            addNewCheckbox(checkboxContainer, taskItem.description)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<TaskItem>>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun updateTaskItem(description: String, isChecked: Boolean) {
        val taskItemIndex = taskItems.indexOfFirst { it.description == description }
        if (taskItemIndex != -1) {
            val taskItem = taskItems[taskItemIndex]
            taskItems[taskItemIndex] = taskItem.copy(completed = isChecked)
            taskService.updateTaskItem(taskItem.id_task, taskItem.id, taskItem).enqueue(object :
                Callback<TaskItem> {
                override fun onResponse(call: Call<TaskItem>, response: Response<TaskItem>) {
                    // Handle successful update
                }

                override fun onFailure(call: Call<TaskItem>, t: Throwable) {
                    // Handle failure
                }
            })
        }
    }

    private fun deleteTaskItem(description: String) {
        val taskItemIndex = taskItems.indexOfFirst { it.description == description }
        if (taskItemIndex != -1) {
            val taskItem = taskItems[taskItemIndex]
            taskItems.removeAt(taskItemIndex)
            taskService.deleteTaskItem(taskItem.id_task, taskItem.id).enqueue(object :
                Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    // Handle successful deletion
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // Handle failure
                }
            })
        }
    }

    private fun saveData(onComplete: (() -> Unit)? = null) {
        val title = editText.text.toString().takeIf { it.isNotEmpty() } ?: "New Title"
        val task = Task(id = taskId ?: 0, user = 1, title = title, created_at = "", updated_at = "")

        val taskCall = if (taskId == null) {
            taskService.createTask(task)
        } else {
            taskService.updateTask(taskId!!, task)
        }

        taskCall.enqueue(object : Callback<Task> {
            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                if (response.isSuccessful) {
                    val savedTask = response.body()!!
                    saveTaskItems(savedTask.id, onComplete)
                }
            }

            override fun onFailure(call: Call<Task>, t: Throwable) {
                // Handle failure
                onComplete?.invoke()
            }
        })
    }

    private fun saveTaskItems(taskId: Int, onComplete: (() -> Unit)?) {
        val taskItemsCall = taskService.getTaskItems(taskId)

        taskItemsCall.enqueue(object : Callback<List<TaskItem>> {
            override fun onResponse(call: Call<List<TaskItem>>, response: Response<List<TaskItem>>) {
                if (response.isSuccessful) {
                    val existingItems = response.body() ?: emptyList()
                    val existingItemIds = existingItems.map { it.id }.toSet()

                    val containerChildCount = checkboxContainer.childCount
                    for (i in 0 until containerChildCount) {
                        val child = checkboxContainer.getChildAt(i) as LinearLayout
                        val checkBox = child.findViewById<CheckBox>(R.id.checkbox)
                        val editText = child.findViewById<EditText>(R.id.editText)
                        val description = editText.text.toString()

                        val taskItem = TaskItem(id = 0, id_task = taskId, description = description, completed = checkBox.isChecked, created_at = "", updated_at = "")
                        val taskItemCall = if (existingItemIds.contains(taskItem.id)) {
                            taskService.updateTaskItem(taskId, taskItem.id, taskItem)
                        } else {
                            taskService.createTaskItem(taskId, taskItem)
                        }

                        taskItemCall.enqueue(object : Callback<TaskItem> {
                            override fun onResponse(call: Call<TaskItem>, response: Response<TaskItem>) {
                                // Handle successful item save/update
                            }

                            override fun onFailure(call: Call<TaskItem>, t: Throwable) {
                                // Handle failure
                            }
                        })
                    }

                    onComplete?.invoke()
                } else {
                    onComplete?.invoke()
                }
            }

            override fun onFailure(call: Call<List<TaskItem>>, t: Throwable) {
                // Handle failure
                onComplete?.invoke()
            }
        })
    }
}
