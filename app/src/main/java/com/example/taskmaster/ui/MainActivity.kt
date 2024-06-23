package com.example.taskmaster.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmaster.R
import com.example.taskmaster.api.ApiClient
import com.example.taskmaster.api.AuthService
import com.example.taskmaster.adapter.TaskAdapter
import com.example.taskmaster.api.TaskService
import com.example.taskmaster.model.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() ,  TaskAdapter.OnDeleteClickListener{

    private lateinit var logoutButton: ImageButton
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private val sharedPreferences by lazy { getSharedPreferences("app_prefs", MODE_PRIVATE) }
    private val token by lazy { sharedPreferences.getString("token", null) }
    private val taskService by lazy { ApiClient.getClient(token).create(TaskService::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewTaskCard)
        recyclerView.layoutManager = LinearLayoutManager(this)

        taskAdapter = TaskAdapter(emptyList(), this) // Initialize adapter with empty list
        recyclerView.adapter = taskAdapter

        getTasks()

        logoutButton = findViewById(R.id.logoutBtn)

        logoutButton.setOnClickListener {
            logout()
        }

        val username = intent.getStringExtra("USERNAME") ?: "User"
        val greetingTextView: TextView = findViewById(R.id.greetingTextView)
        greetingTextView.text = "Hi, $username"

        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        if (token == null) {
            // Redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // User is already logged in, proceed with main activity functionality
        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            // Start DetailScreenActivity
            val intent = Intent(this, DetailPage::class.java)
            startActivity(intent)
        }

    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            val apiService = ApiClient.getClient(token).create(AuthService::class.java)
            val call = apiService.logout()
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MainActivity, "Logout Successful", Toast.LENGTH_SHORT).show()
                        // Clear session data
                        sharedPreferences.edit().remove("token").apply()

                        // Navigate to LoginActivity
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity, "Logout failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "No token found, unable to logout", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getTasks() {

        val call = taskService.getTasks()

        call.enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                if (response.isSuccessful) {
                    val tasks = response.body()
                    Log.d("Body Task", tasks.toString())
//                    taskAdapter.updateTasks(tasks)
                    tasks?.let {
                        taskAdapter.updateTasks(it) // Update RecyclerView adapter with fetched tasks
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Failed to retrieve tasks: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed to retrieve tasks: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteTask(task: Task) {
        val call = taskService.deleteTask(task.id)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Task deleted successfully", Toast.LENGTH_SHORT).show()
                    // Optionally, update UI by fetching tasks again or removing from adapter directly
                    getTasks()
                } else {
                    Toast.makeText(this@MainActivity, "Failed to delete task: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@MainActivity, "An error occurred while deleting task", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDeleteClick(task: Task) {
        deleteTask(task)
    }
}