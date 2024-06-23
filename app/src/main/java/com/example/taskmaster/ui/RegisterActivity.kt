package com.example.taskmaster.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
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

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var tvLoginNow: TextView
    private lateinit var ivEyePasswordRegister: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        usernameEditText = findViewById(R.id.etusrnmreg)
        passwordEditText = findViewById(R.id.etpassreg)
        confirmPasswordEditText = findViewById(R.id.etcpassreg)
        registerButton = findViewById(R.id.btnreg)
        tvLoginNow = findViewById(R.id.tvlognow)
        ivEyePasswordRegister = findViewById(R.id.ivShowPasswordReg)
        ivEyePasswordRegister.setImageResource(R.drawable.hideye)
        ivEyePasswordRegister.setOnClickListener(View.OnClickListener {
            if(passwordEditText.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance())

                ivEyePasswordRegister.setImageResource(R.drawable.hideye)
            }else{
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
                ivEyePasswordRegister.setImageResource(R.drawable.eyepassword)
            }

        })

        val ivEyePasswordConfirm = findViewById<ImageView>(R.id.ivShowPasswordConf)
        ivEyePasswordConfirm.setImageResource(R.drawable.hideye)
        ivEyePasswordConfirm.setOnClickListener(View.OnClickListener {
            if(confirmPasswordEditText.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                confirmPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance())

                ivEyePasswordConfirm.setImageResource(R.drawable.hideye)
            }else{
                confirmPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
                ivEyePasswordConfirm.setImageResource(R.drawable.eyepassword)
            }

        })
        registerButton.setOnClickListener {
            registerUser()
        }

        tvLoginNow.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
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
            Toast.makeText(this, "Password and Confirm Password must match", Toast.LENGTH_SHORT).show()
            return
        }

        val user = User(username, password)

        val apiService = ApiClient.getClient(null).create(AuthService::class.java)
        val call = apiService.register(user)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val newUser = response.body()
                    if (newUser != null) {
                        Toast.makeText(this@RegisterActivity, "User registered successfully", Toast.LENGTH_SHORT).show()
                        // Auto login after successful registration
                        loginUser(username, password)
                    } else {
                        Toast.makeText(this@RegisterActivity, "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "An error occurred", Toast.LENGTH_SHORT).show()
            }
        })
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
                            // Simpan token di SharedPreferences
                            val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
                            sharedPreferences.edit().putString("token", token).apply()

                            // Initialize ApiClient with the new token
                            ApiClient.getClient(token)

                            // Lanjutkan ke MainActivity
                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            intent.putExtra("USERNAME", username)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@RegisterActivity, "Login failed: No token received", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "An error occurred during login", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
