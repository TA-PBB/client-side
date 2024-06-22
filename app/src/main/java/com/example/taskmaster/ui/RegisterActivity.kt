package com.example.taskmaster.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mykotlinclientapp.api.ApiClient
import com.example.taskmaster.R
import com.example.taskmaster.api.AuthService
import com.example.taskmaster.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var tvLoginNow: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        usernameEditText = findViewById(R.id.etusrnmreg)
        passwordEditText = findViewById(R.id.etpassreg)
        confirmPasswordEditText = findViewById(R.id.etcpassreg)
        registerButton = findViewById(R.id.btnreg)
        tvLoginNow = findViewById(R.id.tvlognow)

        registerButton.setOnClickListener {
            registerUser()
        }

        tvLoginNow.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent) git gg
        }

    }

    private fun registerUser() {
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Password and Confirm Password must match", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val user = User(username, password)

        val apiService = ApiClient.create(null).create(AuthService::class.java)
        val call = apiService.register(user)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "User registered successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    loginUser(username, password)
                } else {
                    Toast.makeText(this@RegisterActivity, "Registration failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "An error occurred", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun loginUser(username: String, password: String) {
        val user = User(username, password)
        val apiService = ApiClient.create(null).create(AuthService::class.java)
        val call = apiService.login(user)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()?.token
                    if (token != null) {
                        val editor = sharedPreferences.edit()
                        editor.putString("token", token)
                        editor.apply()
                        Toast.makeText(
                            this@RegisterActivity,
                            "Login successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Navigate to main activity with username
                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        intent.putExtra("USERNAME", username)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Login failed: No token received",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(
                    this@RegisterActivity,
                    "An error occurred during login",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}