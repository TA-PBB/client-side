package com.example.taskmaster.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
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

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.et_username)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val tvRegisterNow = findViewById<TextView>(R.id.tv_register_now)
        val ivEyePassword = findViewById<ImageView>(R.id.ivShowPassword)
        ivEyePassword.setImageResource(R.drawable.hideye)
        ivEyePassword.setOnClickListener(View.OnClickListener {
            if(etPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance())

                ivEyePassword.setImageResource(R.drawable.hideye)
            }else{
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
                ivEyePassword.setImageResource(R.drawable.eyepassword)
            }

        })

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
                if (response.isSuccessful) {
                    val loggedInUser = response.body()
                    if (loggedInUser != null) {
                        Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()

                        // Store token in shared preferences
                        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
                        sharedPreferences.edit().putString("token", loggedInUser.token).apply()

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("USERNAME", username)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@LoginActivity, "Login failed: $errorBody", Toast.LENGTH_SHORT).show()
                    Log.e("LoginActivity", "Login failed: $errorBody")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "An error occurred", Toast.LENGTH_SHORT).show()
                Log.e("LoginActivity", "An error occurred: ${t.message}", t)
            }
        })
    }
}
