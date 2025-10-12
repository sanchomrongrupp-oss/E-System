package com.example.e_system

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
class signin : AppCompatActivity() {
    private var isPasswordVisible = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signin)

        val passwordEditText = findViewById<EditText>(R.id.passwordInput)
        val ivToggle = findViewById<ImageView>(R.id.ivTogglePassword)

        var isPasswordVisible = false

        ivToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                // Show password
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ivToggle.setImageResource(R.drawable.hide) // ✅ changed drawable
            } else {
                // Hide password
                passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                ivToggle.setImageResource(R.drawable.view) // show icon
            }

            // Keep cursor at the end
            passwordEditText.setSelection(passwordEditText.text.length)
        }
        val btnloginhomepage = findViewById<LinearLayout?>(R.id.btnlogin)
        btnloginhomepage?.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(this@signin, home_page::class.java)
            startActivity(intent)
        })

    }
}