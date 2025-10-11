package com.example.e_system

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class introl_second : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_introl_second)

        //Button Next to three screen
        val btnnexttothree = findViewById<ImageButton?>(R.id.btnnextintrol2)
        btnnexttothree?.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(this@introl_second, introl_three::class.java)
            startActivity(intent)
        })
    }
}