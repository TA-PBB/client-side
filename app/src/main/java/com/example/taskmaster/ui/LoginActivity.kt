package com.example.taskmaster.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmaster.R
import com.example.taskmaster.api.ApiClient
import com.example.taskmaster.api.AuthService
import com.example.taskmaster.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        val etUsername = findViewById<EditText>(R.id.et_username)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val tvRegisterNow = findViewById<TextView>(R.id.tv_register_now)
        val ivEyePassword = findViewById<ImageView>(R.id.ivShowPassword)
        ivEyePassword.setImageResource(R.drawable.hideye)

        // Check if token exists
        val token = sharedPreferences.getString("token", null)
        if (token != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        ivEyePassword.setOnClickListener {
            if (etPassword.transformationMethod == HideReturnsTransformationMethod.getInstance()) {
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                ivEyePassword.setImageResource(R.drawable.hideye)
            } else {
                etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                ivEyePassword.setImageResource(R.drawable.eyepassword)
            }
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                loginUser(username, password)
            } else {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
            }
        }

        tvRegisterNow.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(username: String, password: String) {
        val user = User(username, password)
        val apiService = ApiClient.getClient(null).create(AuthService::class.java) // No token
        val call = apiService.login(user)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful && response.body() != null) {
                    val loggedInUser = response.body()
                    if (loggedInUser != null) {
                        val token = loggedInUser.token
                        if (token != null) {
                            // Save token in SharedPreferences
                            val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
                            sharedPreferences.edit().putString("token", token).apply()

                            // Initialize ApiClient with the new token
                            ApiClient.getClient(token)

                            // Proceed to MainActivity
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra("USERNAME", username)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Login failed: No token received", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "An error occurred during login", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
