package com.example.taskmaster.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        usernameEditText = findViewById(R.id.etusrnmreg)
        passwordEditText = findViewById(R.id.etpassreg)
        confirmPasswordEditText = findViewById(R.id.etcpassreg)
        registerButton = findViewById(R.id.btnreg)

//        registerButton.setOnClickListener {
//            registerUser()
//        }
    }

//    private fun registerUser() {
//        val username = usernameEditText.text.toString().trim()
//        val password = passwordEditText.text.toString().trim()
//        val confirmPassword = confirmPasswordEditText.text.toString().trim()
//
//        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
//            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if (password != confirmPassword) {
//            Toast.makeText(this, "Password dan Konfirmasi Password harus sama", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val user = User(username, password, confirmPassword)
//
//        val apiService = ApiClient.getClient().create(AuthService::class.java)
//        val call = apiService.register(user)
//        call.enqueue(object : Callback<Void> {
//            override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                if (response.isSuccessful) {
//                    Toast.makeText(this@RegisterActivity, "Pengguna berhasil didaftarkan", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this@RegisterActivity, "Registrasi gagal", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<Void>, t: Throwable) {
//                Toast.makeText(this@RegisterActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
}