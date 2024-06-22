package com.example.taskmaster.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmaster.R

class SplashActivity : AppCompatActivity() {
    override  fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Simulate loading process
        Handler(Looper.getMainLooper()).postDelayed({
            // Start the main activity after the splash screen
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000) // Delay in milliseconds (e.g., 3000ms = 3 seconds)
    }
}