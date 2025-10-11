package com.example.e_system

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        //Button Getstarted
        val btngetstart = findViewById<Button>(R.id.btngetstart)
            btngetstart.setOnClickListener {
                val intent = Intent(this, introl_first::class.java)
                startActivity(intent)
            }
    }

}