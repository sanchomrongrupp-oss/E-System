package com.example.e_system

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity


class introl_first : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introl_first)

        //Button Next to secound screen
        val btnnexttosecound = findViewById<ImageButton?>(R.id.btnnextintrol1)
        btnnexttosecound?.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(this@introl_first, introl_second::class.java)
            startActivity(intent)
        })
    }
}