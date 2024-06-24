package com.example.taskmaster.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmaster.R
import com.example.taskmaster.api.ApiClient
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
    private var taskName: String? = null
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
        taskName = intent.getStringExtra("TASK_NAME")
        editText.setText(taskName)
        saveButton.setOnClickListener {
            saveData()
        }

        backButton.setOnClickListener {

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

        }

        editText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                val taskText = editText.text.toString()
                if (taskText.isNotEmpty()) {
                    addNewCheckbox(checkboxContainer, taskText, isCheckedAPI = false)
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
            addNewCheckbox(checkboxContainer, "", false)
        }
    }

    private fun addNewCheckbox(container: LinearLayout, text: String, isCheckedAPI: Boolean) {
        val inflater = LayoutInflater.from(this)
        val newCheckboxLayout = inflater.inflate(R.layout.checkbox_layout, container, false) as LinearLayout

        val newCheckbox = newCheckboxLayout.findViewById<CheckBox>(R.id.checkbox)
        val newEditText = newCheckboxLayout.findViewById<EditText>(R.id.editText)

        newEditText.setText(text)
        newEditText.hint = "description"

        // Set the isChecked status based on the provided parameter
        newCheckbox.isChecked = isCheckedAPI

        newEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                val newTaskText = newEditText.text.toString()
                if (newTaskText.isNotEmpty()) {
                    // When adding a new checkbox from keyboard action, set isChecked to false explicitly
                    addNewCheckbox(container, newTaskText, isCheckedAPI = false)
                    newEditText.text.clear()
                    newEditText.clearFocus()
                    newEditText.requestFocus()
                }
                true
            } else {
                false
            }
        }

        newCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {
                deleteTaskItem(newEditText.text.toString(), newCheckboxLayout, container)
                container.removeView(newCheckboxLayout)
            } else {
                updateTaskItem(newEditText.text.toString(), isChecked)
            }
        }

        container.addView(newCheckboxLayout)
        newEditText.requestFocus() // Set focus to the new EditText
    }

    private fun loadTaskData(taskId: Int) {
        taskService.getTaskItems(taskId).enqueue(object : Callback<List<TaskItem>> {
            override fun onResponse(call: Call<List<TaskItem>>, response: Response<List<TaskItem>>) {
                if (response.isSuccessful) {
                    val taskItemsList = response.body() ?: emptyList()

                    taskItems.clear()
                    taskItems.addAll(taskItemsList)

                    checkboxContainer.removeAllViews() // Clear the container before adding new items

                    taskItemsList.forEach { taskItem ->
                        addNewCheckbox(checkboxContainer, taskItem.description, taskItem.completed)
                    }

                    // Check if the taskItemsList is empty or null and add a new checkbox if it is
                    if (taskItemsList.isEmpty()) {
                        addNewCheckbox(checkboxContainer, "", false)
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
            Log.d("id Task item", taskItemIndex.toString())
            taskItems[taskItemIndex] = taskItem.copy(completed = isChecked)
            taskService.updateTaskItem(taskItem.task, taskItem.id, taskItem).enqueue(object :
                Callback<TaskItem> {
                override fun onResponse(call: Call<TaskItem>, response: Response<TaskItem>) {
                    // Handle successful update
                    if (response.isSuccessful) {
                        Log.e("DetailPage", "Response success: Task item updated successfully")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("DetailPage", "Failed to update task item: ${response.code()} $errorBody")
                        Log.e("DetailPage", "Response message: ${response.message()}")

                    }
                }

                override fun onFailure(call: Call<TaskItem>, t: Throwable) {
                    // Handle failure

                }
            })
        }
    }

    private fun deleteTaskItem(description: String, checkboxLayout: LinearLayout, container: LinearLayout) {
        val taskItemIndex = taskItems.indexOfFirst { it.description == description }
        if (taskItemIndex != -1) {
            val taskItem = taskItems[taskItemIndex]
            taskItems.removeAt(taskItemIndex)
            taskService.deleteTaskItem(taskItem.task, taskItem.id).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // Handle successful deletion
                        Log.e("DetailPage", "Response success: Task item deleted successfully")
                        container.removeView(checkboxLayout)
                        // Check if container is empty and add new checkbox if it is
                        if (container.childCount == 0) {
                            addNewCheckbox(container, "", false)
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("DetailPage", "Failed to delete task item: ${response.code()} $errorBody")
                        Log.e("DetailPage", "Response message: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // Handle failure
                    Log.e("DetailPage", "Delete task failed", t)
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
                    Log.e("DetailPage", "Response success: $savedTask")
                    saveTaskItems(savedTask.id, onComplete)
                    Toast.makeText(this@DetailPage, "Task saved successfully", Toast.LENGTH_SHORT).show()
                }else {
                    Log.e("DetailPage", "Save task failed with response code: ${response.code()}")
                    Log.e("DetailPage", "Response message: ${response.message()}")
                    Toast.makeText(this@DetailPage, "Task saved Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Task>, t: Throwable) {
                // Handle failure
                Log.e("DetailPage", "Save task failed", t)
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
                    val existingItemMap = existingItems.associateBy { it.id }

                    val containerChildCount = checkboxContainer.childCount
                    for (i in 0 until containerChildCount) {
                        val child = checkboxContainer.getChildAt(i) as LinearLayout
                        val checkBox = child.findViewById<CheckBox>(R.id.checkbox)
                        val editText = child.findViewById<EditText>(R.id.editText)
                        val description = editText.text.toString()

                        // Cari item yang cocok berdasarkan posisi child view
                        val existingItem = if (i < existingItems.size) existingItems[i] else null

                        val taskItem = if (existingItem != null) {
                            existingItem.copy(description = description, completed = checkBox.isChecked)
                        } else {
                            TaskItem(id = 0, task = taskId, id_task = taskId, description = description, completed = checkBox.isChecked, created_at = "", updated_at = "")
                        }

                        val taskItemCall = if (existingItem != null) {
                            taskService.updateTaskItem(taskId, existingItem.id, taskItem)
                        } else {
                            taskService.createTaskItem(taskId, taskItem)
                        }

                        taskItemCall.enqueue(object : Callback<TaskItem> {
                            override fun onResponse(call: Call<TaskItem>, response: Response<TaskItem>) {
                                if (response.isSuccessful) {
                                    // Handle successful item save/update
                                    val updatedTaskItem = response.body()
                                    Log.d("SaveTaskItems", "Task item saved successfully: $updatedTaskItem")
                                } else {
                                    // Handle unsuccessful item save/update
                                    val errorBody = response.errorBody()?.string()
                                    Log.e("SaveTaskItems", "Failed to save task item: ${response.code()} ${errorBody}")
                                }
                            }

                            override fun onFailure(call: Call<TaskItem>, t: Throwable) {
                                // Handle failure
                                Log.e("SaveTaskItems", "Failure to save task item: ${t.message}", t)
                                // Log request details
                                Log.e("SaveTaskItems", "Request failed: ${call.request()}")
                            }
                        })
                    }

                    onComplete?.invoke()
                } else {
                    Log.e("SaveTaskItems", "Failed to fetch task items: ${response.code()} ${response.message()}")
                    onComplete?.invoke()
                }
            }

            override fun onFailure(call: Call<List<TaskItem>>, t: Throwable) {
                // Handle failure
                Log.e("SaveTaskItems", "Failed to fetch task items: ${t.message}", t)
                onComplete?.invoke()
            }
        })
    }

}
