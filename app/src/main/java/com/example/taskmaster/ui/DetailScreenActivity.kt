package com.example.taskmaster.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import com.example.taskmaster.R

class DetailScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_screen)

        val editText: EditText = findViewById(R.id.editText)
        val checkboxContainer: LinearLayout = findViewById(R.id.checkboxContainer)

        editText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                val taskText = editText.text.toString()
                if (taskText.isNotEmpty()) {
                    addNewCheckbox(checkboxContainer, taskText)
                    editText.text.clear()
                }
                true
            } else {
                false
            }
        }
    }

    private fun addNewCheckbox(container: LinearLayout, text: String) {
        // Inflate a new layout from XML
        val inflater = LayoutInflater.from(this)
        val newCheckboxLayout = inflater.inflate(R.layout.checkbox_layout, container, false) as LinearLayout

        // Find the CheckBox and EditText in the new layout
        val newCheckbox = newCheckboxLayout.findViewById<CheckBox>(R.id.checkbox)
        val newEditText = newCheckboxLayout.findViewById<EditText>(R.id.editText)

        // Set the text for the new EditText
        newEditText.setText(text)

        // Set an action listener for the new EditText
        newEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                newEditText.clearFocus()
                true
            } else {
                false
            }
        }

        // Add the new layout to the container
        container.addView(newCheckboxLayout)
    }
}