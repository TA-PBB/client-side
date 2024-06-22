package com.example.taskmaster.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.taskmaster.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val username = intent.getStringExtra("USERNAME") ?: "User"
        val greetingTextView: TextView = findViewById(R.id.greetingTextView)
        greetingTextView.text = "Hi, $username"

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            // Memulai NewActivity
            val intent = Intent(this, DetailScreenActivity::class.java)
            startActivity(intent)
        }
    }
}
