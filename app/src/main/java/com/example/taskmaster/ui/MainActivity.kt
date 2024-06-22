package com.example.taskmaster.ui

import DetailScreenActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.mykotlinclientapp.api.ApiClient
import com.example.taskmaster.R
import com.example.taskmaster.api.AuthService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var logoutButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logoutButton = findViewById(R.id.logoutBtn)

        logoutButton.setOnClickListener {
            logout()
        }

        val username = intent.getStringExtra("USERNAME") ?: "User"
        val greetingTextView: TextView = findViewById(R.id.greetingTextView)
        greetingTextView.text = "Hi, $username"

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            // Start DetailScreenActivity
            val intent = Intent(this, DetailScreenActivity::class.java)
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
                        Toast.makeText(this@MainActivity, "Logout failed", Toast.LENGTH_SHORT).show()
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

}
